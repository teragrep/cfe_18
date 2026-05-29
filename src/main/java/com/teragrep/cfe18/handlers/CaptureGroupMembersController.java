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
package com.teragrep.cfe18.handlers;

import com.teragrep.cfe18.CaptureGroupMembersMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.json.JSONObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.List;

@RestController
@RequestMapping(path = "v2/captures/groups")
@SecurityRequirement(name = "api")
public class CaptureGroupMembersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureGroupMembersController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    CaptureGroupMembersMapper captureGroupMapper;

    @RequestMapping(
            path = "/{groupId}/members",
            method = RequestMethod.PUT,
            produces = "application/json"
    )
    @Operation(summary = "Link capture with group")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Capture linked with group"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Capture does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Group does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Type mismatch between capture and group",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Tag already exists within group",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error, contact admin",
                    content = @Content
            )
    })
    public ResponseEntity<String> create(@PathVariable("groupId") int groupId, @RequestBody int captureId) {
        LOGGER.info("About to insert <[{}]>", captureId);
        Integer returnedCaptureId = captureGroupMapper.create(groupId, captureId);
        LOGGER.info("Values returned what happened with linking <[{}]>", returnedCaptureId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", returnedCaptureId);
        jsonObject.put("message", "Capture linked with group");
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);

    }

    @RequestMapping(
            path = "/{groupId}/members",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @Operation(summary = "Fetch captures in group")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the captures"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Capture group does not exist",
                    content = @Content
            )
    })
    public ResponseEntity<String> get(
            @PathVariable("groupId") int groupId,
            @RequestParam(required = false) Integer version
    ) {
        List<Integer> cg = captureGroupMapper.get(groupId, version);
        return new ResponseEntity<>(cg.toString(), HttpStatus.OK);
    }

    @RequestMapping(
            path = "/{groupId}/members/{captureId}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Delete capture from group")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Capture deleted from group"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Capture does not exist OR capture is not linked to group",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error, contact admin",
                    content = @Content
            )
    })
    public ResponseEntity<String> delete(
            @PathVariable("groupId") int groupId,
            @PathVariable("captureId") int captureId
    ) {
        LOGGER.info("Deleting Capture from group <[{}]>", groupId);
        captureGroupMapper.delete(groupId, captureId);
        JSONObject j = new JSONObject();
        j.put("id", groupId);
        j.put("message", "Capture deleted from group");
        return new ResponseEntity<>(j.toString(), HttpStatus.OK);

    }
}
