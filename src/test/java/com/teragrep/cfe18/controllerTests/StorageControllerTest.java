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
public class StorageControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    public void testInsertStorage() {

        Storage storage = new Storage();
        storage.setStorageType(StorageType.CFE_04);
        storage.setStorageName("cfe_04");

        String json = gson.toJson(storage);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(String.valueOf(json), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/storage");
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
        String expected = "New storage created";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(expected, actual);
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED));

    }

    @Test
    @Order(2)
    public void testFetchStorages() {
        ArrayList<Storage> expected = new ArrayList<>();

        Storage storage = new Storage();
        storage.setStorageName("cfe_04");
        storage.setStorageType(StorageType.CFE_04);
        storage.setId(1);

        expected.add(storage);

        String expectedJson = new Gson().toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/storage");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet));

        assertEquals(expectedJson, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }

    @Test
    @Order(3)
    public void testFetchStorage() {
        Storage storage = new Storage();
        storage.setStorageName("cfe_04");
        storage.setStorageType(StorageType.CFE_04);
        storage.setId(1);

        String expectedJson = new Gson().toJson(storage);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/storage/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet));

        assertEquals(expectedJson, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }

    @Test
    @Order(3)
    public void testInsertFlowStorage() throws Exception {

        Flow flow = new Flow();
        flow.setName("Testflow");

        String json = gson.toJson(flow);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(String.valueOf(json), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/flow");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpClientBuilder.create().build().execute(request);

        FlowStorage flowStorage = new FlowStorage();
        flowStorage.setFlow("Testflow");
        flowStorage.setStorage_id(1);

        String jsonStorage = gson.toJson(flowStorage);

        // forms the json to requestEntity
        StringEntity requestEntityStorageFlow = new StringEntity(
                String.valueOf(jsonStorage),
                ContentType.APPLICATION_JSON
        );

        // Creates the request
        HttpPut requestEntityStorage = new HttpPut("http://localhost:" + port + "/storage/flow");
        // set requestEntity to the put request
        requestEntityStorage.setEntity(requestEntityStorageFlow);
        // Header
        requestEntityStorage.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(requestEntityStorage);

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = new JSONObject(responseString);

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New flow storage created";

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Assertions

        assertEquals(expected, actual);
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED));
    }

    @Test
    @Order(4)
    public void testFetchFlowStorage() throws Exception {
        ArrayList<FlowStorage> expected = new ArrayList<>();

        FlowStorage flowStorage = new FlowStorage();
        flowStorage.setId(1);
        flowStorage.setStorage_type("CFE_04");
        flowStorage.setFlow("Testflow");
        flowStorage.setStorage_name("cfe_04");
        flowStorage.setStorage_id(1);

        expected.add(flowStorage);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/storage/flow/Testflow");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        String expectedJson = new Gson().toJson(expected);

        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertEquals(expectedJson, responseStringGet);
    }

    @Test
    @Order(5)
    public void testFetchFlowStorages() throws Exception {

        ArrayList<FlowStorage> expected = new ArrayList<>();

        FlowStorage flowStorage = new FlowStorage();
        flowStorage.setId(1);
        flowStorage.setStorage_type("CFE_04");
        flowStorage.setFlow("Testflow");
        flowStorage.setStorage_name("cfe_04");
        flowStorage.setStorage_id(1);

        expected.add(flowStorage);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/storage/flow");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        String expectedJson = new Gson().toJson(expected);

        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertEquals(expectedJson, responseStringGet);
    }

    @Test
    @Order(6)
    public void testInsertCaptureStorage() throws Exception {
        // add sink for capture
        // insert sink

        Sink sink = new Sink();
        sink.setFlowId(1);
        sink.setPort("cap");
        sink.setIpAddress("capsink");
        sink.setProtocol("prot");

        String json1 = gson.toJson(sink);

        // forms the json to requestEntity
        StringEntity requestEntity1 = new StringEntity(String.valueOf(json1), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request1 = new HttpPut("http://localhost:" + port + "/sink");
        // set requestEntity to the put request
        request1.setEntity(requestEntity1);
        // Header
        request1.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpClientBuilder.create().build().execute(request1);

        // add relp type capture with the same flow
        CaptureRelp captureRelp = new CaptureRelp();
        captureRelp.setTag("relpTag");
        captureRelp.setRetentionTime("P30D");
        captureRelp.setCategory("audit");
        captureRelp.setApplication("relp");
        captureRelp.setIndex("audit_relp");
        captureRelp.setSourceType("relpsource1");
        captureRelp.setProtocol("prot");
        captureRelp.setFlow("Testflow");

        String jsonFile = gson.toJson(captureRelp);

        // forms the json to requestEntity
        StringEntity requestEntity3 = new StringEntity(String.valueOf(jsonFile), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request3 = new HttpPut("http://localhost:" + port + "/v2/captures/definitions/relp-streams");
        // set requestEntity to the put request
        request3.setEntity(requestEntity3);
        // Header
        request3.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpClientBuilder.create().build().execute(request3);

        // Link index and sourcetype to storage
        Cfe04StorageIndex cfe04StorageIndex = new Cfe04StorageIndex();
        cfe04StorageIndex.setIndexId(1);
        cfe04StorageIndex.setRepFactor("repFactor");
        cfe04StorageIndex.setDisabled(true);
        cfe04StorageIndex.setHomePath("homePath");
        cfe04StorageIndex.setColdpath("coldPath");
        cfe04StorageIndex.setThawedPath("thawedPath");

        String cfe04StorageIndexJson = gson.toJson(cfe04StorageIndex);

        // forms the json to requestEntity
        StringEntity cfe04IndexAsEntity = new StringEntity(
                String.valueOf(cfe04StorageIndexJson),
                ContentType.APPLICATION_JSON
        );

        // Creates the request
        HttpPut cfe04indexRequest = new HttpPut(
                "http://localhost:" + port + "/v2/storages/definitions/cfe_04/1/indexes"
        );
        // set requestEntity to the put request
        cfe04indexRequest.setEntity(cfe04IndexAsEntity);
        // Header
        cfe04indexRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse cfe04indexResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(cfe04indexRequest));

        // Get the entity from response
        HttpEntity cfe04IndexEntity = cfe04indexResponse.getEntity();

        // Entity response string
        String cfe04IndexResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(cfe04IndexEntity));

        // Parsing response as JSONObject
        JSONObject cfe04IndexAsJsonResponse = Assertions.assertDoesNotThrow(() -> new JSONObject(cfe04IndexResponse));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedCfe04Index = "New index linked to storage";

        // Creating string from Json that was given as a response
        String actualCfe04Index = Assertions
                .assertDoesNotThrow(() -> cfe04IndexAsJsonResponse.get("message").toString());

        Cfe04StorageSourcetype cfe04StorageSourcetype = new Cfe04StorageSourcetype();
        cfe04StorageSourcetype.setSourcetypeId(1);
        cfe04StorageSourcetype.setCategory("category");
        cfe04StorageSourcetype.setFreeformIndexerEnabled(true);
        cfe04StorageSourcetype.setFreeformIndexerText("text");
        cfe04StorageSourcetype.setTruncate("truncate");
        cfe04StorageSourcetype.setMaxDaysAgo("maxDaysAgo");
        cfe04StorageSourcetype.setSourceDescription("sourceDescription");
        cfe04StorageSourcetype.setFreeformLbText("freeformLbText");
        cfe04StorageSourcetype.setFreeformLbEnabled(true);

        String cfe04StorageSourcetypeJson = gson.toJson(cfe04StorageSourcetype);

        // forms the json to requestEntity
        StringEntity cfe04SourcetypeRequestEntity = new StringEntity(
                String.valueOf(cfe04StorageSourcetypeJson),
                ContentType.APPLICATION_JSON
        );

        // Creates the request
        HttpPut cfe04SourcetypeRequest = new HttpPut(
                "http://localhost:" + port + "/v2/storages/definitions/cfe_04/1/sourcetypes"
        );
        // set requestEntity to the put request
        cfe04SourcetypeRequest.setEntity(cfe04SourcetypeRequestEntity);
        // Header
        cfe04SourcetypeRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse cfe04SourcetypeHttpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(cfe04SourcetypeRequest));

        // Get the entity from response
        HttpEntity cfe04SourcetypeHttpResponseEntity = cfe04SourcetypeHttpResponse.getEntity();

        // Entity response string
        String cfe04SourcetypeEntity = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(cfe04SourcetypeHttpResponseEntity));

        // Parsing response as JSONObject
        JSONObject cfe04SourcetypeAsJsonResponse = Assertions
                .assertDoesNotThrow(() -> new JSONObject(cfe04SourcetypeEntity));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedCfe04Sourcetype = "New sourcetype linked to storage";

        // Creating string from Json that was given as a response
        String actualCfe04Sourcetype = Assertions
                .assertDoesNotThrow(() -> cfe04SourcetypeAsJsonResponse.get("message").toString());

        // link the cfe_04 storage to capture
        CaptureStorage captureStorage = new CaptureStorage();
        captureStorage.setCapture_id(1);
        captureStorage.setStorage_id(1);

        String jsonStorage = gson.toJson(captureStorage);

        // forms the json to requestEntity
        StringEntity requestEntityStorageCapture = new StringEntity(
                String.valueOf(jsonStorage),
                ContentType.APPLICATION_JSON
        );

        // Creates the request
        HttpPut requestEntityStorage = new HttpPut("http://localhost:" + port + "/storage/capture");
        // set requestEntity to the put request
        requestEntityStorage.setEntity(requestEntityStorageCapture);
        // Header
        requestEntityStorage.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(requestEntityStorage);

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = new JSONObject(responseString);

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New capture storage created";

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Assertions
        assertEquals(expectedCfe04Sourcetype, actualCfe04Sourcetype);
        assertEquals(HttpStatus.SC_CREATED, cfe04SourcetypeHttpResponse.getStatusLine().getStatusCode());
        assertEquals(expectedCfe04Index, actualCfe04Index);
        assertEquals(HttpStatus.SC_CREATED, cfe04indexResponse.getStatusLine().getStatusCode());
        assertEquals(expected, actual);
        assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CREATED));

    }

    @Test
    @Order(7)
    public void testRetrieveCaptureStorage() throws Exception {
        ArrayList<CaptureStorage> expected = new ArrayList<>();
        CaptureStorage captureStorage = new CaptureStorage();
        captureStorage.setStorage_name("cfe_04");
        captureStorage.setStorage_id(1);
        captureStorage.setCapture_id(1);

        expected.add(captureStorage);
        String expectedJson = new Gson().toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/storage/capture/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertEquals(expectedJson, responseStringGet);
    }

    @Test
    @Order(8)
    public void testRetrieveCaptureStorages() throws Exception {
        ArrayList<CaptureStorage> expected = new ArrayList<>();
        CaptureStorage captureStorage = new CaptureStorage();
        captureStorage.setStorage_name("cfe_04");
        captureStorage.setStorage_id(1);
        captureStorage.setCapture_id(1);

        expected.add(captureStorage);
        String expectedJson = new Gson().toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/storage/capture");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertEquals(expectedJson, responseStringGet);
    }

    @Test
    @Order(9)
    public void testDeleteNonExistentStorage() {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/storage/" + 124);

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
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    @Order(10)
    public void testDeleteStorageInUse() throws Exception {

        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/storage/" + 1);

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
        String expected = "Is in use";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_CONFLICT));
    }

    @Test
    @Order(11)
    public void testDeleteNonExistentFlowStorage() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/storage/flow/" + "Testflow/" + 124);

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
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    @Order(12)
    public void testDeleteFlowStorageInUse() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/storage/flow/" + "Testflow/" + 1);

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
        String expected = "Is in use";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    @Order(13)
    public void testDeleteNonExistentCaptureStorage() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/storage/capture/" + 112 + "/" + 112);

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
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
    }

    @Test
    @Order(14)
    public void testDeleteCaptureStorage() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/storage/capture/" + 1 + "/" + 1);
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
        String expected = "Capture = " + 1 + ", with Storage " + 1 + " deleted.";

        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertEquals(expected, actual);
    }

    @Test
    @Order(15)
    public void testDeleteFlowStorage() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/storage/flow/" + "Testflow/" + 1);
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
        String expected = "Flow =Testflow, Storage 1 deleted.";

        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertEquals(expected, actual);
    }

    @Test
    @Order(16)
    public void testDeleteStorage() {
        // Delete sourcetype and index so that storage can be deleted
        // Creates the request
        HttpDelete indexDelete = new HttpDelete(
                "http://localhost:" + port + "/v2/storages/definitions/cfe_04/" + 1 + "/indexes/" + 1
        );
        // Header
        indexDelete.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse indexDeleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(indexDelete));

        // Get the entity from response
        HttpEntity indexDeleteResponseEntity = indexDeleteResponse.getEntity();

        // Entity response string
        String indexDeleteEntity = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(indexDeleteResponseEntity));

        // Parsing response as JSONObject
        JSONObject indexDeleteAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(indexDeleteEntity));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedindex = "Index deleted from cfe_04 storage";

        // Creating string from Json that was given as a response
        String actualindex = Assertions.assertDoesNotThrow(() -> indexDeleteAsJson.get("message").toString());

        // Creates the request
        HttpDelete sourcetypeRequest = new HttpDelete(
                "http://localhost:" + port + "/v2/storages/definitions/cfe_04/" + 1 + "/sourcetypes/" + 1
        );
        // Header
        sourcetypeRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse sourcetypeResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(sourcetypeRequest));

        // Get the entity from response
        HttpEntity sourcetypeResponseEntity = sourcetypeResponse.getEntity();

        // Entity response string
        String sourcetypeAsResponse = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(sourcetypeResponseEntity));

        // Parsing response as JSONObject
        JSONObject sourcetypeAsJsonResponse = Assertions.assertDoesNotThrow(() -> new JSONObject(sourcetypeAsResponse));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedSourcetype = "Sourcetype deleted from cfe_04 storage";

        // Creating string from Json that was given as a response
        String actualSourcetype = Assertions
                .assertDoesNotThrow(() -> sourcetypeAsJsonResponse.get("message").toString());

        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/storage/" + 1);
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
        String expected = "Storage deleted";

        // Assertions
        assertEquals(expectedindex, actualindex);
        assertEquals(HttpStatus.SC_OK, indexDeleteResponse.getStatusLine().getStatusCode());
        assertEquals(expectedSourcetype, actualSourcetype);
        assertEquals(HttpStatus.SC_OK, sourcetypeResponse.getStatusLine().getStatusCode());
        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

}
