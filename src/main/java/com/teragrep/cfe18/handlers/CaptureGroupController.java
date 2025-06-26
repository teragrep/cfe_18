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

import com.teragrep.cfe18.CaptureGroupMapper;
import com.teragrep.cfe18.handlers.entities.CaptureGroup;
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
@RequestMapping(path = "group/captures")
@SecurityRequirement(name = "api")
public class CaptureGroupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureGroupController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    CaptureGroupMapper captureGroupmapper;


    @RequestMapping(path = "", method = RequestMethod.PUT, produces = "application/json")
    @Operation(summary = "Link capture with group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Capture linked with group",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureGroup.class))}),
            @ApiResponse(responseCode = "404", description = "Capture does not exist", content = @Content),
            @ApiResponse(responseCode = "404", description = "Group does not exist", content = @Content),
            @ApiResponse(responseCode = "409", description = "Type mismatch between capture and group", content = @Content),
            @ApiResponse(responseCode = "409", description = "Tag already exists within group", content = @Content),
            @ApiResponse(responseCode = "400", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<String> create(@RequestBody CaptureGroup newCaptureGroup) {
        LOGGER.info("About to insert <[{}]>", newCaptureGroup);
        try {
            CaptureGroup c = captureGroupmapper.create(
                    newCaptureGroup.getCaptureDefinitionId(),
                    newCaptureGroup.getId()
            );
            LOGGER.info("Values returned what happened with linking <[{}]>", c);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", c.getId());
            jsonObject.put("message", "Capture linked with group");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newCaptureGroup.getId());
            jsonErr.put("message", ex.getCause().getMessage());
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                int error = ((SQLException) cause).getErrorCode();
                String state = ((SQLException) cause).getSQLState();
                // 1452 No referenced row, 23000 constraint error state from database. Group and capture types do not match e.g. relp,file...
                if (error==1452 && state.equals("23000")) {
                    jsonErr.put("message", "Type mismatch between capture group and capture");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.CONFLICT);
                    // 1062 duplicate entry code, 23000 constraint error state from database
                } else if (error==1062 && state.equals("23000")){
                    jsonErr.put("message", "Tag already exists within given group");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.CONFLICT);
                    // Custom error thrown by procedure. Missing items
                } else if (state.equals("45000")) {
                    jsonErr.put("message", "Record does not exist");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.NOT_FOUND);
                } else {
                    jsonErr.put("message", "Error unrecognized, contact admin");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
                }
            }
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch captures in group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the captures",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureGroup.class))}),
            @ApiResponse(responseCode = "404", description = "Capture group does not exist", content = @Content),
            @ApiResponse(responseCode = "400", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<?> get(@PathVariable("id") int id, @RequestParam(required = false) Integer version) {
        try {
            List<CaptureGroup> cg = captureGroupmapper.get(id, version);
            return new ResponseEntity<>(cg, HttpStatus.OK);
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("message", ex.getCause().getMessage());
            jsonErr.put("id", id);
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("45000")) {
                    jsonErr.put("message", "Record does not exist");
                    return new ResponseEntity<>(ex.toString(), HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(ex.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch all captures in all groups if linked", description = "Will return empty list if there are no captures with groups to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureGroup.class))})})
    public List<CaptureGroup> getAll(@RequestParam(required = false) Integer version) {
        return captureGroupmapper.getAll(version);
    }

    @RequestMapping(path = "/{captureId}/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete capture from group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capture deleted from group",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureGroup.class))}),
            @ApiResponse(responseCode = "404", description = "Capture does not exist", content = @Content),
            @ApiResponse(responseCode = "404", description = "Capture is not linked to group", content = @Content),
            @ApiResponse(responseCode = "400", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<String> delete(@PathVariable("captureId") int captureId,@PathVariable("id") int id) {
        LOGGER.info("Deleting Capture from group <[{}]>", captureId);
        try {
            captureGroupmapper.delete(captureId,id);
            JSONObject j = new JSONObject();
            j.put("id", captureId);
            j.put("message", "Capture deleted from group");
            return new ResponseEntity<>(j.toString(), HttpStatus.OK);
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", captureId);
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
}

