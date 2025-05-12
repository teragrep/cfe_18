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
public class HostControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;


    @Test
    @Order(1)
    public void testInsertRelpHost() throws Exception {

        HostRelp relpHost = new HostRelp();
        relpHost.setMd5("relpHostmd5");
        relpHost.setFqHost("relpHostfq");

        String json = gson.toJson(relpHost);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(
                String.valueOf(json),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/host/relp");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = new JSONObject(responseString);

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New host created with relp type";

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
    public void testGetRelpHost() throws Exception {

        HostRelp relpHost = new HostRelp();
        relpHost.setId(1);
        relpHost.setMd5("relpHostmd5");
        relpHost.setFqHost("relpHostfq");
        String json = gson.toJson(relpHost);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/host/relp/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(json, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }

    // insert cfe type host
    @Test
    @Order(3)
    public void testInsertCfeHost() throws Exception {
        // insert hub first
        // send one hub
        Hub hub1 = new Hub();
        hub1.setFqHost("hubfq");
        hub1.setMd5("hubmd5");
        hub1.setIp("hubip");

        String json1 = gson.toJson(hub1);

        // forms the json to requestEntity
        StringEntity requestEntity1 = new StringEntity(
                String.valueOf(json1),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request1 = new HttpPut("http://localhost:" + port + "/host/hub");
        // set requestEntity to the put request
        request1.setEntity(requestEntity1);
        // Header
        request1.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpClientBuilder.create().build().execute(request1);


        HostCfe host = new HostCfe();
        host.setMd5("randommd5value");
        host.setFqHost("hostFq");
        host.setHubFq("hubfq");

        String json = gson.toJson(host);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(
                String.valueOf(json),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/host/file");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = new JSONObject(responseString);

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New host created";

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Assertions
        assertEquals(expected, actual);
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));

    }


    // Host Meta side tests are here aswell because cfe type host requires hosts before retrieval


    // insert host meta test
    @Test
    @Order(4)
    public void testInsertHostMeta() throws Exception {

        HostMeta hostmeta = new HostMeta();
        hostmeta.setArch("arch1");
        hostmeta.setFlavor("flavor1");
        hostmeta.setHostname("hostname1");
        hostmeta.setHostId(3);
        hostmeta.setOs("linux");
        hostmeta.setReleaseVersion("release_version1");

        String json = gson.toJson(hostmeta);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(
                String.valueOf(json),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/host/meta");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = new JSONObject(responseString);

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New host meta created";

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Assertions
        assertEquals(expected, actual);
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));

    }


    // insert ip to host meta test
    @Test
    @Order(5)
    public void testInsertIp() throws Exception {
        IPAddress ip = new IPAddress();
        ip.setIpAddress("ip1");

        String json = gson.toJson(ip);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(
                String.valueOf(json),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/host/meta/ip");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);

        // Parsin respponse as JSONObject
        JSONObject responseAsJson = new JSONObject(responseString);

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New ip address created";

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Assertions
        assertEquals(expected, actual);
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));

    }

    // insert interface to host meta test
    @Test
    @Order(6)
    public void testInsertInterface() throws Exception {
        InterfaceType interfaceType = new InterfaceType();
        interfaceType.setInterfaceType("interface1");

        String json = gson.toJson(interfaceType);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(
                String.valueOf(json),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/host/meta/interface");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);

        // Parsin respponse as JSONObject

        JSONObject responseAsJson = new JSONObject(responseString);
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "New interface created";

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Assertions
        assertEquals(expected, actual);
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));

    }

    // link ip to host meta
    @Test
    @Order(7)
    public void testLinkIpToHostMeta() throws Exception {
        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/host/meta/ip/1/1");
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);

        // Parsin respponse as JSONObject

        JSONObject responseAsJson = new JSONObject(responseString);
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "IP linked to hostMeta";

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Assertions
        assertEquals(expected, actual);
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));

    }

    // link interface to host meta
    @Test
    @Order(8)
    public void testLinkInterfaceToHostMeta() throws Exception {

        HttpPut request = new HttpPut("http://localhost:" + port + "/host/meta/interface/1/1");
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString = EntityUtils.toString(entity);

        // Parsin respponse as JSONObject

        JSONObject responseAsJson = new JSONObject(responseString);
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Interface linked to hostMeta";

        // Creating string from Json that was given as a response
        String actual = responseAsJson.get("message").toString();

        // Assertions
        assertEquals(expected, actual);
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));

    }

    // retrieve all ip test
    @Test
    @Order(9)
    public void testGetIpaddress() throws Exception {

        ArrayList<IPAddress> expected = new ArrayList<>();
        IPAddress ip = new IPAddress();
        ip.setHostMetaId(1);
        ip.setIpAddress("ip1");

        expected.add(ip);
        String json = gson.toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/host/meta/ip");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(json, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    // retrieve all interface test
    @Test
    @Order(10)
    public void testGetInterface() throws Exception {
        ArrayList<InterfaceType> expected = new ArrayList<>();
        InterfaceType interfaceType = new InterfaceType();
        interfaceType.setHostMetaId(1);
        interfaceType.setInterfaceType("interface1");

        expected.add(interfaceType);
        String json = gson.toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/host/meta/interface");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(json, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }

    // retrieve all host meta test
    @Test
    @Order(11)
    public void testGetAllHostMetas() throws Exception {

        ArrayList<HostMeta> expected = new ArrayList<>();
        HostMeta hostMeta = new HostMeta();
        hostMeta.setId(1);
        hostMeta.setArch("arch1");
        hostMeta.setReleaseVersion("release_version1");
        hostMeta.setFlavor("flavor1");
        hostMeta.setOs("linux");
        hostMeta.setHostname("hostname1");
        hostMeta.setHostId(3);

        expected.add(hostMeta);
        String json = gson.toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/host/meta");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(json, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }


    // retrieve cfe based host
    @Test
    @Order(12)
    public void testGetCfeHost() throws Exception {

        HostCfe host = new HostCfe();
        host.setId(3);
        host.setMd5("randommd5value");
        host.setFqHost("hostFq");
        host.setHubId(1);
        host.setHubFq("hubfq");

        String json = gson.toJson(host);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/host/file/" + 3);

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(json, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    // retrieve all hosts

    @Test
    @Order(13)
    public void testGetAllCfeHosts() throws Exception {
        ArrayList<HostCfe> expectedListFile = new ArrayList<>();

        HostCfe host = new HostCfe();
        host.setId(3);
        host.setMd5("randommd5value");
        host.setFqHost("hostFq");
        host.setHubId(1);
        host.setHubFq("hubfq");

        HostCfe hub1 = new HostCfe();
        hub1.setId(2);
        hub1.setFqHost("hubfq");
        hub1.setMd5("hubmd5");
        hub1.setHubId(1);
        hub1.setHubFq("hubfq");

        expectedListFile.add(hub1);
        expectedListFile.add(host);
        String expectedJson = gson.toJson(expectedListFile);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/host/cfe");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(expectedJson, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }

    @Test
    @Order(14)
    public void testGetAllRelpHosts() throws Exception {

        ArrayList<HostRelp> expectedListFile = new ArrayList<>();

        HostRelp host2 = new HostRelp();
        host2.setId(1);
        host2.setMd5("relpHostmd5");
        host2.setFqHost("relpHostfq");

        expectedListFile.add(host2);
        String expectedJson = gson.toJson(expectedListFile);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/host/relp");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = HttpClientBuilder.create().build().execute(requestGet);

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = EntityUtils.toString(entityGet, "UTF-8");

        assertEquals(expectedJson, responseStringGet);
        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));

    }



    @Test
    @Order(15)
    public void testDeleteRelpHost() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/relp/" + 1);

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
        String expected = "Host deleted";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    @Order(16)
    public void testDeleteNonExistentCfeHost() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/cfe/" + 99999);

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
    @Order(17)
    public void testDeleteNonExistentRelpHost() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/relp/" + 99999);

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
    @Order(18)
    public void testDeleteNonExistentHostMeta() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/meta/" + 99999);

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
    @Order(19)
    public void testDeleteNonExistentHostMetaIp() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/meta/ip/" + 99999);

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
    @Order(20)
    public void testDeleteHostMetaIpInUse() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/meta/ip/1");

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
    @Order(21)
    public void testDeleteNonExistentHostMetaInterface() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/meta/ip/" + 99999);

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
    @Order(22)
    public void testDeleteHostMetaInterfaceInUse() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/meta/interface/1");

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

    // Delete

    @Test
    @Order(23)
    public void testDeleteHostMetaLinkIp() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/meta/ip/1/1" );

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
        String expected = "Ip link deleted";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    @Order(24)
    public void testDeleteHostMetaLinkInterface() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/meta/interface/1/1" );

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
        String expected = "Interface link deleted";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }
    @Test
    @Order(25)
    public void testDeleteHostMeta() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/meta/" + 1);

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
        String expected = "Hostmeta deleted";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }


    @Test
    @Order(26)
    public void testDeleteHostMetaIp() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/meta/ip/" + 1);

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
        String expected = "Ip deleted";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }

    @Test
    @Order(27)
    public void testDeleteHostMetaInterface() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/meta/interface/" + 1);


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
        String expected = "Interface deleted";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }


    @Test
    @Order(28)
    public void testDeleteCfeHost() throws Exception {
        HttpDelete delete = new HttpDelete("http://localhost:" + port + "/host/cfe/" + 3);

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
        String expected = "Host deleted";

        assertEquals(expected, actual);
        assertThat(deleteResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
    }



}
