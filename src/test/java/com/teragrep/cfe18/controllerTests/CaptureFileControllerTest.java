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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MigrateDatabaseExtension.class)
class CaptureFileControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @BeforeAll
    void testData() throws Exception {
        // Filecapturemeta
        FileProcessing file = new FileProcessing();
        file.setInputtype(FileProcessing.InputType.regex);
        file.setInputvalue("capregex");
        file.setRuleset("capruleset");
        file.setName("capname");
        file.setTemplate("regex.moustache");

        String json = gson.toJson(file);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(
                String.valueOf(json),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/file/capture/meta/rule");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpClientBuilder.create().build().execute(request);

        // Get the response from endpoint
        HttpResponse httpResponseFileId = HttpClientBuilder.create().build().execute(request);

        // Get the entity from response
        HttpEntity entityFileId = httpResponseFileId.getEntity();

        // Entity response string
        String responseFileId = EntityUtils.toString(entityFileId);

        // Parsin respponse as JSONObject
        new JSONObject(responseFileId);


        // add flow and sink

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

        // Parsin respponse as JSONObject
        JSONObject responseJson2 = new JSONObject(response2);

        // insert sink

        Sink sink = new Sink();
        sink.setFlow("capflow");
        sink.setPort("cap");
        sink.setIp_address("capsink");
        sink.setProtocol("prot");

        String json1 = gson.toJson(sink);

        // forms the json to requestEntity
        StringEntity requestEntity1 = new StringEntity(
                String.valueOf(json1),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request1 = new HttpPut("http://localhost:" + port + "/sink/details");
        // set requestEntity to the put request
        request1.setEntity(requestEntity1);
        // Header
        request1.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpClientBuilder.create().build().execute(request1);
    }

    @Test
    @Order(1)
    public void testInsertCfeCapture() throws Exception {


        // add Capture File

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

        String jsonFile = gson.toJson(captureFile);

        // forms the json to requestEntity
        StringEntity requestEntity3 = new StringEntity(
                String.valueOf(jsonFile),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request3 = new HttpPut("http://localhost:" + port + "/capture/file");
        // set requestEntity to the put request
        request3.setEntity(requestEntity3);
        // Header
        request3.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request3);

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = new JSONObject(responseString);

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New capture created";

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Assertions
        assertEquals(expected, actual);
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));
    }

    @Test
    @Order(2)
    public void testGetCfeCapture() throws Exception {
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
        captureFile.setType(CaptureFile.CaptureType.cfe);

        String json = gson.toJson(captureFile);

        // Asserting get request                                            // cfe id
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/file/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");


        assertEquals(json, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    @Order(3)
    public void testgetAllCfeCaptures() throws Exception {

        ArrayList<CaptureFile> expected = new ArrayList<>();

        CaptureFile captureFile2 = new CaptureFile();
        captureFile2.setId(1);
        captureFile2.setTag("f466e5a4-tagpath1");
        captureFile2.setRetentionTime("P30D");
        captureFile2.setCategory("audit");
        captureFile2.setApplication("app1");
        captureFile2.setIndex("app1_audit");
        captureFile2.setSourceType("sourcetype1");
        captureFile2.setProtocol("prot");
        captureFile2.setFlow("capflow");
        captureFile2.setTagPath("tagpath1");
        captureFile2.setCapturePath("capturepath1");
        captureFile2.setFileProcessingTypeId(1);
        captureFile2.setType(CaptureFile.CaptureType.cfe);

        expected.add(captureFile2);

        String expectedJson = gson.toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/file");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(expectedJson, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }

    @Test
    @Order(4)
    public void testDeleteNonExistentCapture() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/capture/file/124124");

        // Header
        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = HttpClientBuilder.create().build().execute(delete);

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = EntityUtils.toString(entityDelete, "UTF-8");

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = new JSONObject(responseStringGet);

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Record does not exist";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    @Order(5)
    public void testDeleteCapture() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/capture/file/1");

        // Header
        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = HttpClientBuilder.create().build().execute(delete);

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = EntityUtils.toString(entityDelete, "UTF-8");

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = new JSONObject(responseStringGet);

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Capture deleted";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }
}
