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

import com.teragrep.cfe18.HostGroupMapper;
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
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(path = "host")
@SecurityRequirement(name = "api")
public class HostGroupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostGroupController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    HostGroupMapper hostGroupMapper;

    @RequestMapping(path = "/group", method = RequestMethod.PUT, produces = "application/json")
    @Operation(summary = "Create host group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New host group created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostGroup.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> create(@RequestBody HostGroup newHostGroup) {
        LOGGER.info("About to insert <[{}]>", newHostGroup);
        try {
            HostGroup hg = hostGroupMapper.create(
                    newHostGroup.getHostGroupName(),
                    newHostGroup.getHostGroupType()
            );
            LOGGER.debug("Values returned <[{}]>", hg);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", hg.getId());
            jsonObject.put("message", "Host group created");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newHostGroup.getId());
            jsonErr.put("message", ex.getCause().getMessage());
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/group/link", method = RequestMethod.PUT, produces = "application/json")
    @Operation(summary = "Link host with group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Host linked with group",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostGroup.class))}),
            @ApiResponse(responseCode = "400", description = "Type mismatch between host group and host OR Host does not exist OR type mismatch between host and group",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> createLink(@RequestBody HostGroup newHostGroup) {
        LOGGER.info("About to insert <[{}]>", newHostGroup);
        try {
            HostGroup hg = hostGroupMapper.createLink(newHostGroup.getHostId(), newHostGroup.getId());
            LOGGER.debug("Values returned <[{}]>", hg);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", hg.getId());
            jsonObject.put("message", "Host linked with group");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newHostGroup.getId());
            jsonErr.put("message", ex.getCause().getMessage());
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/group/{id}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch host group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Host group retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostGroup.class))}),
            @ApiResponse(responseCode = "400", description = "Host group does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<?> get(@PathVariable("id") int id, @RequestParam(required = false) Integer version) {
        try {
            HostGroup hg = hostGroupMapper.get(id, version);
            return new ResponseEntity<>(hg, HttpStatus.OK);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", id);
            jsonErr.put("message", ex.getCause().getMessage());
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("45000")) {
                    jsonErr.put("message", "Record does not exist");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/group/hosts/{id}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch hosts in group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hosts retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostGroup.class))}),
            @ApiResponse(responseCode = "400", description = "Host group does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<?> getHosts(@PathVariable("id") int id, @RequestParam(required = false) Integer version) {
        try {
            List<HostGroup> hg = hostGroupMapper.getHosts(id, version);
            return new ResponseEntity<>(hg, HttpStatus.OK);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", id);
            jsonErr.put("message", ex.getCause().getMessage());
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("45000")) {
                    jsonErr.put("message", "Record does not exist");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/group", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch all groups", description = "Will return empty list if there are no host groups to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostGroup.class))})})
    public List<HostGroup> getAll(@RequestParam(required = false) Integer version) {
        return hostGroupMapper.getAll(version);
    }

    @RequestMapping(path = "/group/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete host group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Host group deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostGroup.class))}),
            @ApiResponse(responseCode = "400", description = "Host group is being used OR Host group does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> delete(@PathVariable("id") int id) {
        LOGGER.info("Deleting Host Group <[{}]>", id);
        try {
            hostGroupMapper.delete(id);
            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("message", "Host Group deleted");
            return new ResponseEntity<>(j.toString(), HttpStatus.OK);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", id);
            jsonErr.put("message", ex.getCause().getMessage());
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("23000")) {
                    jsonErr.put("message", "Is in use");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
                } else if (state.equals("45000")) {
                    jsonErr.put("message", "Record does not exist");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/group/{hostId}/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete host from group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Host deleted from group",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostGroup.class))}),
            @ApiResponse(responseCode = "400", description = "Host does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> deleteLink(@PathVariable("hostId") int hostId, @PathVariable("id") int id) {
        LOGGER.info("Deleting Host Group <[{}]>", id);
        try {
            hostGroupMapper.deleteLink(hostId, id);
            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("message", "Host deleted from group");
            return new ResponseEntity<>(j.toString(), HttpStatus.OK);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", id);
            jsonErr.put("message", ex.getCause().getMessage());
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("23000")) {
                    jsonErr.put("message", "Is in use");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
                } else if (state.equals("45000")) {
                    jsonErr.put("message", "Record does not exist");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }
}


