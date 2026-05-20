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

import com.teragrep.cfe18.HostGroupsMapper;
import com.teragrep.cfe18.handlers.entities.HostGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping(path = "v2/hosts/groups")
@SecurityRequirement(name = "api")
public class HostGroupsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostGroupsController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    HostGroupsMapper hostGroupsMapper;

    @RequestMapping(
            path = "",
            method = RequestMethod.PUT,
            produces = "application/json"
    )
    @Operation(summary = "Insert host group")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "New host group created",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HostGroup.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error, contact admin",
                    content = @Content
            )
    })
    public ResponseEntity<String> create(@RequestBody HostGroup newHostGroup) {
        LOGGER.info("About to insert <[{}]>", newHostGroup);
        HostGroup hg = hostGroupsMapper.create(newHostGroup.getHost_group_name(), newHostGroup.getHost_group_type());
        LOGGER.debug("Values returned <[{}]>", hg);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", hg.getId());
        jsonObject.put("message", "New host group created");
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);

    }

    // Get host group details
    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @Operation(summary = "Fetch host group")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Host group retrieved",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HostGroup.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Host group does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error, contact admin",
                    content = @Content
            )
    })
    public ResponseEntity<?> get(
            @PathVariable("id") final Integer id,
            @RequestParam(required = false) Integer version
    ) {
        HostGroup hg = hostGroupsMapper.get(id, version);
        return new ResponseEntity<>(hg, HttpStatus.OK);

    }

    @RequestMapping(
            path = "",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @Operation(
            summary = "Fetch all host groups",
            description = "Will return empty list if there are no host groups to fetch"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Host groups fetched",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HostGroup.class)
                            )
                    }
            )
    })
    public List<HostGroup> getAll(@RequestParam(required = false) Integer version) {
        return hostGroupsMapper.getAll(version);
    }

    // Delete
    @RequestMapping(
            path = "/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Delete host group")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Host group deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HostGroup.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Host group is being used OR Host group does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error, contact admin",
                    content = @Content
            )
    })
    public ResponseEntity<String> delete(@PathVariable("id") final Integer id) {
        LOGGER.info("Deleting Host Group <[{}]>", id);
        hostGroupsMapper.delete(id);
        JSONObject j = new JSONObject();
        j.put("message", "Host Group deleted.");
        return new ResponseEntity<>(j.toString(), HttpStatus.OK);

    }
}
