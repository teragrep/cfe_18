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

import com.teragrep.cfe18.HostMapper;
import com.teragrep.cfe18.handlers.entities.FileCaptureMeta;
import com.teragrep.cfe18.handlers.entities.HostFile;
import com.teragrep.cfe18.handlers.entities.HostRelp;
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
import javax.websocket.server.PathParam;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(path = "host")
@SecurityRequirement(name = "api")
public class HostController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    HostMapper hostMapper;


    // Get host by id
    @RequestMapping(path = "/file/{id}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch file based host by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File based host retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostFile.class))}),
            @ApiResponse(responseCode = "400", description = "ID given does not match any host_id OR ID given directs to a hub based host OR ID given points to different type of host",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<?> getHostFile(@PathVariable("id") int id, @RequestParam(required = false) Integer version) {
        try {
            HostFile hf = hostMapper.getHostFileById(id,version);
            if (hf.getHost_type().equals("cfe"))
                return new ResponseEntity<>(hf, HttpStatus.OK);
            else {
                throw new Exception("Different host_type");
            }
        } catch (Exception ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", id);
            jsonErr.put("message", "Unexpected error");
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                switch (state) {
                    case "45000":
                        jsonErr.put("message", "Record does not exist with the given host id");
                        break;
                    case "45100":
                        jsonErr.put("message", "Host given is a hub");
                        break;
                }
            } else if (ex.getMessage().equals("Different host_type")) {
                LOGGER.error(ex.getMessage());
                jsonErr.put("message", "host exists with different type");
            }
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    // Get host by id
    @RequestMapping(path = "/relp/{id}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch relp based host by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relp based host retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostRelp.class))}),
            @ApiResponse(responseCode = "400", description = "ID given does not match any host_id OR ID given points to different type of host",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<?> getHostRelp(@PathVariable("id") int id, @RequestParam(required = false) Integer version) {
        try {
            HostRelp hr = hostMapper.getHostRelpById(id,version);
            if (hr.getHostType().equals("relp"))
                return new ResponseEntity<>(hr, HttpStatus.OK);
            else {
                throw new Exception("Different host_type");
            }
        } catch (Exception ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", id);
            jsonErr.put("message", "Unexpected error");
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("45000")) {
                    jsonErr.put("message", "Record does not exist with the given host id");
                }

            } else if (ex.getMessage().equals("Different host_type")) {
                LOGGER.error(ex.getMessage());
                jsonErr.put("message", "host exists with different type");
            }
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(path = "", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch all hosts from lastId and amount based on pageSize. LastId defaults to 0 returning first 100 rows. PageSize is defaulted in application.properties", description = "Will return empty list if there are no hosts to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hosts fetched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostFile.class))})})
    public List<HostFile> getAllHost(@RequestParam(required = false) Integer version, @RequestParam(defaultValue = "${pagination.pageSize}") Integer pageSize, @RequestParam(defaultValue = "0") Integer lastId) {
        return hostMapper.getAllHost(version,pageSize,lastId);
    }


    //Insert new host with cfe type
    @RequestMapping(path = "/file", method = RequestMethod.PUT, produces = "application/json")
    @Operation(summary = "Insert file based host")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New file based host created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostFile.class))}),
            @ApiResponse(responseCode = "400", description = "ID,MD5 or fqhost already exists OR Hub does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> newHostFile(@RequestBody HostFile newHostFile) {
        LOGGER.info("About to insert <[{}]>",newHostFile);
        try {
            HostFile hf = hostMapper.addHostFile(
                    newHostFile.getMD5(),
                    newHostFile.getFqHost(),
                    newHostFile.getHub_fq());
            LOGGER.debug("Values returned <[{}]>",hf);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", hf.getId());
            jsonObject.put("message", "New host created with cfe type");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newHostFile.getId());
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                // Get specific error type
                int error = ((SQLException) cause).getErrorCode();
                // Link error with state to get accurate error status
                String state = error + "-" + ((SQLException) cause).getSQLState();
                if (state.equals("1062-23000")) {
                    jsonErr.put("message", "ID,MD5 or fqhost already exists");
                } else if (state.equals("1644-45000")) {
                    jsonErr.put("message", "Hub does not exist");
                }
                return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/relp", method = RequestMethod.PUT, produces = "application/json")
    @Operation(summary = "Insert relp based host")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New relp based host created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostRelp.class))}),
            @ApiResponse(responseCode = "400", description = "ID,MD5 or fqhost already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> newHostRelp(@RequestBody HostRelp newHostRelp) {
        LOGGER.info("About to insert <[{}]>",newHostRelp);
        try {
            HostRelp hr = hostMapper.addHostRelp(
                    newHostRelp.getMd5(),
                    newHostRelp.getFqHost());
            LOGGER.debug("Values returned <[{}]>",hr);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", hr.getId());
            jsonObject.put("message", "New host created with relp type");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newHostRelp.getId());
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                // Get specific error type
                int error = ((SQLException) cause).getErrorCode();
                // Link error with state to get accurate error status
                String state = error + "-" + ((SQLException) cause).getSQLState();
                if (state.equals("1062-23000")) {
                    jsonErr.put("message", "ID,MD5 or fqhost already exists");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
                }
            }
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete host")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Host deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostFile.class))}),
            @ApiResponse(responseCode = "400", description = "Host is being used OR Host does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> removeHost(@PathVariable("id") int id) {
        LOGGER.info("Deleting Host  <[{}]>",id);
        JSONObject jsonErr = new JSONObject();
        jsonErr.put("id", id);
        try {
            hostMapper.deleteHost(id);
            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("message", "Host with id =  " + id + " deleted.");
            return new ResponseEntity<>(j.toString(), HttpStatus.OK);
        } catch (Exception ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("23000")) {
                    jsonErr.put("message", "Is in use");
                } else if (state.equals("45000")) {
                    jsonErr.put("message", "Record does not exist");
                }
                return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
