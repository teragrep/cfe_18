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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MigrateDatabaseExtension.class)
public class HostMetaControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    public void testAddHostMeta() {
        TestApiClient testApiClient = new TestApiClient(port, token);
        Integer hostId = testApiClient.insertRelpHost("relpHostmd5", "relpHostfq");

        HostMeta hostMeta = new HostMeta();
        hostMeta.setMetaKey("metaKey");
        hostMeta.setMetaValue("metaValue");

        String hostMetaJson = gson.toJson(hostMeta);

        StringEntity requestEntity4 = new StringEntity(String.valueOf(hostMetaJson), ContentType.APPLICATION_JSON);

        HttpPut request4 = new HttpPut("http://localhost:" + port + "/v2/hosts/definitions/" + hostId + "/metadata");
        request4.setEntity(requestEntity4);
        request4.setHeader("Authorization", "Bearer " + token);

        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request4));

        HttpEntity entity = httpResponse.getEntity();

        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        String expected = "New host meta added";

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_CREATED, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(2)
    public void testRetrieveHostMeta() {
        HostMeta hostMeta = new HostMeta();
        hostMeta.setHostId(1);
        hostMeta.setMetaKey("metaKey");
        hostMeta.setMetaValue("metaValue");

        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/hosts/definitions/1/metadata");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        ArrayList<HostMeta> hostMetas = new ArrayList<>();
        hostMetas.add(hostMeta);

        String json = gson.toJson(hostMetas);

        assertEquals(json, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());

    }

    @Test
    @Order(3)
    public void testNoHostForInsertingMeta() {
        HostMeta hostMeta = new HostMeta();
        hostMeta.setHostId(67);
        hostMeta.setMetaKey("metaKey");
        hostMeta.setMetaValue("metaValue");

        String hostMetaJson = gson.toJson(hostMeta);

        StringEntity requestEntity4 = new StringEntity(String.valueOf(hostMetaJson), ContentType.APPLICATION_JSON);

        HttpPut request4 = new HttpPut("http://localhost:" + port + "/v2/hosts/definitions/67/metadata");
        request4.setEntity(requestEntity4);
        request4.setHeader("Authorization", "Bearer " + token);

        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request4));

        HttpEntity entity = httpResponse.getEntity();

        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));
        String expected = "Record does not exist";

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_NOT_FOUND, httpResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(4)
    public void testGetAllHostMetas() {

        HostMeta hostMeta = new HostMeta();
        hostMeta.setHostId(1);
        hostMeta.setMetaKey("metaKey");
        hostMeta.setMetaValue("metaValue");

        ArrayList<HostMeta> expected = new ArrayList<>();

        expected.add(hostMeta);

        String json = gson.toJson(expected);

        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/hosts/definitions/1/metadata");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(json, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());
    }

    @Test
    @Order(5)
    public void testHostMetaGetByKey() {
        HostMeta hostMeta = new HostMeta();
        hostMeta.setHostId(1);
        hostMeta.setMetaKey("metaKey");
        hostMeta.setMetaValue("metaValue");

        ArrayList<HostMeta> expected = new ArrayList<>();
        expected.add(hostMeta);
        String expectedAsString = gson.toJson(expected);

        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/hosts/definitions/1/metadata?key=metaKey");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(expectedAsString, responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());

    }

    @Test
    @Order(6)
    public void testHostMetaMissingKey() {
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/hosts/definitions/1/metadata?key=missing");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        ArrayList<HostMeta> hostMetas = new ArrayList<>();
        // asserts empty list. Nothing is found with key that does not exist
        assertEquals(hostMetas.toString(), responseStringGet);
        assertEquals(HttpStatus.SC_OK, responseGet.getStatusLine().getStatusCode());

    }

    @Test
    @Order(7)
    public void testDeleteHostMetaKey() {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/hosts/definitions/1/metadata?key=metaKey");

        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        String expected = "Hostmeta deleted.";

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_OK, deleteResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(8)
    public void testDeleteHostMeta() {
        //insert another hostMeta for testing
        HostMeta hostMeta = new HostMeta();
        hostMeta.setMetaKey("metaKey");
        hostMeta.setMetaValue("metaValue");

        String hostMetaJson = gson.toJson(hostMeta);

        StringEntity requestEntity4 = new StringEntity(String.valueOf(hostMetaJson), ContentType.APPLICATION_JSON);

        HttpPut request4 = new HttpPut("http://localhost:" + port + "/v2/hosts/definitions/1/metadata");
        request4.setEntity(requestEntity4);
        request4.setHeader("Authorization", "Bearer " + token);

        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request4));

        HttpEntity entity = httpResponse.getEntity();

        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        String expected = "New host meta added";

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        assertEquals(expected, actual);
        assertEquals(HttpStatus.SC_CREATED, httpResponse.getStatusLine().getStatusCode());

        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/hosts/definitions/1/metadata");

        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String deleteResponseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        JSONObject deleteJson = Assertions.assertDoesNotThrow(() -> new JSONObject(deleteResponseString));

        String actualDelete = Assertions.assertDoesNotThrow(() -> deleteJson.get("message").toString());

        String expectedDelete = "Hostmeta deleted.";

        assertEquals(HttpStatus.SC_OK, deleteResponse.getStatusLine().getStatusCode());
        assertEquals(expectedDelete, actualDelete);
    }

}
