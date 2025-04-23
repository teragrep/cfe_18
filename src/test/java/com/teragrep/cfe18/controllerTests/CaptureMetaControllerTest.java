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
public class CaptureMetaControllerTest extends TestSpringBootInformation{

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    public void testAddCaptureMeta() {
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

        Assertions.assertDoesNotThrow(() -> {HttpClientBuilder.create().build().execute(request2);});

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
        Assertions.assertDoesNotThrow(() -> {HttpClientBuilder.create().build().execute(request1); });

        CaptureRelp captureRelp = new CaptureRelp();
        captureRelp.setTag("relpTag");
        captureRelp.setRetention_time("P30D");
        captureRelp.setCategory("audit");
        captureRelp.setApplication("relp");
        captureRelp.setIndex("audit_relp");
        captureRelp.setSource_type("relpsource1");
        captureRelp.setProtocol("prot");
        captureRelp.setFlow("capFlow");

        String jsonFile = gson.toJson(captureRelp);

        // forms the json to requestEntity
        StringEntity requestEntity3 = new StringEntity(
                String.valueOf(jsonFile),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request3 = new HttpPut("http://localhost:" + port + "/capture/relp");
        // set requestEntity to the put request
        request3.setEntity(requestEntity3);
        // Header
        request3.setHeader("Authorization", "Bearer " + token);

        Assertions.assertDoesNotThrow(() -> {HttpClientBuilder.create().build().execute(request3); });


        // ******************************************************************************************************

        CaptureMeta captureMeta = new CaptureMeta();
        captureMeta.setCapture_id(1);
        captureMeta.setCapture_meta_key("relpKey1");
        captureMeta.setCapture_meta_value("relpValue1");

        String jsonFileApplication = gson.toJson(captureMeta);

        // forms the json to requestEntity
        StringEntity requestEntity4 = new StringEntity(
                String.valueOf(jsonFileApplication),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request4 = new HttpPut("http://localhost:" + port + "/capture/meta/");
        // set requestEntity to the put request
        request4.setEntity(requestEntity4);
        // Header
        request4.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint

        HttpResponse httpResponse = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request4));

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString =  Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New capture meta created for = 1";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() ->  responseAsJson.get("message").toString());

        // Assertions
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));
        assertEquals(expected, actual);
    }

    @Test
    @Order(2)
    public void testRetrieveCaptureMeta(){
        ArrayList<CaptureMeta> expected = new ArrayList<>();
        CaptureMeta captureMeta = new CaptureMeta();
        captureMeta.setCapture_id(1);
        captureMeta.setCapture_meta_key("relpKey1");
        captureMeta.setCapture_meta_value("relpValue1");
        expected.add(captureMeta);

        String json = gson.toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/meta/" + 1);

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() ->  HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() ->  EntityUtils.toString(entityGet, "UTF-8"));


        assertEquals(json, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }

    @Test
    @Order(3)
    public void testNoCaptureForInsertingMeta(){
        CaptureMeta captureMeta = new CaptureMeta();
        captureMeta.setCapture_id(123);
        captureMeta.setCapture_meta_key("relpKey1");
        captureMeta.setCapture_meta_value("relpValue1");

        String jsonFileApplication = gson.toJson(captureMeta);

        // forms the json to requestEntity
        StringEntity requestEntity4 = new StringEntity(
                String.valueOf(jsonFileApplication),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request4 = new HttpPut("http://localhost:" + port + "/capture/meta/");
        // set requestEntity to the put request
        request4.setEntity(requestEntity4);
        // Header
        request4.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = Assertions.assertDoesNotThrow(() ->  HttpClientBuilder.create().build().execute(request4));

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = Assertions.assertDoesNotThrow(() ->  EntityUtils.toString(entity));

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() ->  new JSONObject(responseString));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Capture does not exist";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() ->  responseAsJson.get("message").toString());

        // Assertions
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals(expected, actual);
    }

    @Test
    @Order(4)
    public void testNoMetaForCapture() {
        CaptureRelp captureRelp = new CaptureRelp();
        captureRelp.setTag("a");
        captureRelp.setRetention_time("a");
        captureRelp.setCategory("a");
        captureRelp.setApplication("a");
        captureRelp.setIndex("a");
        captureRelp.setSource_type("a");
        captureRelp.setProtocol("prot");
        captureRelp.setFlow("capFlow");

        String jsonFile = gson.toJson(captureRelp);

        // forms the json to requestEntity
        StringEntity requestEntity3 = new StringEntity(
                String.valueOf(jsonFile),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request3 = new HttpPut("http://localhost:" + port + "/capture/relp");
        // set requestEntity to the put request
        request3.setEntity(requestEntity3);
        // Header
        request3.setHeader("Authorization", "Bearer " + token);

        Assertions.assertDoesNotThrow(() -> {HttpClientBuilder.create().build().execute(request3); });

        // ******************************************************************************************************

        // Fetching capture that does not exist
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/meta/" + 555);

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Capture meta does not exist with given ID";

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertThat(
                responseGet.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals(expected, actual);
    }

    @Test
    @Order(5)
    public void testGetAllCaptureMetas()   {
        // add another capture meta for capture
        CaptureMeta captureMeta = new CaptureMeta();
        captureMeta.setCapture_id(1);
        captureMeta.setCapture_meta_key("relpKey2");
        captureMeta.setCapture_meta_value("relpValue2");

        String jsonFileApplication = gson.toJson(captureMeta);

        // forms the json to requestEntity
        StringEntity requestEntity4 = new StringEntity(
                String.valueOf(jsonFileApplication),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request4 = new HttpPut("http://localhost:" + port + "/capture/meta/");
        // set requestEntity to the put request
        request4.setEntity(requestEntity4);
        // Header
        request4.setHeader("Authorization", "Bearer " + token);

        // Execute inserting another capture meta
        Assertions.assertDoesNotThrow(() -> {HttpClientBuilder.create().build().execute(request4);});

        // Creating a list of existing capture metas
        ArrayList<CaptureMeta> expected = new ArrayList<>();
        CaptureMeta captureMeta2 = new CaptureMeta();
        captureMeta2.setCapture_id(1);
        captureMeta2.setCapture_meta_key("relpKey1");
        captureMeta2.setCapture_meta_value("relpValue1");

        expected.add(captureMeta2);
        expected.add(captureMeta);

        String json = gson.toJson(expected);
        // Fetching all capture metas
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/meta" );

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() ->  HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() ->  EntityUtils.toString(entityGet, "UTF-8"));

        // Assertions
        assertThat(
                responseGet.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_OK));
        assertEquals(json, responseStringGet);
    }


    @Test
    @Order(6)
    public void testCaptureMetaKeyValue() {
        // expected capture
        ArrayList<CaptureDefinition> expected = new ArrayList<>();
        CaptureDefinition captureRelp = new CaptureDefinition();
        captureRelp.setId(1);
        captureRelp.setTag("relpTag");
        captureRelp.setApplication("relp");
        captureRelp.setCaptureIndex("audit_relp");
        captureRelp.setSourcetype("relpsource1");
        expected.add(captureRelp);
        String jsonFile = gson.toJson(expected);

        // Fetching capture definition via key value pair from capture meta
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/meta/relpKey2/relpValue2");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() ->  HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() ->  EntityUtils.toString(entityGet, "UTF-8"));

        // Assertions
        assertEquals(jsonFile, responseStringGet);
        assertThat(
                responseGet.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_OK));

    }
    @Test
    @Order(7)
    public void testCaptureMetaKeyValueNoKey() {
        // Fetching capture definition via key value pair from capture meta
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/meta/missingKey/relpValue2");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() ->  HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() ->  EntityUtils.toString(entityGet, "UTF-8"));

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() ->  new JSONObject(responseStringGet));

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() ->  responseAsJson.get("message").toString());

        String expected = "No such key value pair exists";
        // Assertions
        assertEquals(expected, actual);
        assertThat(
                responseGet.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_BAD_REQUEST));

    }
    @Test
    @Order(8)
    public void testCaptureMetaKeyValueNoValue() {
        // Fetching capture definition via key value pair from capture meta
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/meta/relpKey2/missingValue");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() ->  HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() ->  EntityUtils.toString(entityGet, "UTF-8"));

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() ->  new JSONObject(responseStringGet));

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() ->  responseAsJson.get("message").toString());

        String expected = "No such key value pair exists";
        // Assertions
        assertEquals(expected, actual);
        assertThat(
                responseGet.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_BAD_REQUEST));

    }
    @Test
    @Order(9)
    public void testDeleteCaptureMeta()   {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/capture/meta/"+1);

        // Header
        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions.assertDoesNotThrow(() ->  HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() ->  EntityUtils.toString(entityDelete, "UTF-8"));

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() ->  new JSONObject(responseStringGet));

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() ->  responseAsJson.get("message").toString());

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "capture meta 1 deleted.";

        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertEquals(expected, actual);
    }

    @Test
    @Order(10)
    public void testDeleteNonExistentCaptureMeta()   {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/capture/meta/"+122);

        // Header
        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions.assertDoesNotThrow(() ->  HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() ->  EntityUtils.toString(entityDelete, "UTF-8"));

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() ->  new JSONObject(responseStringGet));

        // Creating string from Json that was given as a response
        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Record does not exist";

        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_BAD_REQUEST));
        assertEquals(expected, actual);
    }

}
