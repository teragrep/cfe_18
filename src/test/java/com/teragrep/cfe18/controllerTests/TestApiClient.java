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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

public class TestApiClient {

    private final Gson gson = new Gson();
    private final int port;
    private final String token;

    public TestApiClient(int port, String token) {
        this.port = port;
        this.token = token;
    }

    public Integer insertFlow(final String name) {
        Flow flow = new Flow();
        flow.setName(name);
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

        return Assertions.assertDoesNotThrow(() -> flowAsJson.getInt("id"));
    }

    public HttpResponse deleteFlow(final Integer flowId) {
        HttpDelete flowRequest = new HttpDelete("http://localhost:" + port + "/flow/" + flowId);
        flowRequest.setHeader("Authorization", "Bearer " + token);

        return Assertions.assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(flowRequest));
    }

    public Integer insertSink(final Integer flowId, final String sinkPort, final String ip, final String protocol) {
        Sink sink = new Sink();
        sink.setFlowId(flowId);
        sink.setPort(sinkPort);
        sink.setIpAddress(ip);
        sink.setProtocol(protocol);

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

        return Assertions.assertDoesNotThrow(() -> sinkAsJson.getInt("id"));

    }

    public Integer insertRelpHost(final String md5, final String fq) {
        HostRelp relpHost1 = new HostRelp();
        relpHost1.setMd5(md5);
        relpHost1.setFqHost(fq);

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

        return Assertions.assertDoesNotThrow(() -> relpHostResponseJsonObject1.getInt("id"));
    }

    public Integer insertHostGroup(final Integer hostId, final String groupName) {
        HostGroup relpHostGroup1 = new HostGroup();
        relpHostGroup1.setHost_id(hostId);
        relpHostGroup1.setHost_group_name(groupName);

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

        return Assertions.assertDoesNotThrow(() -> hostGroupResponseJson1.getInt("host_group_id"));

    }

    public Integer insertCaptureGroup(final String name, final IntegrationType integrationType, final Integer flowId) {

        CaptureGroups captureGroup1 = new CaptureGroups();
        captureGroup1.setCaptureGroupName(name);
        captureGroup1.setCaptureGroupType(integrationType);
        captureGroup1.setFlowId(flowId);

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

        return Assertions.assertDoesNotThrow(() -> captureGroupAsJson1.getInt("id"));
    }

    public Integer insertLinkage(final Integer captureGroupId, final Integer hostGroupId) {

        Linkage linkage1 = new Linkage();
        linkage1.setCapture_group_id(captureGroupId);
        linkage1.setHost_group_id(hostGroupId);

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

        return Assertions.assertDoesNotThrow(() -> linkageJsonResponse1.getInt("id"));

    }

    public Integer insertCapture(
            final String tag,
            final String retention,
            final String category,
            final String application,
            final String index,
            final String sourceType,
            final String protocol,
            final String flow
    ) {
        CaptureRelp captureRelp1 = new CaptureRelp();
        captureRelp1.setTag(tag);
        captureRelp1.setRetentionTime(retention);
        captureRelp1.setCategory(category);
        captureRelp1.setApplication(application);
        captureRelp1.setIndex(index);
        captureRelp1.setSourceType(sourceType);
        captureRelp1.setProtocol(protocol);
        captureRelp1.setFlow(flow);

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

        return Assertions.assertDoesNotThrow(() -> captureResponseJson1.getInt("id"));

    }

    public Integer insertCfe04Storage(final String storageName, final Integer flowId) {
        Storage storage = new Storage();
        storage.setStorageType(StorageType.CFE_04);
        storage.setStorageName(storageName);
        storage.setFlowId(flowId);

        String jsonStorage = gson.toJson(storage);

        StringEntity storageRequest = new StringEntity(String.valueOf(jsonStorage), ContentType.APPLICATION_JSON);

        HttpPut storageAsRequest = new HttpPut("http://localhost:" + port + "/storage");
        storageAsRequest.setEntity(storageRequest);
        storageAsRequest.setHeader("Authorization", "Bearer " + token);

        HttpResponse storageResponse = Assertions
                .assertDoesNotThrow(() -> HttpClientBuilder.create().build().execute(storageAsRequest));

        HttpEntity storageResponseEntity = storageResponse.getEntity();

        String storageAsResponse = Assertions.assertDoesNotThrow(() -> EntityUtils.toString(storageResponseEntity));

        JSONObject storageAsJson = Assertions.assertDoesNotThrow(() -> new JSONObject(storageAsResponse));

        return Assertions.assertDoesNotThrow(() -> storageAsJson.getInt("id"));
    }
}
