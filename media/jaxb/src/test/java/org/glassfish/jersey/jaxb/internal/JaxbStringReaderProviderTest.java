/*
 * Copyright (c) 2012, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

/*
 * Portions contributed by Joseph Walton (Atlassian)
 */

package org.glassfish.jersey.jaxb.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;

import jakarta.inject.Provider;
import jakarta.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.SAXParserFactory;

import org.glassfish.jersey.jaxb.FeatureSupplier;
import org.glassfish.jersey.model.internal.CommonConfig;
import org.glassfish.jersey.model.internal.ComponentBag;

import org.junit.jupiter.api.Test;

public class JaxbStringReaderProviderTest {

    @Test
    public void stringReaderDoesNotReadExternalDtds() {

        Provider<SAXParserFactory> saxParserFactoryProvider = new Provider<SAXParserFactory>() {
            final SaxParserFactoryInjectionProvider spf = new SaxParserFactoryInjectionProvider(null,
                    new CommonConfig(RuntimeType.SERVER, ComponentBag.INCLUDE_ALL));

            @Override
            public SAXParserFactory get() {
                SAXParserFactory factory = spf.get();

                for (Map.Entry<String, Boolean> entry : FeatureSupplier.allowDoctypeDeclFeature().getFeatures().entrySet()){
                    JaxbFeatureUtil.setProperty(SAXParserFactory.class, entry, factory::setFeature);
                }

                return factory;
            }
        };

        JaxbStringReaderProvider.RootElementProvider provider = new JaxbStringReaderProvider.RootElementProvider(
                saxParserFactoryProvider, new Providers() {
            @Override
            public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type,
                                                                 Type genericType,
                                                                 Annotation[] annotations,
                                                                 MediaType mediaType) {
                return null;
            }

            @Override
            public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type,
                                                                 Type genericType,
                                                                 Annotation[] annotations,
                                                                 MediaType mediaType) {
                return null;
            }

            @Override
            public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type) {
                return null;
            }

            @Override
            public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType) {
                return null;
            }
        });

        String content = "<!DOCTYPE x SYSTEM 'file:///no-such-file'> <rootObject/>";

        provider.getConverter(RootObject.class, null, null).fromString(content);
    }

    @XmlRootElement
    static class RootObject {
    }
}
