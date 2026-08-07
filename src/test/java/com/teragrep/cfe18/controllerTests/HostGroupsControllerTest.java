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
import com.teragrep.cfe18.handlers.entities.HostGroup;
import com.teragrep.cfe18.handlers.entities.IntegrationType;
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
public class HostGroupsControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    public void testAddHostGroup() {
        HostGroup relpHostGroup = new HostGroup();
        relpHostGroup.setHost_group_name("hostgroup1");
        relpHostGroup.setHost_group_type(IntegrationType.RELP);

        String jsonGroup = gson.toJson(relpHostGroup);

        StringEntity requestEntityGroup = new StringEntity(String.valueOf(jsonGroup), ContentType.APPLICATION_JSON);

        HttpPut requestGroup = new HttpPut("http://localhost:" + port + "/v2/hosts/groups");
        requestGroup.setEntity(requestEntityGroup);
        requestGroup.setHeader("Authorization", "Bearer " + token);

        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGroup));

        HttpEntity entity = httpResponse.getEntity();

        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        String expected = "New host group created";

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        assertEquals(expected, actual);
        assertEquals((HttpStatus.SC_CREATED), httpResponse.getStatusLine().getStatusCode());

    }

    @Test
    @Order(2)
    public void testAddHostGroupWithIndifferentType() {
        HostGroup relpHostGroup = new HostGroup();
        relpHostGroup.setHost_group_name("hostgroup1");
        relpHostGroup.setHost_group_type(IntegrationType.CFE);

        String jsonGroup = gson.toJson(relpHostGroup);

        StringEntity requestEntityGroup = new StringEntity(String.valueOf(jsonGroup), ContentType.APPLICATION_JSON);

        HttpPut requestGroup = new HttpPut("http://localhost:" + port + "/v2/hosts/groups");
        requestGroup.setEntity(requestEntityGroup);
        requestGroup.setHeader("Authorization", "Bearer " + token);

        HttpResponse httpResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGroup));

        HttpEntity entity = httpResponse.getEntity();

        String responseString = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        String expected = "Record already exists with different integration type";

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        assertEquals(expected, actual);
        assertEquals((HttpStatus.SC_CONFLICT), httpResponse.getStatusLine().getStatusCode());

    }

    @Test
    @Order(3)
    public void testRetrieveHostGroup() {
        HostGroup hostGroup = new HostGroup();
        hostGroup.setHost_group_name("hostgroup1");
        hostGroup.setId(1);
        hostGroup.setHost_group_type(IntegrationType.RELP);

        String expectedJson = new Gson().toJson(hostGroup);

        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/hosts/groups/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(expectedJson, responseStringGet);
        assertEquals((HttpStatus.SC_OK), responseGet.getStatusLine().getStatusCode());

    }

    @Test
    @Order(4)
    public void testRetrieveAllHostGroups() {
        ArrayList<HostGroup> expected = new ArrayList<>();
        HostGroup hostGroup = new HostGroup();
        hostGroup.setHost_group_name("hostgroup1");
        hostGroup.setId(1);
        hostGroup.setHost_group_type(IntegrationType.RELP);

        expected.add(hostGroup);

        String expectedJson = new Gson().toJson(expected);

        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/hosts/groups");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertEquals(expectedJson, responseStringGet);
        assertEquals((HttpStatus.SC_OK), responseGet.getStatusLine().getStatusCode());

    }

    @Test
    @Order(5)
    public void testDeleteHostGroupInUse() {
        TestApiClient testApiClient = new TestApiClient(port, token);
        Integer hostId = testApiClient.insertRelpHost("md5", "fq");
        Integer hostGroupId = testApiClient.insertHostToGroup(1, hostId);

        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/hosts/groups/" + hostGroupId);

        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());
        String expected = "Is in use";

        assertEquals(expected, actual);
        assertEquals((HttpStatus.SC_CONFLICT), deleteResponse.getStatusLine().getStatusCode());
    }

    @Test
    @Order(6)
    public void testDeleteHostGroup() {
        TestApiClient testApiClient = new TestApiClient(port, token);
        testApiClient.insertHostGroup("group2", IntegrationType.RELP);
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/hosts/groups/2");

        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        String expected = "Host Group deleted.";

        assertEquals((HttpStatus.SC_OK), deleteResponse.getStatusLine().getStatusCode());
        assertEquals(expected, actual);
    }

    @Test
    @Order(7)
    public void testDeleteNonExistentHostGroup() {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/v2/hosts/groups/124");

        delete.setHeader("Authorization", "Bearer " + token);

        HttpResponse deleteResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(delete));

        HttpEntity entityDelete = deleteResponse.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityDelete, "UTF-8"));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        String expected = "Record does not exist";

        assertEquals((HttpStatus.SC_NOT_FOUND), deleteResponse.getStatusLine().getStatusCode());
        assertEquals(expected, actual);
    }

    @Test
    @Order(8)
    public void testRetrieveNonExistentHostGroup() {
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/v2/hosts/groups/67");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        JSONObject responseAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringGet));

        String actual = Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        String expected = "Record does not exist";

        assertEquals(expected, actual);
        assertEquals((HttpStatus.SC_NOT_FOUND), responseGet.getStatusLine().getStatusCode());

    }

}
