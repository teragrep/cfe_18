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
import com.teragrep.cfe18.handlers.entities.Cfe04Transform;
import com.teragrep.cfe18.handlers.entities.Storage;
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
import org.springframework.context.annotation.Description;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MigrateDatabaseExtension.class)
public class Cfe04TransformControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    @Description("Tests successful add of cfe_04 transform")
    public void testAddCfe04Transforms() {

        // Insert base cfe_04 storage first
        Storage storage = new Storage();
        storage.setStorageType(Storage.StorageType.CFE_04);
        storage.setStorageName("cfe_04");
        String json2 = gson.toJson(storage);

        // forms the json to requestEntity
        StringEntity requestEntity2 = new StringEntity(String.valueOf(json2), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request2 = new HttpPut("http://localhost:" + port + "/storage");
        // set requestEntity to the put request
        request2.setEntity(requestEntity2);
        // Header
        request2.setHeader("Authorization", "Bearer " + token);

        Assertions.assertDoesNotThrow(() -> {
            HttpClientBuilder.create().build().execute(request2);
        });

        Cfe04Transform cfe04Transform = new Cfe04Transform();
        cfe04Transform.setCfe04Id(1);
        cfe04Transform.setName("transform1");
        cfe04Transform.setWriteMeta(true);
        cfe04Transform.setWriteDefault(true);
        cfe04Transform.setDefaultValue("default");
        cfe04Transform.setDestinationKey("destKey");
        cfe04Transform.setRegex("regex");
        cfe04Transform.setFormat("format");
        String json = gson.toJson(cfe04Transform);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(String.valueOf(json), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/storage/cfe04/transforms");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        HttpResponse response = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        // Get the entity from response
        HttpEntity entity = response.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New cfe_04 transforms created";
        int expectedId = 1;
        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());
        int actualId = Assertions.assertDoesNotThrow(() -> responseAsJson.getInt("id"));

        // Assertions
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED));
        assertEquals(expectedId, actualId);
        assertEquals(expected, actual);

    }

    @Test
    @Order(2)
    @Description("Tests adding cfe_04 transform for a cfe_04 that does not exist")
    public void testAddCfe04TransformsMissingCfe04() {
        Cfe04Transform cfe04Transform = new Cfe04Transform();
        cfe04Transform.setCfe04Id(500);
        cfe04Transform.setName("transform1");
        cfe04Transform.setWriteMeta(true);
        cfe04Transform.setWriteDefault(true);
        cfe04Transform.setDefaultValue("default");
        cfe04Transform.setDestinationKey("destKey");
        cfe04Transform.setRegex("regex");
        cfe04Transform.setFormat("format");
        String json = gson.toJson(cfe04Transform);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(String.valueOf(json), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/storage/cfe04/transforms");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        HttpResponse response = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        // Get the entity from response
        HttpEntity entity = response.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "No such cfe_04 id";
        int expectedId = 0;

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());
        int actualId = Assertions.assertDoesNotThrow(() -> responseAsJson.getInt("id"));

        // Assertions
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals(expectedId, actualId);
        assertEquals(expected, actual);
    }

    @Test
    @Order(3)
    @Description("Tests that ALL cfe_04 transforms can be fetched")
    public void testGetALLCfe04Transforms() {

        Cfe04Transform cfe04Transform2 = new Cfe04Transform();
        cfe04Transform2.setId(1);
        cfe04Transform2.setCfe04Id(1);
        cfe04Transform2.setName("transform1");
        cfe04Transform2.setWriteMeta(true);
        cfe04Transform2.setWriteDefault(true);
        cfe04Transform2.setDefaultValue("default");
        cfe04Transform2.setDestinationKey("destKey");
        cfe04Transform2.setRegex("regex");
        cfe04Transform2.setFormat("format");

        Cfe04Transform cfe04Transform = new Cfe04Transform();
        cfe04Transform.setId(3);
        cfe04Transform.setCfe04Id(1);
        cfe04Transform.setName("transform2");
        cfe04Transform.setWriteMeta(false);
        cfe04Transform.setWriteDefault(true);
        cfe04Transform.setDefaultValue("default");
        cfe04Transform.setDestinationKey("destKey");
        cfe04Transform.setRegex("regex");
        cfe04Transform.setFormat("format");
        String json = gson.toJson(cfe04Transform);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(String.valueOf(json), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/storage/cfe04/transforms");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        ArrayList<Cfe04Transform> expected = new ArrayList<>();
        expected.add(cfe04Transform2);
        expected.add(cfe04Transform);

        String json2 = gson.toJson(expected);
        // Fetching all capture metas
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/storage/cfe04/transforms");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        // Assertions
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertEquals(json2, responseStringGet);

    }

    @Test
    @Order(4)
    @Description(
        "Tests that endpoint is idempotent. First unit test adds same transform as this one. Should return same output"
    )
    public void testIdempotentCfe04Transforms() {
        Cfe04Transform cfe04Transform = new Cfe04Transform();
        cfe04Transform.setCfe04Id(1);
        cfe04Transform.setName("transform1");
        cfe04Transform.setWriteMeta(true);
        cfe04Transform.setWriteDefault(true);
        cfe04Transform.setDefaultValue("default");
        cfe04Transform.setDestinationKey("destKey");
        cfe04Transform.setRegex("regex");
        cfe04Transform.setFormat("format");
        String json = gson.toJson(cfe04Transform);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(String.valueOf(json), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/storage/cfe04/transforms");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        HttpResponse response = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        // Get the entity from response
        HttpEntity entity = response.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New cfe_04 transforms created";
        int expectedId = 1;

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());
        int actualId = Assertions.assertDoesNotThrow(() -> responseAsJson.getInt("id"));

        // Assertions
        assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED));
        assertEquals(expectedId, actualId);
        assertEquals(expected, actual);

    }

    @Test
    @Order(5)
    @Description("Tests delete endpoint successfully")
    public void testDeleteCfe04Transforms() {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/storage/cfe04/transforms/" + 1);

        // Header
        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());
        int actualId = Assertions.assertDoesNotThrow(() -> responseAsJson.getInt("id"));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "cfe_04 transforms with id of 1 deleted.";
        int expectedId = 1;

        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertEquals(expectedId, actualId);
        assertEquals(expected, actual);

    }

    @Test
    @Order(6)
    @Description("Tests that records for one Cfe_04 can successfully be fetched")
    public void testGetCfe04TransformsForOneCfe04() {
        ArrayList<Cfe04Transform> cfe04TransformList = new ArrayList<>();
        Cfe04Transform cfe04Transform = new Cfe04Transform();
        cfe04Transform.setId(3);
        cfe04Transform.setCfe04Id(1);
        cfe04Transform.setName("transform2");
        cfe04Transform.setWriteMeta(false);
        cfe04Transform.setWriteDefault(true);
        cfe04Transform.setDefaultValue("default");
        cfe04Transform.setDestinationKey("destKey");
        cfe04Transform.setRegex("regex");
        cfe04Transform.setFormat("format");
        cfe04TransformList.add(cfe04Transform);
        String expectedJson = gson.toJson(cfe04TransformList);

        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/storage/cfe04/transforms/" + 1);

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        // Assertions
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertEquals(expectedJson, responseStringGet);
    }

    @Test
    @Order(7)
    @Description("Tests that something cant be deleted if does not exist")
    public void testDeleteCfe04TransformsInvalidCfe04() {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/storage/cfe04/transforms/" + 12222);

        // Header
        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Record does not exist";

        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals(expected, actual);

    }

}
