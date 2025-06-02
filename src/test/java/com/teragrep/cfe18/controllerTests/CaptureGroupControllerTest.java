package com.teragrep.cfe18.controllerTests;

import com.google.gson.Gson;
import com.teragrep.cfe18.handlers.entities.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
import org.springframework.context.annotation.Description;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MigrateDatabaseExtension.class)
public class CaptureGroupControllerTest extends TestSpringBootInformation {


    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    @Description("Insert 2 captures and 2 groups for testing")
    public void testData() {
        // insert flow before sink so database has something to stick to.
        Flow flow = new Flow();
        flow.setName("testflow1");
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

        HttpResponse httpResponseFlow = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request2));

        // Get the entity from response
        HttpEntity entityFlow = httpResponseFlow.getEntity();

        // Entity response string
        String responseStringFlow = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityFlow));

        // Parsing response as JSONObject
        JSONObject responseAsJsonFlow = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringFlow));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedFlow = "New flow created";

        // Creating string from Json that was given as a response
        String actualFlow = Assertions.assertDoesNotThrow(() -> responseAsJsonFlow.get("message").toString());


        // insert sink
        Sink sink = new Sink();
        sink.setFlow("testflow1");
        sink.setPort("601");
        sink.setIp_address("ip1");
        sink.setProtocol("prot");

        String json = gson.toJson(sink);

        // forms the json to requestEntity
        StringEntity requestEntity = new StringEntity(
                String.valueOf(json),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut request = new HttpPut("http://localhost:" + port + "/sink/details");
        // set requestEntity to the put request
        request.setEntity(requestEntity);
        // Header
        request.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponseSink = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(request));

        // Get the entity from response
        HttpEntity entitySink = httpResponseSink.getEntity();

        // Entity response string
        String responseStringSink = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entitySink));

        // Parsing response as JSONObject
        JSONObject responseAsJsonSink = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringSink));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedSink = "New sink created";

        // Creating string from Json that was given as a response
        String actualSink = Assertions.assertDoesNotThrow(() -> responseAsJsonSink.get("message").toString());

        // Capture
        CaptureRelp captureRelp = new CaptureRelp();
        captureRelp.setTag("relpTag");
        captureRelp.setRetentionTime("P30D");
        captureRelp.setCategory("audit");
        captureRelp.setApplication("relp");
        captureRelp.setIndex("audit_relp");
        captureRelp.setSourceType("relpsource1");
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
        HttpResponse httpResponseCapture1 = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCapture));

        // Get the entity from response
        HttpEntity entityCapture1 = httpResponseCapture1.getEntity();

        // Entity response string
        String responseStringCapture1 = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityCapture1));

        // Parsing response as JSONObject
        JSONObject responseAsJsonCapture1 = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringCapture1));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedCapture1 = "New capture created";

        // Creating string from Json that was given as a response
        String actualCapture1 = Assertions.assertDoesNotThrow(() -> responseAsJsonCapture1.get("message").toString());

        // Capture
        CaptureRelp captureRelp2 = new CaptureRelp();
        captureRelp2.setTag("relpTag2");
        captureRelp2.setRetentionTime("P30D");
        captureRelp2.setCategory("audit");
        captureRelp2.setApplication("relp");
        captureRelp2.setIndex("audit_relp");
        captureRelp2.setSourceType("relpsource1");
        captureRelp2.setProtocol("prot");
        captureRelp2.setFlow("testflow1");

        String relpJson2 = gson.toJson(captureRelp2);

        // forms the json to requestEntity
        StringEntity requestEntityCapture2 = new StringEntity(
                String.valueOf(relpJson2),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut requestCapture2 = new HttpPut("http://localhost:" + port + "/capture/relp");
        // set requestEntity to the put request
        requestCapture2.setEntity(requestEntityCapture2);
        // Header
        requestCapture2.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponseCapture2 = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCapture));

        // Get the entity from response
        HttpEntity entityCapture2 = httpResponseCapture2.getEntity();

        // Entity response string
        String responseStringCapture2 = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityCapture2));

        // Parsing response as JSONObject
        JSONObject responseAsJsonCapture2 = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringCapture2));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedCapture2 = "New capture created";

        // Creating string from Json that was given as a response
        String actualCapture2 = Assertions.assertDoesNotThrow(() -> responseAsJsonCapture2.get("message").toString());

        // Capture group 1
        CaptureGroups captureGroup = new CaptureGroups();
        captureGroup.setCaptureGroupName("captureGroup1");
        captureGroup.setCaptureGroupType(CaptureGroups.groupType.relp);

        String captureGroupJson = gson.toJson(captureGroup);

        // forms the json to requestEntity
        StringEntity requestEntityCaptureGroup = new StringEntity(
                String.valueOf(captureGroupJson),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/groups/capture");
        // set requestEntity to the put request
        requestCaptureGroup.setEntity(requestEntityCaptureGroup);
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponseCaptureGroup = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup));

        // Get the entity from response
        HttpEntity entityCaptureGroup = httpResponseCaptureGroup.getEntity();

        // Entity response string
        String responseStringCaptureGroup = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityCaptureGroup));

        // Parsing response as JSONObject
        JSONObject responseAsJsonCaptureGroup = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringCaptureGroup));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedCaptureGroup = "New capture group created";

        // Creating string from Json that was given as a response
        String actualCaptureGroup = Assertions.assertDoesNotThrow(() -> responseAsJsonCaptureGroup.get("message").toString());

        // Capture group 2
        CaptureGroup CaptureGroup2 = new CaptureGroup();
        CaptureGroup2.setCaptureGroupName("captureGroup2");
        CaptureGroup2.setCaptureGroupType(CaptureGroup.groupType.relp);

        String CaptureGroup2Json = gson.toJson(CaptureGroup2);

        // forms the json to requestEntity
        StringEntity requestEntityCaptureGroup2 = new StringEntity(
                String.valueOf(CaptureGroup2Json),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut requestCaptureGroup2 = new HttpPut("http://localhost:" + port + "/groups/capture");
        // set requestEntity to the put request
        requestCaptureGroup2.setEntity(requestEntityCaptureGroup2);
        // Header
        requestCaptureGroup2.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponseCaptureGroup2 = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup2));

        // Get the entity from response
        HttpEntity entityCaptureGroup2 = httpResponseCaptureGroup2.getEntity();

        // Entity response string
        String responseStringCaptureGroup2 = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityCaptureGroup2));

        // Parsing response as JSONObject
        JSONObject responseAsJsonCaptureGroup2 = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringCaptureGroup2));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedCaptureGroup2 = "New capture group created";

        // Creating string from Json that was given as a response
        String actualCaptureGroup2 = Assertions.assertDoesNotThrow(() -> responseAsJsonCaptureGroup2.get("message").toString());


        // Assertions   
        assertThat(
                httpResponseFlow.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));
        assertEquals(expectedFlow, actualFlow);
        // Assertions
        assertThat(
                httpResponseSink.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));
        assertEquals(expectedSink, actualSink);
        assertThat(
                httpResponseCapture1.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));
        assertEquals(expectedCapture1, actualCapture1);
        assertThat(
                httpResponseCapture2.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));
        assertEquals(expectedCapture2, actualCapture2);
        assertThat(
                httpResponseCaptureGroup.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));
        assertEquals(expectedCaptureGroup, actualCaptureGroup);
        assertThat(
                httpResponseCaptureGroup2.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));
        assertEquals(expectedCaptureGroup2, actualCaptureGroup2);
    }

    @Test
    @Order(2)
    public void testInsertCaptureToGroup() {
        CaptureGroup CaptureGroup = new CaptureGroup();
        CaptureGroup.setId(1);
        CaptureGroup.setCaptureDefinitionId(1);

        String CaptureGroupJson = gson.toJson(CaptureGroup);

        // forms the json to requestEntity
        StringEntity requestEntityCaptureGroup = new StringEntity(
                String.valueOf(CaptureGroupJson),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/group/captures");
        // set requestEntity to the put request
        requestCaptureGroup.setEntity(requestEntityCaptureGroup);
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponseCaptureGroup = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup));

        // Get the entity from response
        HttpEntity entityCaptureGroup = httpResponseCaptureGroup.getEntity();

        // Entity response string
        String responseStringCaptureGroup = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityCaptureGroup));

        // Parsing response as JSONObject
        JSONObject responseAsJsonCaptureGroup = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringCaptureGroup));
        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expectedCaptureGroup = "Capture linked with group";

        // Creating string from Json that was given as a response
        String actualCaptureGroup = Assertions.assertDoesNotThrow(() -> responseAsJsonCaptureGroup.get("message").toString());

        assertThat(
                httpResponseCaptureGroup.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CREATED));
        assertEquals(expectedCaptureGroup, actualCaptureGroup);
    }

    @Test
    @Order(3)
    public void testGetCapturesInGroup() {
        ArrayList<CaptureGroup> expected = new ArrayList<>();
        CaptureGroup captureGroup = new CaptureGroup();
        captureGroup.setCaptureDefinitionId(1);

        expected.add(captureGroup);

        String expectedJson = new Gson().toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/group/captures/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertEquals(expectedJson, responseStringGet);
    }

    @Test
    @Order(4)
    public void testGetAllCapturesWithGroups() {
        ArrayList<CaptureGroup> expected = new ArrayList<>();
        CaptureGroup captureGroup = new CaptureGroup();
        captureGroup.setCaptureDefinitionId(1);

        expected.add(captureGroup);

        String expectedJson = new Gson().toJson(expected);

        // Asserting get request
        HttpGet requestGet = new HttpGet("http://localhost:" + port + "/group/captures/1");

        requestGet.setHeader("Authorization", "Bearer " + token);

        HttpResponse responseGet = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestGet));

        HttpEntity entityGet = responseGet.getEntity();

        String responseStringGet = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityGet, "UTF-8"));

        assertThat(responseGet.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertEquals(expectedJson, responseStringGet);
    }

    @Test
    @Order(5)
    public void testNoTwoTagsInCaptureGroup() {
        // Insert another capture with the same tag
        CaptureRelp captureRelp = new CaptureRelp();
        captureRelp.setTag("relpTag");
        captureRelp.setRetentionTime("P30D2");
        captureRelp.setCategory("audit22");
        captureRelp.setApplication("relp22");
        captureRelp.setIndex("audit_relp22");
        captureRelp.setSourceType("relpsource22");
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
        HttpResponse httpResponseCapture2 = Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCapture));

        // Get the entity from response
        HttpEntity entityCapture2 = httpResponseCapture2.getEntity();

        // Entity response string
        String responseStringCapture2 = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entityCapture2));

        // Parsing response as JSONObject
        JSONObject responseAsJsonCapture2 = Assertions.assertDoesNotThrow(() -> new JSONObject(responseStringCapture2));
        // Creating expected message as JSON Object from the data that was sent towards endpoint

        // Creating string from Json that was given as a response
        int captureId = Assertions.assertDoesNotThrow(() -> responseAsJsonCapture2.getInt("id"));

        // Try to insert new capture within same group and assert results

        // Capture Group
        CaptureGroup captureGroup = new CaptureGroup();
        captureGroup.setId(1);
        captureGroup.setCaptureDefinitionId(captureId);

        String cgJson = gson.toJson(captureGroup);

        // forms the json to requestEntity
        StringEntity requestEntityCaptureGroup = new StringEntity(
                String.valueOf(cgJson),
                ContentType.APPLICATION_JSON);

        // Creates the request
        HttpPut requestCaptureGroup = new HttpPut("http://localhost:" + port + "/group/captures");
        // set requestEntity to the put request
        requestCaptureGroup.setEntity(requestEntityCaptureGroup);
        // Header
        requestCaptureGroup.setHeader("Authorization", "Bearer " + token);

        // Get the response from endpoint
        HttpResponse httpResponse =  Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup));



        // Assertion

        // Get the entity from response
        HttpEntity entity = httpResponse.getEntity();

        // Entity response string
        String responseString =Assertions.assertDoesNotThrow(() -> EntityUtils.toString(entity));

        // Parsing response as JSONObject
        JSONObject responseAsJson =Assertions.assertDoesNotThrow(() -> new JSONObject(responseString));

        // Creating expected message as JSON Object from the data that was sent towards endpoint
        String expected = "Tag already exists within given group";

        // Creating string from Json that was given as a response
        String actual =Assertions.assertDoesNotThrow(() -> responseAsJson.get("message").toString());

        // Assertions
        assertEquals(expected, actual);
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_CONFLICT));
    }

}
