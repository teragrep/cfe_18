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

import com.teragrep.cfe18.HostMetaMapper;
import com.teragrep.cfe18.handlers.entities.HostMeta;
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

@RequestMapping(path = "v2/hosts/definitions")
@RestController
@SecurityRequirement(name = "api")
public class HostMetaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostMetaController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    HostMetaMapper hostMetaMapper;

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/{hostId}/metadata",
            produces = "application/json"
    )
    @Operation(summary = "Insert new host meta.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "New host meta created",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HostMeta.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Internal server error, contact admin",
                    content = @Content
            )
    })
    public ResponseEntity<String> create(@PathVariable("hostId") int hostId, @RequestBody HostMeta newHostMeta) {
        LOGGER.info("About to insert <[{}]>", newHostMeta);
        newHostMeta.setHostId(hostId);
        HostMeta hm = hostMetaMapper
                .create(newHostMeta.getHostId(), newHostMeta.getMetaKey(), newHostMeta.getMetaValue());
        LOGGER.debug("Values returned <[{}]>", hm);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", hm.getHostId());
        jsonObject.put("message", "New host meta added");
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
    }

    @RequestMapping(
            path = "/{hostId}/metadata",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @Operation(
            summary = "Fetch all host metas. Key is optional",
            description = "Will return empty list if there are no host metas to fetch"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "host metas fetched",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HostMeta.class)
                            )
                    }
            )
    })
    public List<HostMeta> get(
            @PathVariable("hostId") int hostId,
            @RequestParam(required = false) String key,
            @RequestParam(required = false) Integer version
    ) {
        return hostMetaMapper.get(hostId, key, version);
    }

    @RequestMapping(
            path = "/{hostId}/metadata",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Delete host meta")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Host meta deleted",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HostMeta.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Internal server error, contact admin",
                    content = @Content
            )
    })
    public ResponseEntity<String> delete(
            @PathVariable("hostId") Integer hostId,
            @RequestParam(required = false) String key
    ) {
        LOGGER.info("Deleting Hostmeta <[{}]>", hostId);
        hostMetaMapper.delete(hostId, key);
        JSONObject j = new JSONObject();
        j.put("id", hostId);
        j.put("message", "Hostmeta deleted.");
        return new ResponseEntity<>(j.toString(), HttpStatus.OK);
    }
}
