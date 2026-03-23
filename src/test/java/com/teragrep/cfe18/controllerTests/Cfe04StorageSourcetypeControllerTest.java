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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MigrateDatabaseExtension.class)
public class Cfe04StorageSourcetypeControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @BeforeAll
    public void testData() {
        /*
        Insert
        1. cfe_04 storage
        2. flow
        3. sink
        4. capture
         */

        Storage storage = new Storage();
        storage.setStorageType(StorageType.CFE_04);
        storage.setStorageName("cfe_04");

        String jsonStorage = gson.toJson(storage);

        // forms the json to requestEntity
        StringEntity storageRequest = new StringEntity(String.valueOf(jsonStorage), ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut storageAsRequest = new HttpPut("http://localhost:" + port + "/storage");
        // set requestEntity to the put request
        storageAsRequest.setEntity(storageRequest);
        // Header
        storageAsRequest.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse storageResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(storageAsRequest));

        // Get the entity from response
        HttpEntity storageResponseEntity = storageResponse.getEntity();

        // Entity response string
        String storageAsResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(storageResponseEntity));

        // Parsing response as JSONObject
        JSONObject storageAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(storageAsResponse));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedStorage = "New storage created";

        // Creating string from Json that was given as a response
        String actualStorage = Assertions.assertDoesNotThrow(() -> storageAsJson.get("message").toString());

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

        // Assertions
        assertEquals(expectedStorage, actualStorage);
        assertEquals(HttpStatus.SC_CREATED, storageResponse.getStatusLine().getStatusCode());
        assertEquals(expectedFlow, actualFlow);
        assertEquals(HttpStatus.SC_CREATED, flowResponse.getStatusLine().getStatusCode());
        assertEquals(expectedSink, actualSink);
        assertEquals(HttpStatus.SC_CREATED, sinkResponse.getStatusLine().getStatusCode());
        assertEquals(expectedCapture, actualCapture);
        assertEquals(HttpStatus.SC_CREATED, captureResponse.getStatusLine().getStatusCode());

    }

    @Test
    @Order(1)
    @Description("Test valid cfe04 storage sourcetype insertion")
    public void testInsertStorageSourcetype() {
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
        StringEntity requestEntity = new StringEntity(
                String.valueOf(cfe04StorageSourcetypeJson),
                ContentType.APPLICATION_JSON
        );

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/v2/storages/definitions/cfe_04/1/sourcetypes");
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
        String expected = "New sourcetype linked to storage";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_CREATED, httpResponse.getStatusLine().getStatusCode());

    }

    @Test
    @Order(2)
    @Description("Test insert invalid sourcetype id to cfe04 storage")
    public void testInsertInvalidSourcetypeId() {
        Cfe04StorageSourcetype cfe04StorageSourcetype = new Cfe04StorageSourcetype();
        cfe04StorageSourcetype.setSourcetypeId(67);
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
        StringEntity requestEntity = new StringEntity(
                String.valueOf(cfe04StorageSourcetypeJson),
                ContentType.APPLICATION_JSON
        );

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/v2/storages/definitions/cfe_04/1/sourcetypes");
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
        String expected = "Record does not exist";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(3)
    @Description("Test insert invalid cfe04 id to sourcetype")
    public void testInsertInvalidCfe04Id() {
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
        StringEntity requestEntity = new StringEntity(
                String.valueOf(cfe04StorageSourcetypeJson),
                ContentType.APPLICATION_JSON
        );

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/v2/storages/definitions/cfe_04/67/sourcetypes");
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
        String expected = "Record does not exist";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(4)
    @Description("Test delete cfe04 storage sourcetype")
    public void testDeleteCfe04StorageSourcetype() {
        // Creates the request
        HttpDelete request = new HttpDelete(
                "http://localhost:" + port + "/v2/storages/definitions/cfe_04/" + 1 + "/sourcetypes/" + 1
        );
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
        String expected = "Sourcetype deleted from cfe_04 storage";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
    }
}
