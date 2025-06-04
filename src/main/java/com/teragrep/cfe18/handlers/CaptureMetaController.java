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

import com.teragrep.cfe18.CaptureMetaMapper;
import com.teragrep.cfe18.handlers.entities.CaptureMeta;
import com.teragrep.cfe18.handlers.entities.CaptureDefinition;
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
@RequestMapping(path = "/capture/meta")
@SecurityRequirement(name = "api")
public class CaptureMetaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureMetaController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    CaptureMetaMapper captureMetaMapper;

    @RequestMapping(path = "", method = RequestMethod.PUT, produces = "application/json")
    @Operation(summary = "Insert new capture meta for capture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Capture meta created for capture",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureMeta.class))}),
            @ApiResponse(responseCode = "404", description = "Capture does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<String> create(@RequestBody CaptureMeta newCaptureMeta) {
        LOGGER.info("About to insert <[{}]>", newCaptureMeta);
        try {
            CaptureMeta cm = captureMetaMapper.create(
                    newCaptureMeta.getCaptureId(),
                    newCaptureMeta.getCaptureMetaKey(),
                    newCaptureMeta.getCaptureMetaValue()
            );
            LOGGER.debug("Values returned <[{}]>", cm);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", cm.getCaptureId());
            jsonObject.put("message", "New capture meta created");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newCaptureMeta.getCaptureId());
            jsonErr.put("message", ex.getCause().getMessage());
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                // 45000 = Custom error, row does not exist
                if (state.equals("45000")) {
                    jsonErr.put("message", "Capture does not exist");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/{captureId}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch capture meta by capture id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the capture meta",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureMeta.class))}),
            @ApiResponse(responseCode = "404", description = "Capture meta does not exist",
                    content = @Content)})
    public ResponseEntity<?> get(@PathVariable("captureId") int captureId, @RequestParam(required = false) Integer version) {
        try {
            List<CaptureMeta> am = captureMetaMapper.get(captureId, version);
            return new ResponseEntity<>(am, HttpStatus.OK);
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", captureId);
            jsonErr.put("message", ex.getCause().getMessage());
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                // 45000 = Custom error, row does not exist
                if (state.equals("45000")) {
                    jsonErr.put("message", "Capture meta does not exist");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch all capture metas", description = "Will return empty list if there are no capture metas to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureMeta.class))})})
    public List<CaptureMeta> getAll(@RequestParam(required = false) Integer version) {
        return captureMetaMapper.getAll(version);
    }

    @RequestMapping(path = "/{captureId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete capture meta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capture meta deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureMeta.class))}),
            @ApiResponse(responseCode = "404", description = "Capture meta does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<String> delete(@PathVariable("captureId") int captureId) {
        LOGGER.info("Deleting Capture meta <[{}]>", captureId);
        try {
            captureMetaMapper.delete(captureId);
            JSONObject j = new JSONObject();
            j.put("id", captureId);
            j.put("message", "Capture meta deleted");
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
                // 45000 = Custom error, row does not exist
                if (state.equals("45000")) {
                    jsonErr.put("message", "Record does not exist");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/{key}/{value}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch capture definitions by key and value")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the capture definitions",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureMeta.class))}),
            @ApiResponse(responseCode = "404", description = "Capture meta key or value does not exist", content = @Content)})
    public ResponseEntity<?> get(@PathVariable("key") String key, @PathVariable("value") String value, @RequestParam(required = false) Integer version) {
        try {
            List<CaptureDefinition> am = captureMetaMapper.getByKeyValue(key, value, version);
            return new ResponseEntity<>(am, HttpStatus.OK);
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            JSONObject jsonErr = new JSONObject();
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                // 45000 = Custom error, row does not exist
                if (state.equals("45000")) {
                    jsonErr.put("message", "No such key value pair exists");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

}

