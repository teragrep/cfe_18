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
import com.teragrep.cfe18.handlers.entities.CaptureGroup;
import com.teragrep.cfe18.handlers.entities.Flow;
import com.teragrep.cfe18.handlers.entities.Sink;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MigrateDatabaseExtension.class)
public class CaptureGroupControllerTest extends TestSpringBootInformation {


    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @BeforeAll
    public void testData() throws Exception {
        // add flow
        Flow flow = new Flow();
        flow.setName("capflow");
        String json2 = gson.toJson(flow);

        // forms the json to requestEntity
        StringEntity requestEntity2 = new StringEntity(
                String.valueOf(json2),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request2 = new HttpPut("http://localhost:" + port + "/flow");
        // set requestEntity to the put request
        request2.setEntity(requestEntity2);
        // Header
        request2.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse2 = HttpClientBuilder.create().build().execute(request2);

        // Get the entity from response
        HttpEntity entity2 = httpResponse2.getEntity();

        // Entity response string
        String response2 = EntityUtils.toString(entity2);

        // Parsing response as JSONObject
        JSONObject responseJson2 = new JSONObject(response2);

        String expected2 = "New flow created";

        // Creating string from Json that was given as a response
        String actual2 = responseJson2.get("message").toString();

        assertEquals(expected2, actual2);
        assertThat(
                httpResponse2.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));
    }


    @Test
    @Order(1)
    public void testSelectAllEmpty() {
        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/group");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        List<CaptureGroup> expectedCaptureGroups = new ArrayList<>();

        String expected = gson.toJson(expectedCaptureGroups);

        assertEquals(expected, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(2)
    public void testCreateCaptureGroupInvalidFlow() {

        // Capture Group
        CaptureGroup captureGroup = new CaptureGroup();
        captureGroup.setCaptureGroupName("groupRelp");
        captureGroup.setCaptureGroupType(CaptureGroup.groupType.relp);
        captureGroup.setFlowId(555);

        String cgJson = gson.toJson(captureGroup);

        // forms the json to requestEntity
        StringEntity requestEntityCaptureGroup = new StringEntity(
                String.valueOf(cgJson),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/capture/group");
        // set requestEntity to the put request
        requestCaptureGroup.setEntity(requestEntityCaptureGroup);
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup));

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Record does not exist";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(
                HttpStatus.SC_NOT_FOUND,
                httpResponse.getStatusLine().getStatusCode());
        assertEquals(expected, actual);
    }

    @Test
    @Order(3)
    public void testCreateCaptureGroup() {

        // Capture Group
        CaptureGroup captureGroup = new CaptureGroup();
        captureGroup.setCaptureGroupName("groupRelp");
        captureGroup.setCaptureGroupType(CaptureGroup.groupType.relp);
        captureGroup.setFlowId(1);

        String cgJson = gson.toJson(captureGroup);

        // forms the json to requestEntity
        StringEntity requestEntityCaptureGroup = new StringEntity(
                String.valueOf(cgJson),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/capture/group");
        // set requestEntity to the put request
        requestCaptureGroup.setEntity(requestEntityCaptureGroup);
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup));

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New capture group created";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(
                HttpStatus.SC_CREATED,
                httpResponse.getStatusLine().getStatusCode());
        assertEquals(expected, actual);
    }

    @Test
    @Order(4)
    public void testSelectCaptureGroup() {
        CaptureGroup captureGroup = new CaptureGroup();
        captureGroup.setCaptureGroupType(CaptureGroup.groupType.relp);
        captureGroup.setId(1);
        captureGroup.setCaptureGroupName("groupRelp");
        captureGroup.setFlowId(1);

        String expectedJson = gson.toJson(captureGroup);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/group/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(expectedJson, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());

    }

    @Test
    @Order(5)
    public void testSelectAllCaptureGroups() {

        // Capture Group 2
        CaptureGroup captureGroup2 = new CaptureGroup();
        captureGroup2.setId(2);
        captureGroup2.setCaptureGroupName("groupRelp2");
        captureGroup2.setCaptureGroupType(CaptureGroup.groupType.relp);
        captureGroup2.setFlowId(1);

        String cgJson = gson.toJson(captureGroup2);

        // forms the json to requestEntity
        StringEntity requestEntityCaptureGroup = new StringEntity(
                String.valueOf(cgJson),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/capture/group");
        // set requestEntity to the put request
        requestCaptureGroup.setEntity(requestEntityCaptureGroup);
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup));


        ArrayList<CaptureGroup> expected = new ArrayList<>();
        CaptureGroup captureGroup = new CaptureGroup();
        captureGroup.setId(1);
        captureGroup.setCaptureGroupName("groupRelp");
        captureGroup.setCaptureGroupType(CaptureGroup.groupType.relp);
        captureGroup.setFlowId(1);


        expected.add(captureGroup);
        expected.add(captureGroup2);

        String expectedJson = gson.toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/group");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(expectedJson, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());

    }

    @Test
    @Order(6)
    public void testDeleteNonExistentCaptureGroup() {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/capture/group/125412");

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
    public void testDeleteCaptureGroup() {
        //groupRelp
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/capture/group/1");

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
        String expected = "Capture group deleted";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_OK, deleteResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(8)
    public void testSelectNonExistentCaptureGroup() {
        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/group/112233");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.getString("message"));

        String expected = "Record does not exist";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_NOT_FOUND, responseGet.getStatusLine().getStatusCode());

    }


}
