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

import com.teragrep.cfe18.FileProcessingTypeMapper;
import com.teragrep.cfe18.handlers.entities.FileProcessing;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.commons.lang3.EnumUtils;
import org.json.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping(path = "file/capture")
@SecurityRequirement(name = "api")
public class FileProcessingTypeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessingTypeController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    FileProcessingTypeMapper fileProcessingTypeMapper;


    @RequestMapping(path = "/meta/{id}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch file processing type by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File processing type retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FileProcessing.class))}),
            @ApiResponse(responseCode = "400", description = "File processing type does not exist with the given id",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
                })
    public ResponseEntity<?> get(@PathVariable("id") int id, @RequestParam(required = false) Integer version) {
        try {
            FileProcessing fc = fileProcessingTypeMapper.get(id,version);
            return new ResponseEntity<>(fc, HttpStatus.OK);
        } catch (Exception ex) {
            JSONObject jsonErr = new JSONObject();
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                // fail-safe if SQL error but not 45000 type
                jsonErr.put("id", id);
                if (state.equals("45000")) {
                    jsonErr.put("message", "Record does not exist with the given id");
                }
                return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Get ALL endpoint
    @RequestMapping(path = "/meta/", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch all file processing types", description = "Will return empty list if there are no file processing types to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File processing types fetched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FileProcessing.class))})
                })
    public List<FileProcessing> getAll(@RequestParam(required = false) Integer version) {
        return fileProcessingTypeMapper.getAll(version);
    }


    @RequestMapping(path = "/meta/rule", method = RequestMethod.PUT, produces = "application/json")
    @Operation(summary = "Insert file processing type for file based capture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New file processing type created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FileProcessing.class))}),
            @ApiResponse(responseCode = "400", description = "Inputvalue must be regex or newline",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> create(@RequestBody FileProcessing newFileProcessing) {
        LOGGER.info("About to insert <[{}]>", newFileProcessing);
        try {
            FileProcessing n = fileProcessingTypeMapper.create(
                    newFileProcessing.getTemplate(),
                    newFileProcessing.getRuleset(),
                    newFileProcessing.getName(),
                    newFileProcessing.getInputtype().toString(),
                    newFileProcessing.getInputvalue());
            LOGGER.debug("Values returned <[{}]>",n);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", n.getId());
            jsonObject.put("message", "New file processing type created");

            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    // Delete
    @RequestMapping(path = "meta/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete file processing type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File processing type deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FileProcessing.class))}),
            @ApiResponse(responseCode = "400", description = "File processing type is being used OR file processing type does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> delete(@PathVariable("id") int id) {
        LOGGER.info("Deleting file processing type  <[{}]>",id);
        JSONObject jsonErr = new JSONObject();
        try {
            FileProcessing fp = fileProcessingTypeMapper.delete(id);
            JSONObject j = new JSONObject();
            j.put("id", fp.getId());
            j.put("message", "File processing type deleted.");
            return new ResponseEntity<>(j.toString(), HttpStatus.OK);
        } catch (Exception ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("23000")) {
                    jsonErr.put("id", id);
                    jsonErr.put("message", "Is in use");
                } else if (state.equals("45000")) {
                    jsonErr.put("id", id);
                    jsonErr.put("message", "Record does not exist");
                }
                return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

