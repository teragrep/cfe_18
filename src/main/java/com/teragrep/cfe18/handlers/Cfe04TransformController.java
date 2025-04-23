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

import com.teragrep.cfe18.Cfe04TransformMapper;
import com.teragrep.cfe18.handlers.entities.Cfe04Transform;
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
@RequestMapping(path="/storage/cfe04/transforms")
@SecurityRequirement(name="api")
public class Cfe04TransformController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Cfe04TransformController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    Cfe04TransformMapper cfe04TransformMapper;

    // Fetch all storages
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch all cfe04 transform details", description = "Will return empty list if there are no transforms to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cfe04 transforms fetched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Cfe04Transform.class))})
    })
    public List<Cfe04Transform> getAllCfe04Transforms(@RequestParam(required = false) Integer version) {
        return cfe04TransformMapper.getAllCfe04Transforms(version);
    }

    // Fetch transforms for cfe_04 via Cfe_04 ID
    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch transforms for cfe_04 via cfe_04 ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "cfe_04 transforms retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Cfe04Transform.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<?> getAllForCfe04Id(@PathVariable Integer id, @RequestParam(required = false) Integer version) {
        try {
            List<Cfe04Transform> cfe04Transforms = cfe04TransformMapper.getCfe04TransformById(id,version);
            return new ResponseEntity<>(cfe04Transforms, HttpStatus.OK);
        } catch (Exception ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", id);
            final Throwable cause = ex.getCause();
            LOGGER.error(cause.getMessage(), cause);
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("45000")) {
                    jsonErr.put("message", "Record does not exist with the given id");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
                }
            }
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // New transforms for cfe_04
    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    @Operation(summary = "Insert new transforms for cfe_04")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New cfe_04 transforms created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Cfe04Transform.class))}),
            @ApiResponse(responseCode = "400", description = "SQL Constraint error",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> addNewCfe04Transform(@RequestBody Cfe04Transform newCfe04Transform) {
        LOGGER.info("About to insert <[{}]>", newCfe04Transform);
        try {
            Cfe04Transform cfe04Transform = cfe04TransformMapper.addNewCfe04Transform(
                    newCfe04Transform.getCfe04Id(),
                    newCfe04Transform.getName(),
                    newCfe04Transform.isWriteMeta(),
                    newCfe04Transform.isWriteDefault(),
                    newCfe04Transform.getDefaultValue(),
                    newCfe04Transform.getDestinationKey(),
                    newCfe04Transform.getRegex(),
                    newCfe04Transform.getFormat());
            LOGGER.debug("Values returned <[{}]>", cfe04Transform);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", cfe04Transform.getId());
            jsonObject.put("message", "New cfe_04 transforms created");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            final Throwable cause = ex.getCause();
            // 1452-23000
            if(cause instanceof SQLException) {
                int error = ((SQLException) cause).getErrorCode();
                String state = error + "-" + ((SQLException) cause).getSQLState();
                JSONObject jsonErr = new JSONObject();
                jsonErr.put("id", newCfe04Transform.getId());
                if (state.equals("1452-23000")) {
                    jsonErr.put("message", "Cfe_04 is missing with the given ID");
                }
                else {
                    jsonErr.put("message", "Error unrecognized, contact admin");
                }
                return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    // Delete cfe_04 transforms
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete cfe_04 transform via transform ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transform deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Cfe04Transform.class))}),
            @ApiResponse(responseCode = "400", description = "Cfe_04 transform does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> removeCfe04Transform(@PathVariable("id") Integer id) {
        LOGGER.info("Deleting cfe_04 transforms with id <[{}]>", id);
        JSONObject jsonErr = new JSONObject();
        jsonErr.put("id", id);
        try {
            cfe04TransformMapper.deleteCfe04TransformById(id);
            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("message", "cfe_04 transforms with id of " + id + " deleted.");
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
