/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.jersey.tests.e2e.server;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class Issue3435Test extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(TestResource.class);
    }

    @Test
    public void emptyStringQueryParamReturnsListWithOneNullElementUUID() {
        Response response = target("test/UUID")
                .queryParam("list", "")
                .request()
                .get();
            assertEquals(200, response.getStatus());
            assertEquals("0: []", response.readEntity(String.class));
    }

    @Test
    public void emptyStringQueryParamReturnsListWithOneNullElementUUIDDefault() {
        Response response = target("test/UUIDdefault")
                .queryParam("list", "")
                .request()
                .get();
            assertEquals(200, response.getStatus());
            assertEquals("1: [ec0cf621-d744-4a1c-b1d8-4b8a44b3dad7]", response.readEntity(String.class));
    }

    @Test
    public void emptyStringQueryParamReturnsListWithOneNullElementString() {
        Response response = target("test/string")
                .queryParam("list", "")
                .request()
                .get();
            assertEquals(200, response.getStatus());
            assertEquals("1: []", response.readEntity(String.class));
    }

    @Test
    public void missingQueryParamReturnsEmptyList() {
        missingQueryParamReturnsEmptyListChecks("test/string");
        missingQueryParamReturnsEmptyListChecks("test/UUID");
    }

    private void missingQueryParamReturnsEmptyListChecks(String path) {
        Response response = target(path)
                .request()
                .get();
            assertEquals(200, response.getStatus());
            assertEquals("0: []", response.readEntity(String.class));
    }

    @Test
    public void filledQueryParamReturnsListWithOneElement() {
        filledQueryParamReturnsListWithOneElementChecks("test/string");
        filledQueryParamReturnsListWithOneElementChecks("test/UUID");
    }

    private void filledQueryParamReturnsListWithOneElementChecks(String path) {
        Response response = target(path)
                .queryParam("list", "ec0cf621-d744-4a1c-b1d8-4b8a44b3dad7")
                .request()
                .get();
            assertEquals(200, response.getStatus());
            assertEquals("1: [ec0cf621-d744-4a1c-b1d8-4b8a44b3dad7]", response.readEntity(String.class));
    }

    @Path("test")
    public static class TestResource {
        @GET
        @Path("UUID")
        public String testUUID(@QueryParam("list") List<UUID> list) {
            return list.size() + ": " + list.toString();
        }

        @GET
        @Path("UUIDdefault")
        public String testUUIDDefault(@DefaultValue("ec0cf621-d744-4a1c-b1d8-4b8a44b3dad7") @QueryParam("list") List<UUID> list) {
            return list.size() + ": " + list.toString();
        }

        @GET
        @Path("string")
        public String testString(@QueryParam("list") List<String> list) {
            return list.size() + ": " + list.toString();
        }
    }
}
