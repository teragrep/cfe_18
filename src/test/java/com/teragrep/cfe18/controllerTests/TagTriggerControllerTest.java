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

import static org.junit.jupiter.api.Assertions.assertEquals;

// Integration test to test out the functionality for tag trigger
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MigrateDatabaseExtension.class)
public class TagTriggerControllerTest extends TestSpringBootInformation {

    Gson gson = new Gson();

    @LocalServerPort
    private int port;

    @Test
    @BeforeAll
    public void testData() {
        Flow flow = new Flow();
        flow.setName("capflow");
        String flowJson = gson.toJson(flow);

        StringEntity flowRequestEntity = new StringEntity(String.valueOf(flowJson), ContentType.APPLICATION_JSON);

        HttpPut flowRequest = new HttpPut("http://localhost:" + port + "/flow");
        flowRequest.setEntity(flowRequestEntity);
        flowRequest.setHeader("Authorization", "Bearer " + token);

        HttpResponse flowResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(flowRequest));

        HttpEntity flowEntity = flowResponse.getEntity();

        String flowAsResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(flowEntity));

        JSONObject flowAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(flowAsResponse));

        String flowAsActual = Assertions.assertDoesNotThrow(() -> flowAsJson.get("message").toString());

        String flowAsExpected = "New flow created";

        Sink sink = new Sink();
        sink.setFlowId(1);
        sink.setPort("555");
        sink.setIpAddress("ip");
        sink.setProtocol("prot");

        String sinkJson = gson.toJson(sink);

        StringEntity sinkEntity = new StringEntity(String.valueOf(sinkJson), ContentType.APPLICATION_JSON);

        HttpPut sinkRequest = new HttpPut("http://localhost:" + port + "/sink");
        sinkRequest.setEntity(sinkEntity);
        sinkRequest.setHeader("Authorization", "Bearer " + token);

        HttpResponse sinkResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(sinkRequest));

        HttpEntity sinkAsEntity = sinkResponse.getEntity();

        String sinkAsResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(sinkAsEntity));

        JSONObject sinkAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(sinkAsResponse));

        String sinkAsExpected = "New sink created";

        String sinkAsActual = Assertions.assertDoesNotThrow(() -> sinkAsJson.get("message").toString());

        assertEquals(flowAsExpected, flowAsActual);
        assertEquals(HttpStatus.SC_CREATED, flowResponse.getStatusLine().getStatusCode());
        assertEquals(sinkAsExpected, sinkAsActual);
        assertEquals(HttpStatus.SC_CREATED, sinkResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void testTagTriggerOnError() {

        HostRelp relpHost1 = new HostRelp();
        relpHost1.setMd5("relpHostmd51");
        relpHost1.setFqHost("relpHostfq1");

        String relpHostJson1 = gson.toJson(relpHost1);

        StringEntity relpHostStringEntity1 = new StringEntity(
                String.valueOf(relpHostJson1),
                ContentType.APPLICATION_JSON
        );

        HttpPut relpHostPutRequest1 = new HttpPut("http://localhost:" + port + "/host/relp");
        relpHostPutRequest1.setEntity(relpHostStringEntity1);
        relpHostPutRequest1.setHeader("Authorization", "Bearer " + token);

        HttpResponse relpHostResponse1 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(relpHostPutRequest1));

        HttpEntity relpHostResponse1Entity = relpHostResponse1.getEntity();

        String relpHostResponseString1 = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(relpHostResponse1Entity));

        JSONObject relpHostResponseJsonObject1 = Assertions
                .assertDoesNotThrow(() -> new JSONObject(relpHostResponseString1));

        String relpHost1expected = "New host created with relp type";

        String relpHost1actual = Assertions
                .assertDoesNotThrow(() -> relpHostResponseJsonObject1.get("message").toString());

        HostRelp relpHost2 = new HostRelp();
        relpHost2.setMd5("relpHostmd52");
        relpHost2.setFqHost("relpHostfq2");

        String relpHostJson2 = gson.toJson(relpHost2);

        StringEntity relpHostStringEntity2 = new StringEntity(
                String.valueOf(relpHostJson2),
                ContentType.APPLICATION_JSON
        );

        HttpPut relpHostPutRequest2 = new HttpPut("http://localhost:" + port + "/host/relp");
        relpHostPutRequest2.setEntity(relpHostStringEntity2);
        relpHostPutRequest2.setHeader("Authorization", "Bearer " + token);

        HttpResponse relpHostResponse2 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(relpHostPutRequest2));

        HttpEntity relpHostResponse2Entity = relpHostResponse2.getEntity();

        String relpHostResponseString2 = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(relpHostResponse2Entity));

        JSONObject relpHostResponseJsonObject2 = Assertions
                .assertDoesNotThrow(() -> new JSONObject(relpHostResponseString2));

        String relpHost2expected = "New host created with relp type";

        String relpHost2actual = Assertions
                .assertDoesNotThrow(() -> relpHostResponseJsonObject2.get("message").toString());

        HostGroup relpHostGroup1 = new HostGroup();
        relpHostGroup1.setHost_id(1);
        relpHostGroup1.setHost_group_name("hostgroup1");

        String hostGroup1 = gson.toJson(relpHostGroup1);

        StringEntity hostGroupEntity1 = new StringEntity(String.valueOf(hostGroup1), ContentType.APPLICATION_JSON);

        HttpPut hostGroupPutRequest1 = new HttpPut("http://localhost:" + port + "/host/group");
        hostGroupPutRequest1.setEntity(hostGroupEntity1);
        hostGroupPutRequest1.setHeader("Authorization", "Bearer " + token);

        HttpResponse hostGroupResponse1 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(hostGroupPutRequest1));

        HttpEntity hostGroupResponseEntity1 = hostGroupResponse1.getEntity();

        String hostGroupResponseString1 = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(hostGroupResponseEntity1));

        JSONObject hostGroupResponseJson1 = Assertions
                .assertDoesNotThrow(() -> new JSONObject(hostGroupResponseString1));

        String hostGroupExpected1 = "New host group created with name = hostgroup1";

        String hostGroupActual1 = Assertions.assertDoesNotThrow(() -> hostGroupResponseJson1.get("message").toString());

        HostGroup relpHostGroup2 = new HostGroup();
        relpHostGroup2.setHost_id(2);
        relpHostGroup2.setHost_group_name("hostgroup2");

        String hostGroup2 = gson.toJson(relpHostGroup2);

        StringEntity hostGroupEntity2 = new StringEntity(String.valueOf(hostGroup2), ContentType.APPLICATION_JSON);

        HttpPut hostGroupPutRequest2 = new HttpPut("http://localhost:" + port + "/host/group");
        hostGroupPutRequest2.setEntity(hostGroupEntity2);
        hostGroupPutRequest2.setHeader("Authorization", "Bearer " + token);

        HttpResponse hostGroupResponse2 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(hostGroupPutRequest2));

        HttpEntity hostGroupResponseEntity2 = hostGroupResponse2.getEntity();

        String hostGroupResponseString2 = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(hostGroupResponseEntity2));

        JSONObject hostGroupResponseJson2 = Assertions
                .assertDoesNotThrow(() -> new JSONObject(hostGroupResponseString2));

        String hostGroupExpected2 = "New host group created with name = hostgroup2";

        String hostGroupActual2 = Assertions.assertDoesNotThrow(() -> hostGroupResponseJson2.get("message").toString());

        HostGroup relpHostGroup3 = new HostGroup();
        relpHostGroup3.setHost_id(1);
        relpHostGroup3.setHost_group_name("hostgroup2");

        String hostGroup3 = gson.toJson(relpHostGroup3);

        StringEntity hostGroupEntity3 = new StringEntity(String.valueOf(hostGroup3), ContentType.APPLICATION_JSON);

        HttpPut hostGroupPutRequest3 = new HttpPut("http://localhost:" + port + "/host/group");
        hostGroupPutRequest3.setEntity(hostGroupEntity3);
        hostGroupPutRequest3.setHeader("Authorization", "Bearer " + token);

        HttpResponse hostGroupResponse3 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(hostGroupPutRequest3));

        HttpEntity hostGroupResponseEntity3 = hostGroupResponse3.getEntity();

        String hostGroupResponseString3 = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(hostGroupResponseEntity3));

        JSONObject hostGroupResponseJson3 = Assertions
                .assertDoesNotThrow(() -> new JSONObject(hostGroupResponseString3));

        String hostGroupExpected3 = "New host group created with name = hostgroup2";

        String hostGroupActual3 = Assertions.assertDoesNotThrow(() -> hostGroupResponseJson3.get("message").toString());

        CaptureGroups captureGroup1 = new CaptureGroups();
        captureGroup1.setCaptureGroupName("groupRelp1");
        captureGroup1.setCaptureGroupType(IntegrationType.RELP);
        captureGroup1.setFlowId(1);

        String relpCaptureGroup1 = gson.toJson(captureGroup1);

        StringEntity captureGroupRequestEntity1 = new StringEntity(
                String.valueOf(relpCaptureGroup1),
                ContentType.APPLICATION_JSON
        );

        HttpPut requestCaptureGroup1 = new HttpPut("http://localhost:" + port + "/v2/captures/group");
        requestCaptureGroup1.setEntity(captureGroupRequestEntity1);
        requestCaptureGroup1.setHeader("Authorization", "Bearer " + token);

        HttpResponse captureGroupResponse1 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup1));

        HttpEntity captureGroupEntity1 = captureGroupResponse1.getEntity();

        String captureGroupAsResponse1 = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(captureGroupEntity1));

        JSONObject captureGroupAsJson1 = Assertions.assertDoesNotThrow(() -> new JSONObject(captureGroupAsResponse1));

        String captureGroupExpected1 = "New capture group created";

        String captureGroupActual1 = Assertions.assertDoesNotThrow(() -> captureGroupAsJson1.get("message").toString());

        CaptureGroups captureGroup2 = new CaptureGroups();
        captureGroup2.setCaptureGroupName("groupRelp2");
        captureGroup2.setCaptureGroupType(IntegrationType.RELP);
        captureGroup2.setFlowId(1);

        String relpCaptureGroup2 = gson.toJson(captureGroup2);

        StringEntity captureGroupRequestEntity2 = new StringEntity(
                String.valueOf(relpCaptureGroup2),
                ContentType.APPLICATION_JSON
        );

        HttpPut requestCaptureGroup2 = new HttpPut("http://localhost:" + port + "/v2/captures/group");
        requestCaptureGroup2.setEntity(captureGroupRequestEntity2);
        requestCaptureGroup2.setHeader("Authorization", "Bearer " + token);

        HttpResponse captureGroupResponse2 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroup2));

        HttpEntity captureGroupEntity2 = captureGroupResponse2.getEntity();

        String captureGroupAsResponse2 = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(captureGroupEntity2));

        JSONObject captureGroupAsJson2 = Assertions.assertDoesNotThrow(() -> new JSONObject(captureGroupAsResponse2));

        String captureGroupExpected2 = "New capture group created";

        String captureGroupActual2 = Assertions.assertDoesNotThrow(() -> captureGroupAsJson2.get("message").toString());

        Linkage linkage1 = new Linkage();
        linkage1.setCapture_group_id(1);
        linkage1.setHost_group_id(1);

        String relpLinkage1 = gson.toJson(linkage1);

        StringEntity linkageRequestEntity1 = new StringEntity(
                String.valueOf(relpLinkage1),
                ContentType.APPLICATION_JSON
        );

        HttpPut requestLinkage1 = new HttpPut("http://localhost:" + port + "/capture/groups/linkage");
        requestLinkage1.setEntity(linkageRequestEntity1);
        requestLinkage1.setHeader("Authorization", "Bearer " + token);

        HttpResponse linkageResponse1 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestLinkage1));

        HttpEntity linkageResponse1Entity = linkageResponse1.getEntity();

        String linkageResponseString1 = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(linkageResponse1Entity));

        JSONObject linkageJsonResponse1 = Assertions.assertDoesNotThrow(() -> new JSONObject(linkageResponseString1));

        String linkageExpected1 = "New linkage created for groups = groupRelp1 and hostgroup1";

        String linkageActual1 = Assertions.assertDoesNotThrow(() -> linkageJsonResponse1.get("message").toString());

        Linkage linkage2 = new Linkage();
        linkage2.setCapture_group_id(1);
        linkage2.setHost_group_id(2);

        String relpLinkage2 = gson.toJson(linkage2);

        StringEntity linkageRequestEntity2 = new StringEntity(
                String.valueOf(relpLinkage2),
                ContentType.APPLICATION_JSON
        );

        HttpPut requestLinkage2 = new HttpPut("http://localhost:" + port + "/capture/groups/linkage");
        requestLinkage2.setEntity(linkageRequestEntity2);
        requestLinkage2.setHeader("Authorization", "Bearer " + token);

        HttpResponse linkageResponse2 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestLinkage2));

        HttpEntity linkageResponse2Entity = linkageResponse2.getEntity();

        String linkageResponseString2 = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(linkageResponse2Entity));

        JSONObject linkageJsonResponse2 = Assertions.assertDoesNotThrow(() -> new JSONObject(linkageResponseString2));

        String linkageExpected2 = "New linkage created for groups = groupRelp1 and hostgroup2";

        String linkageActual2 = Assertions.assertDoesNotThrow(() -> linkageJsonResponse2.get("message").toString());

        CaptureRelp captureRelp1 = new CaptureRelp();
        captureRelp1.setTag("relpTag1");
        captureRelp1.setRetentionTime("P30D");
        captureRelp1.setCategory("audit");
        captureRelp1.setApplication("relp");
        captureRelp1.setIndex("audit_relp");
        captureRelp1.setSourceType("relpsource1");
        captureRelp1.setProtocol("prot");
        captureRelp1.setFlow("capFlow");

        String relpCapture1 = gson.toJson(captureRelp1);

        StringEntity relpCaptureEntity1 = new StringEntity(String.valueOf(relpCapture1), ContentType.APPLICATION_JSON);

        HttpPut captureRequestEntity1 = new HttpPut(
                "http://localhost:" + port + "/v2/captures/definitions/relp-streams"
        );
        captureRequestEntity1.setEntity(relpCaptureEntity1);
        captureRequestEntity1.setHeader("Authorization", "Bearer " + token);

        HttpResponse captureResponse1 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(captureRequestEntity1));

        HttpEntity captureResponse1Entity = captureResponse1.getEntity();

        String captureResponseEntity1 = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(captureResponse1Entity));

        JSONObject captureResponseJson1 = Assertions.assertDoesNotThrow(() -> new JSONObject(captureResponseEntity1));

        String captureExpected1 = "New capture created";

        String captureActual1 = Assertions.assertDoesNotThrow(() -> captureResponseJson1.get("message").toString());

        CaptureRelp captureRelp2 = new CaptureRelp();
        captureRelp2.setTag("relpTag2");
        captureRelp2.setRetentionTime("P30D");
        captureRelp2.setCategory("audit");
        captureRelp2.setApplication("relp");
        captureRelp2.setIndex("audit_relp");
        captureRelp2.setSourceType("relpsource2");
        captureRelp2.setProtocol("prot");
        captureRelp2.setFlow("capFlow");

        String relpCapture2 = gson.toJson(captureRelp2);

        StringEntity relpCaptureEntity2 = new StringEntity(String.valueOf(relpCapture2), ContentType.APPLICATION_JSON);

        HttpPut captureRequestEntity2 = new HttpPut(
                "http://localhost:" + port + "/v2/captures/definitions/relp-streams"
        );
        captureRequestEntity2.setEntity(relpCaptureEntity2);
        captureRequestEntity2.setHeader("Authorization", "Bearer " + token);

        HttpResponse captureResponse2 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(captureRequestEntity2));

        HttpEntity captureResponse2Entity = captureResponse2.getEntity();

        String captureResponseEntity2 = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(captureResponse2Entity));

        JSONObject captureResponseJson2 = Assertions.assertDoesNotThrow(() -> new JSONObject(captureResponseEntity2));

        String captureExpected2 = "New capture created";

        String captureActual2 = Assertions.assertDoesNotThrow(() -> captureResponseJson2.get("message").toString());

        // actual testing
        final int captureId = 1;

        HttpPut requestCaptureGroupMemberHeader1 = new HttpPut(
                "http://localhost:" + port + "/v2/captures/groups/1/members"
        );

        requestCaptureGroupMemberHeader1
                .setEntity(new StringEntity(String.valueOf(captureId), ContentType.APPLICATION_JSON));

        requestCaptureGroupMemberHeader1.setHeader("Authorization", "Bearer " + token);

        HttpResponse captureGroupMemberResponse1 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroupMemberHeader1));

        HttpEntity captureGroupMemberResponse1Entity = captureGroupMemberResponse1.getEntity();

        String captureGroupMemberResponseString1 = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(captureGroupMemberResponse1Entity));

        JSONObject captureGroupMemberJsonResponse1 = Assertions
                .assertDoesNotThrow(() -> new JSONObject(captureGroupMemberResponseString1));

        String captureGroupMemberExpected1 = "Capture linked with group";

        String captureGroupMemberActual1 = Assertions
                .assertDoesNotThrow(() -> captureGroupMemberJsonResponse1.get("message").toString());

        final int captureId2 = 2;

        HttpPut requestCaptureGroupMemberHeader2 = new HttpPut(
                "http://localhost:" + port + "/v2/captures/groups/1/members"
        );

        requestCaptureGroupMemberHeader2
                .setEntity(new StringEntity(String.valueOf(captureId2), ContentType.APPLICATION_JSON));

        requestCaptureGroupMemberHeader2.setHeader("Authorization", "Bearer " + token);

        HttpResponse captureGroupMemberResponse2 = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(requestCaptureGroupMemberHeader2));

        HttpEntity captureGroupMemberResponse2Entity = captureGroupMemberResponse2.getEntity();

        String captureGroupMemberResponseString2 = Assertions
                .assertDoesNotThrow(() -> EntityUtils.toString(captureGroupMemberResponse2Entity));

        JSONObject captureGroupMemberJsonResponse2 = Assertions
                .assertDoesNotThrow(() -> new JSONObject(captureGroupMemberResponseString2));

        String captureGroupMemberExpected2 = "Tag already exists on the same host through different channels";

        String captureGroupMemberActual2 = Assertions
                .assertDoesNotThrow(() -> captureGroupMemberJsonResponse2.get("message").toString());

        // assertions
        assertEquals(relpHost1expected, relpHost1actual);
        assertEquals(HttpStatus.SC_CREATED, relpHostResponse1.getStatusLine().getStatusCode());
        assertEquals(relpHost2expected, relpHost2actual);
        assertEquals(HttpStatus.SC_CREATED, relpHostResponse2.getStatusLine().getStatusCode());
        assertEquals(hostGroupExpected1, hostGroupActual1);
        assertEquals(HttpStatus.SC_CREATED, hostGroupResponse1.getStatusLine().getStatusCode());
        assertEquals(hostGroupExpected2, hostGroupActual2);
        assertEquals(HttpStatus.SC_CREATED, hostGroupResponse2.getStatusLine().getStatusCode());
        assertEquals(hostGroupExpected3, hostGroupActual3);
        assertEquals(HttpStatus.SC_CREATED, hostGroupResponse3.getStatusLine().getStatusCode());
        assertEquals(captureGroupExpected1, captureGroupActual1);
        assertEquals(HttpStatus.SC_CREATED, captureGroupResponse1.getStatusLine().getStatusCode());
        assertEquals(captureGroupExpected2, captureGroupActual2);
        assertEquals(HttpStatus.SC_CREATED, captureGroupResponse2.getStatusLine().getStatusCode());
        assertEquals(linkageExpected1, linkageActual1);
        assertEquals(HttpStatus.SC_CREATED, linkageResponse1.getStatusLine().getStatusCode());
        assertEquals(linkageExpected2, linkageActual2);
        assertEquals(HttpStatus.SC_CREATED, linkageResponse2.getStatusLine().getStatusCode());
        assertEquals(captureExpected1, captureActual1);
        assertEquals(HttpStatus.SC_CREATED, captureResponse1.getStatusLine().getStatusCode());
        assertEquals(captureExpected2, captureActual2);
        assertEquals(HttpStatus.SC_CREATED, captureResponse2.getStatusLine().getStatusCode());
        assertEquals(captureGroupMemberExpected1, captureGroupMemberActual1);
        assertEquals(HttpStatus.SC_CREATED, captureGroupMemberResponse1.getStatusLine().getStatusCode());
        assertEquals(captureGroupMemberExpected2, captureGroupMemberActual2);
        assertEquals(HttpStatus.SC_CONFLICT, captureGroupMemberResponse2.getStatusLine().getStatusCode());

    }

}
