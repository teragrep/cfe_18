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

    @LocalServerPort
    private int port;

    @Test
    public void testTagTriggerOnError() {

        final TestApiClient testApiClient = new TestApiClient(port, token);

        Integer flow = testApiClient.insertFlow("capflow");

        Integer sink = testApiClient.insertSink(flow, "555", "ip", "prot");

        Integer hostId1 = testApiClient.insertRelpHost("relpHostmd51", "relpHostfq1");
        Integer hostId2 = testApiClient.insertRelpHost("relpHostmd52", "relpHostfq2");

        Integer hostGroup1 = testApiClient.insertHostGroup(hostId1, "hostgroup1");
        Integer hostGroup2 = testApiClient.insertHostGroup(hostId2, "hostgroup2");
        Integer hostGroup3 = testApiClient.insertHostGroup(hostId1, "hostgroup2");

        Integer captureGroup1 = testApiClient.insertCaptureGroup("groupRelp1", IntegrationType.RELP, 1);
        Integer captureGroup2 = testApiClient.insertCaptureGroup("groupRelp2", IntegrationType.RELP, 1);

        Integer linkage1 = testApiClient.insertLinkage(captureGroup1, hostGroup1);
        Integer linkage2 = testApiClient.insertLinkage(captureGroup1, hostGroup2);

        Integer capture1 = testApiClient
                .insertCapture("relpTag1", "P30D", "audit", "relp", "audit_relp", "relpsource1", "prot", "capFlow");
        Integer capture2 = testApiClient
                .insertCapture("relpTag2", "P30D", "audit", "relp", "audit_relp", "relpsource2", "prot", "capFlow");

        HttpPut requestCaptureGroupMemberHeader1 = new HttpPut(
                "http://localhost:" + port + "/v2/captures/groups/1/members"
        );

        requestCaptureGroupMemberHeader1
                .setEntity(new StringEntity(String.valueOf(capture1), ContentType.APPLICATION_JSON));

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

        HttpPut requestCaptureGroupMemberHeader2 = new HttpPut(
                "http://localhost:" + port + "/v2/captures/groups/1/members"
        );

        requestCaptureGroupMemberHeader2
                .setEntity(new StringEntity(String.valueOf(capture2), ContentType.APPLICATION_JSON));

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

        assertEquals(captureGroupMemberExpected1, captureGroupMemberActual1);
        assertEquals(HttpStatus.SC_CREATED, captureGroupMemberResponse1.getStatusLine().getStatusCode());
        assertEquals(captureGroupMemberExpected2, captureGroupMemberActual2);
        assertEquals(HttpStatus.SC_CONFLICT, captureGroupMemberResponse2.getStatusLine().getStatusCode());

    }

}
