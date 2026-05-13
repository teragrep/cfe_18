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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MigrateDatabaseExtension.class)
public class CaptureMetaControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @BeforeAll
    public void testData() {
        TestApiClient testApiClient = new TestApiClient(port, token);
        final Integer flowId = testApiClient.insertFlow("capflow");
        testApiClient.insertSink(flowId, "cap", "capsink", "prot");
        testApiClient.insertCapture("relpTag", "P30D", "audit", "relp", "audit_relp", "relpsource1", "prot", "capflow");

    }

    @Test
    @Order(1)
    public void testAddCaptureMeta() {

        CaptureMeta captureMeta = new CaptureMeta();
        captureMeta.setCaptureId(1);
        captureMeta.setCaptureMetaKey("relpKey1");
        captureMeta.setCaptureMetaValue("relpValue1");

        String jsonFileApplication = gson.toJson(captureMeta);

        StringEntity requestEntity4 = new StringEntity(
                String.valueOf(jsonFileApplication),
                ContentType.APPLICATION_JSON
        );

        HttpPut request4 = new HttpPut("http://localhost:" + port + "/v2/captures/definitions/1/metadata");
        request4.setEntity(requestEntity4);
        request4.setHeader("Authorization", "Bearer " + token);

        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request4));

        HttpEntity entity = httpResponse.getEntity();

        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        String expected = "New capture meta created";

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_CREATED, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(2)
    public void testRetrieveCaptureMeta() {
        ArrayList<CaptureMeta> expected = new ArrayList<>();
        CaptureMeta captureMeta = new CaptureMeta();
        captureMeta.setCaptureId(1);
        captureMeta.setCaptureMetaKey("relpKey1");
        captureMeta.setCaptureMetaValue("relpValue1");
        expected.add(captureMeta);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/definitions/1/metadata");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(expected.toString(), responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());

    }

    @Test
    @Order(3)
    public void testNoCaptureForInsertingMeta() {
        CaptureMeta captureMeta = new CaptureMeta();
        captureMeta.setCaptureId(123);
        captureMeta.setCaptureMetaKey("relpKey1");
        captureMeta.setCaptureMetaValue("relpValue1");

        String jsonFileApplication = gson.toJson(captureMeta);

        StringEntity requestEntity4 = new StringEntity(
                String.valueOf(jsonFileApplication),
                ContentType.APPLICATION_JSON
        );

        HttpPut request4 = new HttpPut("http://localhost:" + port + "/v2/captures/definitions/123/metadata");
        request4.setEntity(requestEntity4);
        request4.setHeader("Authorization", "Bearer " + token);

        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request4));

        HttpEntity entity = httpResponse.getEntity();

        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));
        String expected = "Record does not exist";

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(4)
    public void testNoMetaForKey() {
        CaptureRelp captureRelp = new CaptureRelp();
        captureRelp.setTag("a");
        captureRelp.setRetentionTime("a");
        captureRelp.setCategory("a");
        captureRelp.setApplication("a");
        captureRelp.setIndex("a");
        captureRelp.setSourceType("a");
        captureRelp.setProtocol("prot");
        captureRelp.setFlow("capFlow");

        String jsonFile = gson.toJson(captureRelp);

        StringEntity requestEntity3 = new StringEntity(String.valueOf(jsonFile), ContentType.APPLICATION_JSON);

        HttpPut request3 = new HttpPut("http://localhost:" + port + "/v2/captures/1/definitions/relp-streams");
        request3.setEntity(requestEntity3);
        request3.setHeader("Authorization", "Bearer " + token);

        Assertions.assertDoesNotThrow(() -> {
            HttpClientBuilder.create().build().execute(request3);
        });

        HttpGet requestGet = new HttpGet(
                "http://localhost:" + port + "/v2/captures/definitions/555/metadata?key=doesNotExist"
        );

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
    @Order(5)
    public void testGetMetaWithCorrectKey() {
        CaptureMeta captureMeta = new CaptureMeta();
        captureMeta.setCaptureId(1);
        captureMeta.setCaptureMetaKey("relpKey1");
        captureMeta.setCaptureMetaValue("relpValue1");

        HttpGet requestGet = new HttpGet(
                "http://localhost:" + port + "/v2/captures/definitions/1/metadata?key=relpKey1"
        );

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        List<CaptureMeta> exceptedCaptureMetas = new ArrayList<>();
        exceptedCaptureMetas.add(captureMeta);

        assertEquals(exceptedCaptureMetas.toString(), responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(6)
    public void testGetAllCaptureMetas() {
        CaptureMeta captureMeta = new CaptureMeta();
        captureMeta.setCaptureId(1);
        captureMeta.setCaptureMetaKey("relpKey2");
        captureMeta.setCaptureMetaValue("relpValue2");

        String jsonFileApplication = gson.toJson(captureMeta);

        StringEntity requestEntity4 = new StringEntity(
                String.valueOf(jsonFileApplication),
                ContentType.APPLICATION_JSON
        );

        HttpPut request4 = new HttpPut("http://localhost:" + port + "/v2/captures/definitions/1/metadata/");
        request4.setEntity(requestEntity4);
        request4.setHeader("Authorization", "Bearer " + token);

        Assertions.assertDoesNotThrow(() -> {
            HttpClientBuilder.create().build().execute(request4);
        });

        ArrayList<CaptureMeta> expected = new ArrayList<>();
        CaptureMeta captureMeta2 = new CaptureMeta();
        captureMeta2.setCaptureId(1);
        captureMeta2.setCaptureMetaKey("relpKey1");
        captureMeta2.setCaptureMetaValue("relpValue1");

        expected.add(captureMeta2);
        expected.add(captureMeta);

        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/captures/definitions/1/metadata");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(expected.toString(), responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(7)
    public void testDeleteCaptureMeta() {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/captures/definitions/1/metadata");

        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        String expected = "Capture meta deleted";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_OK, deleteResponse.getStatusLine().getStatusCode());
    }

}
