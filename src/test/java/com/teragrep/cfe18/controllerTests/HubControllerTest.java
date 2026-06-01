/*
 * Integration main data management for Teragrep
 * Copyright (C) 2021  Suomen Kanuuna Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://github.com/teragrep/teragrep/blob/main/LICENSE>.
 *
 *
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Suomen Kanuuna Oy without any additional
 * modifications.
 *
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *
 * Origin of the software must be attributed to Suomen Kanuuna Oy. Any modified
 * versions must be marked as "Modified version of" The Program.
 *
 * Names of the licensors and authors may not be used for publicity purposes.
 *
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *
 * To the extent this program is licensed as part of the Commercial versions of
 * Teragrep, the applicable Commercial License may apply to this file if you as
 * a licensee so wish it.
 */
package com.teragrep.cfe18.controllerTests;

import com.google.gson.Gson;
import com.teragrep.cfe18.handlers.entities.Hub;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MigrateDatabaseExtension.class)
public class HubControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @BeforeAll
    public void testData() {
        TestApiClient testApiClient = new TestApiClient(port, token);
        testApiClient.insertHub("hubfq1", "hubmd52", "hubip2");
        testApiClient.insertHostFile("hostFq", "randommd5value", "hubfq1");
    }

    @Test
    @Order(1)
    public void testInsertHub() {
        Hub hub = new Hub();
        hub.setFqHost("hubfq");
        hub.setMd5("hubmd5");
        hub.setIp("hubip");

        String json = gson.toJson(hub);

        StringEntity requestEntity = new StringEntity(String.valueOf(json), ContentType.APPLICATION_JSON);

        HttpPut request = new HttpPut("http://localhost:" + port + "/v2/host/hub");
        request.setEntity(requestEntity);
        request.setHeader("Authorization", "Bearer " + token);

        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        HttpEntity entity = httpResponse.getEntity();

        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        String expected = "New hub created";

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_CREATED, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(2)
    public void testGetHub() {
        Hub hub = new Hub();
        hub.setHostId(1);
        hub.setFqHost("hubfq1");
        hub.setMd5("hubmd52");
        hub.setIp("hubip2");
        hub.setId(1);

        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/host/hub/" + 1);

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = Assertions.assertDoesNotThrow(() -> responseGet.getEntity());

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(hub.toString(), responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(3)
    public void testGetAllHubs() {
        ArrayList<Hub> expected = new ArrayList<>();

        Hub hubexpected1 = new Hub();
        hubexpected1.setHostId(1);
        hubexpected1.setFqHost("hubfq1");
        hubexpected1.setMd5("hubmd52");
        hubexpected1.setIp("hubip2");
        hubexpected1.setId(1);

        Hub hubexpected2 = new Hub();
        hubexpected2.setHostId(3);
        hubexpected2.setFqHost("hubfq");
        hubexpected2.setMd5("hubmd5");
        hubexpected2.setIp("hubip");
        hubexpected2.setId(2);

        expected.add(hubexpected1);
        expected.add(hubexpected2);

        String json = gson.toJson(expected);

        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/host/hub");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(json, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(4)
    public void testDeleteHubInUse() {

        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/host/hub/" + 1);

        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        String expected = "Is in use";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_CONFLICT, deleteResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(5)
    public void testDeleteNonExistentHub() {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/host/hub/" + 112412214);

        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        String expected = "Record does not exist";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_NOT_FOUND, deleteResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(7)
    public void testDeleteHub() {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/host/hub/" + 2);

        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        String expected = "Hub deleted";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_OK, deleteResponse.getStatusLine().getStatusCode());
    }

}
