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
import com.teragrep.cfe18.handlers.entities.CaptureFile;
import com.teragrep.cfe18.handlers.entities.FileProcessing;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
        file.setInputtype(FileProcessing.InputType.regex);
        file.setInputvalue("normalregex");
        file.setRuleset("ruleset1");
        file.setName("name1");
        file.setTemplate("regex.moustache");

        String json = gson.toJson(file);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(
                String.valueOf(json),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/file/capture/meta");
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
        String expected = "New file processing type created";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(expected, actual);
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));

    }

    @Test
    @Order(2)
    public void testGetFileProcessingTypeById() {

        FileProcessing file2 = new FileProcessing();
        file2.setInputtype(FileProcessing.InputType.regex);
        file2.setInputvalue("normalregex");
        file2.setRuleset("ruleset1");
        file2.setName("name1");
        file2.setTemplate("regex.moustache");


        String json2 = gson.toJson(file2);


        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(
                String.valueOf(json2),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/file/capture/meta");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        FileProcessing file = new FileProcessing();
        file.setInputtype(FileProcessing.InputType.regex);
        file.setInputvalue("normalregex");
        file.setRuleset("ruleset1");
        file.setName("name1");
        file.setTemplate("regex.moustache");
        file.setId(1);

        String json = gson.toJson(file);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/file/capture/meta/" + 1);

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(json, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
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
        file1.setInputtype(FileProcessing.InputType.regex);
        file1.setInputvalue("test");
        file1.setRuleset("test");
        file1.setName("test");
        file1.setTemplate("test");
        String json1 = real.toJson(file1);


        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(
                String.valueOf(json1),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/file/capture/meta");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        FileProcessing file2 = new FileProcessing();
        file2.setId(1);
        file2.setInputtype(FileProcessing.InputType.regex);
        file2.setInputvalue("normalregex");
        file2.setRuleset("ruleset1");
        file2.setName("name1");
        file2.setTemplate("regex.moustache");

        // add the expected values to json
        expected.add(file2);
        expected.add(file1);
        String expectedJson = real.toJson(expected);

        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/file/capture/meta");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        // Asserting
        assertEquals(expectedJson, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    @Order(4)
    public void testDeleteProcessingType() {

        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/file/capture/meta/" + 2);

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
        String expected = "File processing type deleted";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }

    @Test
    @Order(5)
    public void testDeleteNonExistentProcessingType() {

        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/file/capture/meta/" + 2222);

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
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND));

    }

    @Test
    @Order(6)
    public void testDeleteProcessingTypeInUse() {
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

        Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request2));

        // insert sink

        Sink sink = new Sink();
        sink.setFlow("capFlow");
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
        Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request1));

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
        Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request3));

        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/file/capture/meta/" + 1);

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
        String expected = "Is in use";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));

    }

}