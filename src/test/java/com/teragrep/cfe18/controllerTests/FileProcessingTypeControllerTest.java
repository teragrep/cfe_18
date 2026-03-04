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
import com.google.gson.GsonBuilder;
import com.teragrep.cfe18.handlers.entities.*;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MigrateDatabaseExtension.class)
public class FileProcessingTypeControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    public void testFileProcessingType() {
        FileProcessing file = new FileProcessing();
        file.setInputtype(InputType.REGEX);
        file.setInputvalue("normalregex");
        file.setRuleset("ruleset1");
        file.setName("name1");
        file.setTemplate("regex.moustache");

        String json = gson.toJson(file);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(String.valueOf(json), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/v2/captures/definitions/files/types");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New file processing type created";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_CREATED, httpResponse.getStatusLine().getStatusCode());

    }

    @Test
    @Order(2)
    public void testGetFileProcessingTypeById() {

        FileProcessing file2 = new FileProcessing();
        file2.setInputtype(InputType.REGEX);
        file2.setInputvalue("normalregex");
        file2.setRuleset("ruleset1");
        file2.setName("name1");
        file2.setTemplate("regex.moustache");

        String jsonFirst = gson.toJson(file2);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(String.valueOf(jsonFirst), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/v2/captures/definitions/files/types");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseFirst = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        HttpEntity firstEntity = responseFirst.getEntity();

        String firstResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(firstEntity, "UTF-8"));

        // Parsing response as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(firstResponse));

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New file processing type created";

        FileProcessing file = new FileProcessing();
        file.setInputtype(InputType.REGEX);
        file.setInputvalue("normalregex");
        file.setRuleset("ruleset1");
        file.setName("name1");
        file.setTemplate("regex.moustache");
        file.setId(1);

        String jsonSecond = gson.toJson(file);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/definitions/files/types/" + 1);

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseSecond = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entitySecond = responseSecond.getEntity();

        String secondResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entitySecond, "UTF-8"));

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_CREATED, responseFirst.getStatusLine().getStatusCode());

        assertEquals(jsonSecond, secondResponse);
        assertEquals(HttpStatus.SC_OK, responseSecond.getStatusLine().getStatusCode());
    }

    @Test
    @Order(3)
    public void testGetAllFileProcessingTypes() {
        GsonBuilder gson2 = new GsonBuilder().serializeNulls();
        Gson real = gson2.create();
        // Declare list of expected values
        ArrayList<FileProcessing> expected = new ArrayList<>();

        // add another piece of data so that
        FileProcessing file1 = new FileProcessing();
        file1.setId(2);
        file1.setInputtype(InputType.REGEX);
        file1.setInputvalue("test");
        file1.setRuleset("test");
        file1.setName("test");
        file1.setTemplate("test");
        String json1 = real.toJson(file1);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(String.valueOf(json1), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/v2/captures/definitions/files/types");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        FileProcessing file2 = new FileProcessing();
        file2.setId(1);
        file2.setInputtype(InputType.REGEX);
        file2.setInputvalue("normalregex");
        file2.setRuleset("ruleset1");
        file2.setName("name1");
        file2.setTemplate("regex.moustache");

        // add the expected values to json
        expected.add(file2);
        expected.add(file1);
        String expectedJson = real.toJson(expected);

        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/definitions/files/types");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        // Asserting
        assertEquals(expectedJson, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(4)
    public void testDeleteProcessingType() {

        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/captures/definitions/files/types/" + 2);

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
        String expected = "File processing type deleted";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_OK, deleteResponse.getStatusLine().getStatusCode());

    }

    @Test
    @Order(5)
    public void testDeleteNonExistentProcessingType() {

        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/captures/definitions/files/types/" + 2222);

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
    @Order(6)
    public void testDeleteProcessingTypeInUse() {
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

        HttpResponse flowResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(flowAsRequest));

        HttpEntity flowAsEntity = flowResponse.getEntity();

        String flowAsResponseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(flowAsEntity, "UTF-8"));

        // Parsing response as JSONObject
        JSONObject flowAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(flowAsResponseString));

        // Creating string from Json that was given as a response
        String flowActual = Assertions.assertDoesNotThrow(() -> flowAsJson.get("message").toString());

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String flowExpected = "New flow created";

        // insert sink

        Sink sink = new Sink();
        sink.setFlowId(1);
        sink.setPort("cap");
        sink.setIpAddress("capsink");
        sink.setProtocol("prot");

        String jsonSink = gson.toJson(sink);

        // forms the json to requestEntity
        StringEntity sinkRequest = new StringEntity(String.valueOf(jsonSink), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut sinkAsRequest = new HttpPut("http://localhost:" + port + "/sink");
        // set requestEntity to the put request
        sinkAsRequest.setEntity(sinkRequest);
        // Header
        sinkAsRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse sinkResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(sinkAsRequest));

        HttpEntity sinkAsResponseEntity = sinkResponse.getEntity();

        String sinkAsResponseString = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(sinkAsResponseEntity, "UTF-8"));

        // Parsing response as JSONObject
        JSONObject sinkASJson = Assertions.assertDoesNotThrow(() -> new JSONObject(sinkAsResponseString));

        // Creating string from Json that was given as a response
        String sinkActual = Assertions.assertDoesNotThrow(() -> sinkASJson.get("message").toString());

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String sinkExpected = "New sink created";

        CaptureFile captureFile = new CaptureFile();
        captureFile.setId(1);
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

        String jsonFile = gson.toJson(captureFile);

        // forms the json to requestEntity
        StringEntity captureEntity = new StringEntity(String.valueOf(jsonFile), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut captureAsRequest = new HttpPut("http://localhost:" + port + "/v2/captures/definitions/files");
        // set requestEntity to the put request
        captureAsRequest.setEntity(captureEntity);
        // Header
        captureAsRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse captureResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(captureAsRequest));

        HttpEntity captureResponseEntity = captureResponse.getEntity();

        String captureResponseAsString = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(captureResponseEntity, "UTF-8"));

        // Parsing response as JSONObject
        JSONObject captureAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(captureResponseAsString));

        // Creating string from Json that was given as a response
        String captureActual = Assertions.assertDoesNotThrow(() -> captureAsJson.get("message").toString());

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String captureExpected = "New capture created";

        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/captures/definitions/files/types/" + 1);

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
        String expected = "Is in use";

        assertEquals(flowExpected, flowActual);
        assertEquals(HttpStatus.SC_CREATED, flowResponse.getStatusLine().getStatusCode());

        assertEquals(sinkExpected, sinkActual);
        assertEquals(HttpStatus.SC_CREATED, sinkResponse.getStatusLine().getStatusCode());

        assertEquals(captureExpected, captureActual);
        assertEquals(HttpStatus.SC_CREATED, captureResponse.getStatusLine().getStatusCode());

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_BAD_REQUEST, deleteResponse.getStatusLine().getStatusCode());

    }

}
