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
import com.teragrep.cfe18.handlers.entities.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
public class CaptureDefinitionControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @BeforeAll
    public void testData() {
        // add flow and sink

        Flow flow = new Flow();
        flow.setName("capflow");
        String flowJson = gson.toJson(flow);

        // forms the json to requestEntity
        StringEntity flowRequest = new StringEntity(String.valueOf(flowJson), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut flowAsRequest = new HttpPut("http://localhost:" + port + "/flow");
        // set requestEntity to the put request
        flowAsRequest.setEntity(flowRequest);
        // Header
        flowAsRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse flowResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(flowAsRequest));

        // Get the entity from response
        HttpEntity flowAsResponseEntity = flowResponse.getEntity();

        // Entity response string
        String flowAsResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(flowAsResponseEntity));

        // Parsing response as JSONObject
        JSONObject flowAsJsonResponse = Assertions.assertDoesNotThrow(() -> new JSONObject(flowAsResponse));

        String expectedFlow = "New flow created";

        // Creating string from Json that was given as a response
        String actualFlow = Assertions.assertDoesNotThrow(() -> flowAsJsonResponse.get("message").toString());

        // insert sink

        Sink sink = new Sink();
        sink.setFlowId(1);
        sink.setPort("cap");
        sink.setIpAddress("capsink");
        sink.setProtocol("prot");

        String sinkJson = gson.toJson(sink);

        // forms the json to requestEntity
        StringEntity sinkRequest = new StringEntity(String.valueOf(sinkJson), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut sinkAsRequest = new HttpPut("http://localhost:" + port + "/sink");
        // set requestEntity to the put request
        sinkAsRequest.setEntity(sinkRequest);
        // Header
        sinkAsRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse sinkResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(sinkAsRequest));

        // Get the entity from response
        HttpEntity sinkResponseEntity = sinkResponse.getEntity();

        // Entity response string
        String sinkAsResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(sinkResponseEntity));

        // Parsing response as JSONObject
        JSONObject sinkAsJsonObject = Assertions.assertDoesNotThrow(() -> new JSONObject(sinkAsResponse));

        String expectedSink = "New sink created";

        // Creating string from Json that was given as a response
        String actualSink = Assertions.assertDoesNotThrow(() -> sinkAsJsonObject.get("message").toString());

        // Assertions
        assertEquals(expectedFlow, actualFlow);
        assertEquals(HttpStatus.SC_CREATED, flowResponse.getStatusLine().getStatusCode());
        assertEquals(expectedSink, actualSink);
        assertEquals(HttpStatus.SC_CREATED, sinkResponse.getStatusLine().getStatusCode());

    }

    @Test
    @Order(1)
    public void testSelectAllEmpty() {
        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/definitions");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        List<CaptureDefinition> expectedCaptures = new ArrayList<>();

        assertEquals(expectedCaptures.toString(), responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(2)
    public void testSelectInvalidId() {
        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/definitions/67");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        String expected = "Record does not exist";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_NOT_FOUND, responseGet.getStatusLine().getStatusCode());

    }

    @Test
    @Order(3)
    public void testSelectAll() {
        CaptureRelp captureRelp = new CaptureRelp();
        captureRelp.setTag("relpTag");
        captureRelp.setRetentionTime("P30D");
        captureRelp.setCategory("audit");
        captureRelp.setApplication("relp");
        captureRelp.setIndex("audit_relp");
        captureRelp.setSourceType("relpsource1");
        captureRelp.setProtocol("prot");
        captureRelp.setFlow("capFlow");

        String captureJson = gson.toJson(captureRelp);

        // forms the json to requestEntity
        StringEntity captureRequest = new StringEntity(String.valueOf(captureJson), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut captureAsRequest = new HttpPut("http://localhost:" + port + "/v2/captures/definitions/relp-streams");
        // set requestEntity to the put request
        captureAsRequest.setEntity(captureRequest);
        // Header
        captureAsRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse captureResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(captureAsRequest));

        // Get the entity from response
        HttpEntity captureResponseEntity = captureResponse.getEntity();

        // Entity response string
        String captureAsResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(captureResponseEntity));

        // Parsing response as JSONObject
        JSONObject captureAsJsonObject = Assertions.assertDoesNotThrow(() -> new JSONObject(captureAsResponse));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedCapture = "New capture created";

        // Creating string from Json that was given as a response
        String actualCapture = Assertions.assertDoesNotThrow(() -> captureAsJsonObject.get("message").toString());

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/definitions");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        List<CaptureDefinition> expectedCaptures = new ArrayList<>();

        CaptureDefinition captureDefinition = new CaptureDefinition();
        captureDefinition.setId(1);
        captureDefinition.setTag("relpTag");
        captureDefinition.setCaptureIndex("audit_relp");
        captureDefinition.setApplication("relp");
        captureDefinition.setSourcetype("relpsource1");

        expectedCaptures.add(captureDefinition);

        String expected = gson.toJson(expectedCaptures);

        assertEquals(expectedCapture, actualCapture);
        assertEquals(HttpStatus.SC_CREATED, captureResponse.getStatusLine().getStatusCode());
        assertEquals(expected, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());

    }

    @Test
    @Order(4)
    public void testSelectValidIdCapture() {

        CaptureDefinition captureDefinition = new CaptureDefinition();
        captureDefinition.setId(1);
        captureDefinition.setTag("relpTag");
        captureDefinition.setCaptureIndex("audit_relp");
        captureDefinition.setApplication("relp");
        captureDefinition.setSourcetype("relpsource1");

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/definitions/" + 1);

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        String expected = captureDefinition.toString();

        assertEquals(expected, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());

    }

    @Test
    @Order(5)
    public void testSelectValidCaptureWithKey() {
        CaptureMeta captureMeta = new CaptureMeta();
        captureMeta.setCapture_id(1);
        captureMeta.setCapture_meta_key("relpKey1");
        captureMeta.setCapture_meta_value("relpValue1");

        String captureMetaJson = gson.toJson(captureMeta);

        // forms the json to requestEntity
        StringEntity requestEntityCaptureMeta = new StringEntity(
                String.valueOf(captureMetaJson),
                ContentType.APPLICATION_JSON
        );

        // Creates the request
        HttpPut captureMetaRequest = new HttpPut("http://localhost:" + port + "/capture/meta/");
        // set requestEntity to the put request
        captureMetaRequest.setEntity(requestEntityCaptureMeta);
        // Header
        captureMetaRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint

        HttpResponse captureMetaResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(captureMetaRequest));

        // Get the entity from response
        HttpEntity captureMetaResponseEntity = captureMetaResponse.getEntity();

        // Entity response string
        String captureMetaAsResponse = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(captureMetaResponseEntity));

        // Parsin respponse as JSONObject
        JSONObject captureMetaAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(captureMetaAsResponse));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedCaptureMeta = "New capture meta created for = 1";

        // Creating string from Json that was given as a response
        String actualCaptureMeta = Assertions.assertDoesNotThrow(() -> captureMetaAsJson.get("message").toString());

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/definitions?key=relpKey1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        List<CaptureDefinition> expectedCaptures = new ArrayList<>();

        CaptureDefinition captureDefinition = new CaptureDefinition();
        captureDefinition.setId(1);
        captureDefinition.setTag("relpTag");
        captureDefinition.setCaptureIndex("audit_relp");
        captureDefinition.setApplication("relp");
        captureDefinition.setSourcetype("relpsource1");

        expectedCaptures.add(captureDefinition);

        String expected = gson.toJson(expectedCaptures);

        assertEquals(expected, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
        assertEquals(expectedCaptureMeta, actualCaptureMeta);
        assertEquals(HttpStatus.SC_CREATED, captureMetaResponse.getStatusLine().getStatusCode());

    }

    @Test
    @Order(6)
    public void testSelectValidCaptureWithInvalidKey() {
        // Asserting get request
        HttpGet requestGet = new HttpGet(
                "http://localhost:" + port + "/v2/captures/definitions?filter.metadata.key=keyThatDoesNotExist"
        );

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        List<CaptureDefinition> expectedCaptures = new ArrayList<>();

        String expected = gson.toJson(expectedCaptures);

        assertEquals(expected, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());

    }

}
