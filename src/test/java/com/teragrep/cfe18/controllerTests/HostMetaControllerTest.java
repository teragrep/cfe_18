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
import com.teragrep.cfe18.handlers.entities.HostMeta;
import com.teragrep.cfe18.handlers.entities.HostRelp;
import com.teragrep.cfe18.handlers.entities.Hub;
import com.teragrep.cfe18.handlers.entities.InterfaceType;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MigrateDatabaseExtension.class)
public class HostMetaControllerTest extends TestSpringBootInformation {


    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @BeforeAll
    public void testData() {
        // Insert two hosts first for testing
        HostRelp host = new HostRelp();
        host.setMd5("md5Testing");
        host.setFqHost("fqhostTesting");

        String json1 = gson.toJson(host);

        // forms the json to requestEntity
        StringEntity requestEntity1 = new StringEntity(
                String.valueOf(json1),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request1 = new HttpPut("http://localhost:" + port + "/host/relp");
        // set requestEntity to the put request
        request1.setEntity(requestEntity1);
        // Header
        request1.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request1));

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New host created with relp type";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        HostRelp host2 = new HostRelp();
        host2.setMd5("md5Testing2");
        host2.setFqHost("fqhostTesting2");

        String json2 = gson.toJson(host2);

        // forms the json to requestEntity
        StringEntity requestEntity2 = new StringEntity(
                String.valueOf(json2),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request2 = new HttpPut("http://localhost:" + port + "/host/relp");
        // set requestEntity to the put request
        request2.setEntity(requestEntity2);
        // Header
        request2.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse2 = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request2));

        // Get the entity from response
        HttpEntity entity2 = httpResponse2.getEntity();

        // Entity response string
        String responseString2 = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity2));

        // Parsing response as JSONObject
        JSONObject responseAsJson2 = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString2));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected2 = "New host created with relp type";

        // Creating string from Json that was given as a response
        String actual2 = Assertions.assertDoesNotThrow(() -> responseAsJson2.get("message").toString());

        // Assertions
        assertEquals(expected, actual);
        assertEquals(
                HttpStatus.SC_CREATED,
                httpResponse.getStatusLine().getStatusCode());

        assertEquals(expected2, actual2);
        assertEquals(
                HttpStatus.SC_CREATED,
                httpResponse2.getStatusLine().getStatusCode());
    }

    @Test
    @Order(1)
    public void testSelectAllHostMetaEmpty() {
        List<HostMeta> hostMetaList = new ArrayList<>();

        String expectedJson = gson.toJson(hostMetaList);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/host/meta");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() ->HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() ->EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(expectedJson, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(2)
    public void testInsertHostMeta() {
        HostMeta hostMeta = new HostMeta();
        hostMeta.setHostId(1);
        hostMeta.setReleaseVersion("1.0.0");
        hostMeta.setArch("arch");
        hostMeta.setHostname("hostname");
        hostMeta.setOs("PinguinOs");
        hostMeta.setFlavor("Vanilla");

        String json = gson.toJson(hostMeta);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(
                String.valueOf(json),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/host/meta");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New host meta created";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(expected, actual);
        assertEquals(
                HttpStatus.SC_CREATED,
                httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(3)
    public void testGetHostMeta() {
        HostMeta hostMeta = new HostMeta();
        hostMeta.setId(1);
        hostMeta.setHostId(1);
        hostMeta.setReleaseVersion("1.0.0");
        hostMeta.setArch("arch");
        hostMeta.setHostname("hostname");
        hostMeta.setOs("PinguinOs");
        hostMeta.setFlavor("Vanilla");
        String json = gson.toJson(hostMeta);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/host/meta/" + 1);

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(json, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    // Get with invalid ID
    @Test
    @Order(4)
    public void testGetHostMetaInvalidId() {
        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/host/meta/" + 112233);

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Record does not exist";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_NOT_FOUND, responseGet.getStatusLine().getStatusCode());
    }


    // Get ALL
    @Test
    @Order(5)
    public void testGetAllHostMeta() {


        List<HostMeta> expectedListFile = new ArrayList<>();
        HostMeta hostMeta = new HostMeta();
        hostMeta.setId(1);
        hostMeta.setHostId(1);
        hostMeta.setReleaseVersion("1.0.0");
        hostMeta.setArch("arch");
        hostMeta.setHostname("hostname");
        hostMeta.setOs("PinguinOs");
        hostMeta.setFlavor("Vanilla");

        // insert another HostMeta to test GetALL properly
        HostMeta hostMeta2 = new HostMeta();
        hostMeta2.setId(2);
        hostMeta2.setHostId(2);
        hostMeta2.setReleaseVersion("2.0.0");
        hostMeta2.setArch("arch2");
        hostMeta2.setHostname("hostname2");
        hostMeta2.setOs("PinguinOs2.0");
        hostMeta2.setFlavor("Chocolate");

        String json = gson.toJson(hostMeta2);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(
                String.valueOf(json),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/host/meta");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        expectedListFile.add(hostMeta);
        expectedListFile.add(hostMeta2);

        String expectedJson = gson.toJson(expectedListFile);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/host/meta");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(expectedJson, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    // Delete with invalid ID
    @Test
    @Order(6)
    public void testDeleteNonExistentHostMeta() {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/meta/" + 112233);

        // Header
        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Record does not exist";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_NOT_FOUND, deleteResponse.getStatusLine().getStatusCode());
    }


    @Test
    @Order(7)
    public void testDeleteHostMeta() {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/meta/" + 1);

        // Header
        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Hostmeta deleted";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_OK, deleteResponse.getStatusLine().getStatusCode());
    }

}
