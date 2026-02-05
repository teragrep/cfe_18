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
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
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
public class CaptureGroupLinkageControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @BeforeAll
    public void testData() {
        // add flow
        Flow flow = new Flow();
        flow.setName("capflow");
        String flowJson = gson.toJson(flow);

        // forms the json to requestEntity
        StringEntity flowRequestEntity = new StringEntity(String.valueOf(flowJson), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut flowRequest = new HttpPut("http://localhost:" + port + "/flow");
        // set requestEntity to the put request
        flowRequest.setEntity(flowRequestEntity);
        // Header
        flowRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse flowResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(flowRequest));

        // Get the entity from response
        HttpEntity flowEntity = flowResponse.getEntity();

        // Entity response string
        String flowAsResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(flowEntity));

        // Parsing response as JSONObject
        JSONObject flowAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(flowAsResponse));

        // Creating string from Json that was given as a response
        String flowAsActual = Assertions.assertDoesNotThrow(() -> flowAsJson.get("message").toString());

        String flowAsExpected = "New flow created";

        // Capture Group
        CaptureGroups captureGroups = new CaptureGroups();
        captureGroups.setCaptureGroupName("groupRelp");
        captureGroups.setCaptureGroupType(IntegrationType.RELP);
        captureGroups.setFlowId(1);

        String groupJson = gson.toJson(captureGroups);

        // forms the json to requestEntity
        StringEntity captureGroupRequestEntity = new StringEntity(
                String.valueOf(groupJson),
                ContentType.APPLICATION_JSON
        );

        // Creates the request
        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/v2/captures/group");
        // set requestEntity to the put request
        requestCaptureGroup.setEntity(captureGroupRequestEntity);
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse captureGroupResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup));

        // Get the entity from response
        HttpEntity captureGroupEntity = captureGroupResponse.getEntity();

        // Entity response string
        String captureGroupAsResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(captureGroupEntity));

        // Parsing response as JSONObject
        JSONObject captureGroupAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(captureGroupAsResponse));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String captureGroupAsExpected = "New capture group created";

        // Creating string from Json that was given as a response
        String captureGroupAsActual = Assertions.assertDoesNotThrow(() -> captureGroupAsJson.get("message").toString());

        // insert sink
        Sink sink = new Sink();
        sink.setFlowId(1);
        sink.setPort("cap");
        sink.setIpAddress("capsink");
        sink.setProtocol("prot");

        String sinkJson = gson.toJson(sink);

        // forms the json to requestEntity
        StringEntity sinkEntity = new StringEntity(String.valueOf(sinkJson), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut sinkRequest = new HttpPut("http://localhost:" + port + "/sink");
        // set requestEntity to the put request
        sinkRequest.setEntity(sinkEntity);
        // Header
        sinkRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse sinkResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(sinkRequest));

        // Get the entity from response
        HttpEntity sinkAsEntity = sinkResponse.getEntity();

        // Entity response string
        String sinkAsResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(sinkAsEntity));

        // Parsing response as JSONObject
        JSONObject sinkAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(sinkAsResponse));

        String sinkAsExpected = "New sink created";

        // Creating string from Json that was given as a response
        String sinkAsActual = Assertions.assertDoesNotThrow(() -> sinkAsJson.get("message").toString());

        // insert capture
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
        StringEntity captureEntity = new StringEntity(String.valueOf(captureJson), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut captureRequest = new HttpPut("http://localhost:" + port + "/capture/relp");
        // set requestEntity to the put request
        captureRequest.setEntity(captureEntity);
        // Header
        captureRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse captureResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(captureRequest));

        // Get the entity from response
        HttpEntity captureAsEntity = captureResponse.getEntity();

        // Entity response string
        String captureAsResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(captureAsEntity));

        // Parsing response as JSONObject
        JSONObject captureAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(captureAsResponse));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String captureAsExpected = "New capture created";

        // Creating string from Json that was given as a response
        String captureAsActual = Assertions.assertDoesNotThrow(() -> captureAsJson.get("message").toString());

        // Assertions
        assertEquals(flowAsExpected, flowAsActual);
        assertEquals(HttpStatus.SC_CREATED, flowResponse.getStatusLine().getStatusCode());
        assertEquals(captureGroupAsExpected, captureGroupAsActual);
        assertEquals(HttpStatus.SC_CREATED, captureGroupResponse.getStatusLine().getStatusCode());
        assertEquals(sinkAsExpected, sinkAsActual);
        assertEquals(HttpStatus.SC_CREATED, sinkResponse.getStatusLine().getStatusCode());
        assertEquals(captureAsExpected, captureAsActual);
        assertEquals(HttpStatus.SC_CREATED, captureResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(1)
    public void testSelectAllEmpty() {
        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/group/capture");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        List<CaptureGroups> expectedCaptureGroups = new ArrayList<>();

        String expected = gson.toJson(expectedCaptureGroups);

        assertEquals(expected, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(2)
    public void testSelectEmptyCaptures() {
        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/group/capture/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.getString("message"));

        String expected = "Record does not exist";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_NOT_FOUND, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(3)
    public void testSelectEmptyGroups() {
        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/group/capture/groups/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.getString("message"));

        String expected = "Record does not exist";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_NOT_FOUND, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(4)
    public void testCreateLinkInvalidCapture() {

        // Creates the request
        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/v2/captures/group/capture/1/67");
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup));

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Capture does not exist";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
        assertEquals(expected, actual);
    }

    @Test
    @Order(5)
    public void testCreateLinkInvalidGroup() {

        // Creates the request
        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/v2/captures/group/capture/67/1");
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup));

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Group does not exist";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
        assertEquals(expected, actual);
    }

    @Test
    @Order(6)
    public void testInsertWrongTypeCaptureToGroup() {
        // Filecapturemeta
        FileProcessing file = new FileProcessing();
        file.setInputtype(InputType.REGEX);
        file.setInputvalue("capregex");
        file.setRuleset("capruleset");
        file.setName("capname");
        file.setTemplate("regex.moustache");

        String json = gson.toJson(file);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(String.valueOf(json), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/file/capture/meta/rule");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponseFileId = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        // Get the entity from response
        HttpEntity entityFileId = httpResponseFileId.getEntity();

        // Entity response string
        String responseFileId = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityFileId));

        // Parsing response as JSONObject
        JSONObject responseJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseFileId));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected1 = "New file processing type created";

        // Creating string from Json that was given as a response
        String actual1 = Assertions.assertDoesNotThrow(() -> responseJson.get("message").toString());

        // CaptureFile
        CaptureFile captureFile = new CaptureFile();
        captureFile.setTag("f466e5a4-tagpath1");
        captureFile.setRetentionTime("P30D");
        captureFile.setCategory("audit");
        captureFile.setApplication("app1");
        captureFile.setIndex("app1_audit");
        captureFile.setSourceType("sourcetype1");
        captureFile.setProtocol("prot");
        captureFile.setFlow("capflow");
        captureFile.setTagPath("tagpath1");
        captureFile.setCapturePath("capturepath1");
        captureFile.setFileProcessingTypeId(1);

        String jsonCapture = gson.toJson(captureFile);

        // forms the json to requestEntity
        StringEntity captureEntity = new StringEntity(String.valueOf(jsonCapture), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut captureRequest = new HttpPut("http://localhost:" + port + "/capture/file");
        // set requestEntity to the put request
        captureRequest.setEntity(captureEntity);
        // Header
        captureRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse captureResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(captureRequest));

        // Get the entity from response
        HttpEntity captureAsEntity = captureResponse.getEntity();

        // Entity response string
        String captureAsResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(captureAsEntity));

        // Parsing response as JSONObject
        JSONObject captureAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(captureAsResponse));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String captureAsExpected = "New capture created";

        // Creating string from Json that was given as a response
        String captureAsActual = Assertions.assertDoesNotThrow(() -> captureAsJson.get("message").toString());

        // Creates the request
        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/v2/captures/group/capture/1/2");
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup));

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Type mismatch between capture group and capture";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(expected1, actual1);
        assertEquals(HttpStatus.SC_CREATED, httpResponseFileId.getStatusLine().getStatusCode());

        assertEquals(captureAsExpected, captureAsActual);
        assertEquals(HttpStatus.SC_CREATED, captureResponse.getStatusLine().getStatusCode());

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_CONFLICT, httpResponse.getStatusLine().getStatusCode());

    }

    @Test
    @Order(7)
    public void testInsertValidCaptureToGroup() {
        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/v2/captures/group/capture/1/1");
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup));

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Capture linked with group";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_CREATED, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(8)
    public void testInsertCaptureToGroupDuplicateTag() {
        // insert another capture with same tag
        CaptureRelp captureRelp = new CaptureRelp();
        captureRelp.setTag("relpTag");
        captureRelp.setRetentionTime("P15D");
        captureRelp.setCategory("audit");
        captureRelp.setApplication("relp2");
        captureRelp.setIndex("audit_relp2");
        captureRelp.setSourceType("relpsource2");
        captureRelp.setProtocol("prot");
        captureRelp.setFlow("capFlow");

        String captureJson = gson.toJson(captureRelp);

        // forms the json to requestEntity
        StringEntity captureEntity = new StringEntity(String.valueOf(captureJson), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut captureRequest = new HttpPut("http://localhost:" + port + "/capture/relp");
        // set requestEntity to the put request
        captureRequest.setEntity(captureEntity);
        // Header
        captureRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse captureResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(captureRequest));

        // Get the entity from response
        HttpEntity captureAsEntity = captureResponse.getEntity();

        // Entity response string
        String captureAsResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(captureAsEntity));

        // Parsing response as JSONObject
        JSONObject captureAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(captureAsResponse));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String captureAsExpected = "New capture created";

        // Creating string from Json that was given as a response
        String captureAsActual = Assertions.assertDoesNotThrow(() -> captureAsJson.get("message").toString());

        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/v2/captures/group/capture/1/3");
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup));

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Tag already exists within given group";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        assertEquals(captureAsExpected, captureAsActual);
        assertEquals(HttpStatus.SC_CREATED, captureResponse.getStatusLine().getStatusCode());

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_CONFLICT, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(9)
    public void testSelectAll() {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        JsonArrayBuilder captureDefs = Json.createArrayBuilder();
        captureDefs.add(1);
        jsonArrayBuilder
                .add(Json.createObjectBuilder().add("capture_def_ids", captureDefs.build().toString()).add("groupId", 1).add("type", "RELP").add("flowId", 1));

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/group/capture");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        String expected = jsonArrayBuilder.build().toString();

        assertEquals(expected, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(10)
    public void testSelectCaptures() {
        JsonArrayBuilder expectedBuilder = Json.createArrayBuilder();
        expectedBuilder.add(Json.createObjectBuilder().add("id", 1));
        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/group/capture/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        String expected = expectedBuilder.build().toString();

        assertEquals(expected, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(11)
    public void testSelectGroups() {
        JsonArrayBuilder expectedBuilder = Json.createArrayBuilder();
        expectedBuilder.add(Json.createObjectBuilder().add("id", 1));
        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/group/capture/groups/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        String expected = expectedBuilder.build().toString();

        assertEquals(expected, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(12)
    public void testDeleteInvalidGroup() {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/captures/group/capture/67/3");

        // Header
        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

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
    @Order(13)
    public void testDeleteInvalidCapture() {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/captures/group/capture/1/67");

        // Header
        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

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
    @Order(14)
    public void testDeleteValidLinkage() {

        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/captures/group/capture/1/1");

        // Header
        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Capture deleted from group";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_OK, deleteResponse.getStatusLine().getStatusCode());

    }
}
