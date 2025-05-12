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
public class GXGControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    public void testAddCaptureGroup() throws Exception {

        // Capture Group
        CaptureGroup captureGroup = new CaptureGroup();
        captureGroup.setCaptureGroupName("groupRelp");
        captureGroup.setCaptureGroupType(CaptureGroup.groupType.relp);

        String cgJson = gson.toJson(captureGroup);

        // forms the json to requestEntity
        StringEntity requestEntityCaptureGroup = new StringEntity(
                String.valueOf(cgJson),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/capture/group");
        // set requestEntity to the put request
        requestCaptureGroup.setEntity(requestEntityCaptureGroup);
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(requestCaptureGroup);

        // Assertion

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = new JSONObject(responseString);

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New capture group created";

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Assertions
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));
        assertEquals(expected, actual);


    }


    @Test
    @Order(2)
    public void testAddHostGroup() throws Exception {

        // Host Group
        HostGroup relpHostGroup = new HostGroup();
        relpHostGroup.setHostGroupName("hostgroup1");
        relpHostGroup.setHostGroupType(HostGroup.groupType.relp);

        String jsonGroup = gson.toJson(relpHostGroup);

        // forms the json to requestEntity
        StringEntity requestEntityGroup = new StringEntity(
                String.valueOf(jsonGroup),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut requestGroup = new HttpPut("http://localhost:" + port + "/host/group");
        // set requestEntity to the put request
        requestGroup.setEntity(requestEntityGroup);
        // Header
        requestGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(requestGroup);

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);


        // Parsin respponse as JSONObject
        JSONObject responseAsJson = new JSONObject(responseString);

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Host group created";

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Assertions
        assertEquals(expected, actual);
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));

    }

    @Test
    @Order(3)
    public void testAddLinkage() throws Exception {
        // Linkage
        Linkage linkage = new Linkage();
        linkage.setCaptureGroupId(1);
        linkage.setHostGroupId(1);

        String jsonGroup = gson.toJson(linkage);

        // forms the json to requestEntity
        StringEntity requestEntityGroup = new StringEntity(
                String.valueOf(jsonGroup),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut requestGroup = new HttpPut("http://localhost:" + port + "/capture/groups/linkage");
        // set requestEntity to the put request
        requestGroup.setEntity(requestEntityGroup);
        // Header
        requestGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(requestGroup);

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);


        // Parsin respponse as JSONObject
        JSONObject responseAsJson = new JSONObject(responseString);

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Linkage created";

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Assertions
        assertEquals(expected, actual);
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));
    }

    @Test
    @Order(4)
    public void testRetrieveCaptureGroup() throws Exception {
        CaptureGroup captureGroup = new CaptureGroup();
        captureGroup.setCaptureGroupType(CaptureGroup.groupType.relp);
        captureGroup.setId(1);
        captureGroup.setCaptureGroupName("groupRelp");

        String expectedJson = new Gson().toJson(captureGroup);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/group/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(expectedJson, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }

    @Test
    @Order(5)
    public void testRetrieveHostGroup() throws Exception {
        HostGroup hostGroup = new HostGroup();
        hostGroup.setHostId(0);
        hostGroup.setHostGroupName("hostgroup1");
        hostGroup.setHostGroupType(HostGroup.groupType.relp);
        hostGroup.setId(1);

        String expectedJson = new Gson().toJson(hostGroup);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/host/group/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(expectedJson, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }

    @Test
    @Order(6)
    public void testRetrieveLinkage() throws Exception {
        Linkage linkage = new Linkage();
        linkage.setId(1);
        linkage.setHostGroupId(1);
        linkage.setCaptureGroupId(1);

        String expectedJson = new Gson().toJson(linkage);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/groups/linkage/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(expectedJson, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }


    @Test
    @Order(7)
    public void testRetrieveAllCaptureGroups() throws Exception {
        ArrayList<CaptureGroup> expected = new ArrayList<>();
        CaptureGroup captureGroup = new CaptureGroup();
        captureGroup.setId(1);
        captureGroup.setCaptureGroupName("groupRelp");
        captureGroup.setCaptureGroupType(CaptureGroup.groupType.relp);

        expected.add(captureGroup);

        String expectedJson = new Gson().toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/group");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(expectedJson, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }


    @Test
    @Order(8)
    public void testRetrieveAllHostGroups() throws Exception {
        ArrayList<HostGroup> expected = new ArrayList<>();
        HostGroup hostGroup = new HostGroup();
        hostGroup.setHostGroupName("hostgroup1");
        hostGroup.setHostGroupType(HostGroup.groupType.relp);
        hostGroup.setId(1);

        expected.add(hostGroup);

        String expectedJson = new Gson().toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/host/group");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(expectedJson, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }

    @Test
    @Order(9)
    public void testRetrieveAllLinkages() throws Exception {

        ArrayList<Linkage> expected = new ArrayList<>();
        Linkage linkage = new Linkage();
        linkage.setId(1);
        linkage.setHostGroupId(1);
        linkage.setCaptureGroupId(1);

        expected.add(linkage);

        String expectedJson = new Gson().toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/capture/groups/linkage");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(expectedJson, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }

    // Delete

    @Test
    @Order(10)
    public void testDeleteCaptureGroupInUse() throws Exception {
        //groupRelp
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/capture/group/1");

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
    @Order(11)
    public void testDeleteNonExistentCaptureGroup() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/capture/group/125412");

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
    @Order(12)
    public void testDeleteHostGroupInUse() throws Exception {
        // hostgroup1
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/group/1");

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
    public void testDeleteNonExistentHostGroup() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/group/124");

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
    @Order(14)
    public void testDeleteNonExistentLinkage() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/capture/groups/linkage/125");

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
    @Order(15)
    public void testDeleteLinkage() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/capture/groups/linkage/1");

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
        String expected = "Linkage deleted";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }
    @Test
    @Order(16)
    public void testNoTwoTagsInCaptureGroup() throws Exception {
        // Insert another capture with the same tag
        CaptureRelp captureRelp = new CaptureRelp();
        captureRelp.setTag("relpTag");
        captureRelp.setRetentionTime("P30D");
        captureRelp.setCategory("audit2");
        captureRelp.setApplication("relp2");
        captureRelp.setIndex("audit_relp2");
        captureRelp.setSourceType("relpsource2");
        captureRelp.setProtocol("prot");
        captureRelp.setFlow("testflow1");

        String relpJson = gson.toJson(captureRelp);

        // forms the json to requestEntity
        StringEntity requestEntityCapture = new StringEntity(
                String.valueOf(relpJson),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut requestCapture = new HttpPut("http://localhost:" + port + "/capture/relp");
        // set requestEntity to the put request
        requestCapture.setEntity(requestEntityCapture);
        // Header
        requestCapture.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpClientBuilder.create().build().execute(requestCapture);

        // Try to insert new capture within same group and assert results

        // Creates the request
        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/capture/group/2/1");
        // set requestEntity to the put request
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(requestCaptureGroup);

        // Assertion

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = new JSONObject(responseString);

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Tag already exists within given group";

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Assertions
        assertEquals(expected, actual);
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_BAD_REQUEST));
    }



    @Test
    @Order(17)
    public void testDeleteCaptureGroup() throws Exception {
        //groupRelp
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/capture/group/1");

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
        String expected = "Capture group deleted";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    @Order(18)
    public void testDeleteHostGroup() throws Exception {
        //groupRelp
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/group/1");

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
        String expected = "Host Group deleted";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

}
