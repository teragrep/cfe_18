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
import com.teragrep.cfe18.handlers.entities.Flow;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MigrateDatabaseExtension.class)
public class ApiUtilityControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    // Testing if token works
    @Test
    @Order(1)
    public void testToken() {

        // Given
        HttpUriRequest request = new HttpGet("http://localhost:" + port + "/v2/meta/jwt");
        request.setHeader("Authorization", "Bearer " + token);
        // When

        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        HttpEntity entity = httpResponse.getEntity();

        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity, "UTF-8"));

        // Then
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
        assertEquals("Hello, subject!", responseString);
    }

    // Testing that without token there is no access
    @Test
    @Order(2)
    public void testInvalidToken() {

        // Given
        HttpUriRequest request2 = new HttpGet("http://localhost:" + port + "/v2/meta/jwt");
        request2.setHeader("Authorization", "Bearer noToken");

        // When

        HttpResponse httpResponse2 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request2));

        // Then
        assertEquals(HttpStatus.SC_UNAUTHORIZED, httpResponse2.getStatusLine().getStatusCode());
    }

    // Tests that giving no token returns 500
    @Test
    @Order(3)
    public void testNoToken() {

        // Given
        HttpUriRequest request2 = new HttpGet("http://localhost:" + port + "/v2/meta/jwt");

        // When

        HttpResponse httpResponse2 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request2));

        // Then
        assertEquals(HttpStatus.SC_UNAUTHORIZED, httpResponse2.getStatusLine().getStatusCode());
    }

    @Test
    @Order(4)
    public void testVersionIsNumber() {

        Flow flow = new Flow();
        flow.setName("Testflow");

        String json = gson.toJson(flow);

        // forms the json to requestEntity
        StringEntity flowEntity = new StringEntity(String.valueOf(json), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut flowRequest = new HttpPut("http://localhost:" + port + "/flow");
        // set requestEntity to the put request
        flowRequest.setEntity(flowEntity);
        // Header
        flowRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse flowResponse = assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(flowRequest));

        // Get the entity from response
        HttpEntity flowResponseEntity = flowResponse.getEntity();

        // Entity response string
        String flowAsResponseString = assertDoesNotThrow(() -> EntityUtils.toString(flowResponseEntity));

        // Parsin respponse as JSONObject
        JSONObject flowAsResponseJson = assertDoesNotThrow(() -> new JSONObject(flowAsResponseString));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedFlow = "New flow created";

        // Creating string from Json that was given as a response
        String actualFlow = assertDoesNotThrow(() -> flowAsResponseJson.get("message").toString());

        // Given
        HttpUriRequest request = new HttpGet("http://localhost:" + port + "/v2/meta/data-version");
        request.setHeader("Authorization", "Bearer " + token);
        // When

        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        HttpEntity entity = httpResponse.getEntity();

        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity, "UTF-8"));

        // Validates here that the result is type of Integer
        Integer result = Integer.valueOf(responseString);

        // Then
        // Assertions
        assertEquals(expectedFlow, actualFlow);
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
        // Asserts that the return is not null.
        assertNotNull(result);
    }

    @Test
    @Order(5)
    public void testVersionCanBeFetched() {

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/meta/data-version");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        Integer version = Integer.valueOf(responseStringGet);

        // Asserting get request
        HttpGet requestFlow = new HttpGet("http://localhost:" + port + "/flow?version=" + version);

        requestFlow.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseFlow = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestFlow));

        HttpEntity entityFlow = responseFlow.getEntity();

        String flowAsResponseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityFlow, "UTF-8"));

        ArrayList<Flow> expected = new ArrayList<>();
        Flow flow = new Flow();
        flow.setName("Testflow");
        flow.setId(1);
        expected.add(flow);
        String expectedJson = new Gson().toJson(expected);

        // Assertions
        assertEquals(expectedJson, flowAsResponseString);
        assertEquals(HttpStatus.SC_OK, responseFlow.getStatusLine().getStatusCode());

    }

}
