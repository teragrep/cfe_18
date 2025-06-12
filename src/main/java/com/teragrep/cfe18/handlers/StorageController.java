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

import com.teragrep.cfe18.StorageMapper;
import com.teragrep.cfe18.handlers.entities.CaptureStorage;
import com.teragrep.cfe18.handlers.entities.FileProcessing;
import com.teragrep.cfe18.handlers.entities.FlowStorage;
import com.teragrep.cfe18.handlers.entities.Storage;
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
@RequestMapping(path = "/storage")
@SecurityRequirement(name = "api")
public class StorageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageController.class);
    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    StorageMapper storageMapper;

    @RequestMapping(path = "", method = RequestMethod.PUT, produces = "application/json")
    @Operation(summary = "Create storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New storage created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Storage.class))}),
            @ApiResponse(responseCode = "400", description = "Storage name already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> create(@RequestBody Storage newStorage) {
        LOGGER.info("About to insert <[{}]>", newStorage);
        try {
            Storage s = storageMapper.create(
                    newStorage.getStorageType(),
                    newStorage.getStorageName());
            LOGGER.debug("Values returned <[{}]>", s);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", s.getId());
            jsonObject.put("message", "New storage created");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newStorage.getId());
            jsonErr.put("message", ex.getCause().toString());
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("23000")) {
                    jsonErr.put("message", "Storage name already exists");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
                }
            }
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(path = "/capture", method = RequestMethod.PUT, produces = "application/json")
    @Operation(summary = "Create new capture storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New capture storage created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureStorage.class))}),
            @ApiResponse(responseCode = "404", description = "Flow storage does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> createCaptureStorage(@RequestBody CaptureStorage newCaptureStorage) {
        LOGGER.info("About to insert <[{}]>", newCaptureStorage);
        try {
            CaptureStorage cs = storageMapper.createCaptureStorage(
                    newCaptureStorage.getCapture_id(),
                    newCaptureStorage.getStorage_id());
            LOGGER.debug("Values returned <[{}]>", cs);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", cs.getCapture_id());
            jsonObject.put("message", "New capture storage created");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newCaptureStorage.getCapture_id());
            jsonErr.put("message", ex.getCause().toString());
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("45000")) {
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch storage", description = "Will return empty list if there are no storages to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Storage fetched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Storage.class))}),
            @ApiResponse(responseCode = "404", description = "Storage does not exist",
                    content = @Content)})
    public ResponseEntity<?> getStorage(@PathVariable("id") int id, @RequestParam(required = false) Integer version) {
        try {
            Storage s = storageMapper.getStorage(id, version);
            return new ResponseEntity<>(s, HttpStatus.OK);
        } catch (Exception ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", id);
            jsonErr.put("message", ex.getCause().toString());
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


    @RequestMapping(path = "/capture/{captureId}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch capture storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capture storage retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureStorage.class))}),
            @ApiResponse(responseCode = "400", description = "Capture storage does not exist with the given ID",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<?> getCaptureStorage(@PathVariable("captureId") int captureId, @RequestParam(required = false) Integer version) {
        try {
            List<CaptureStorage> cs = storageMapper.getCaptureStorage(captureId, version);
            return new ResponseEntity<>(cs, HttpStatus.OK);
        } catch (RuntimeException ex) {
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

    @RequestMapping(path = "", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch all storages", description = "Will return empty list if there are no flow storages to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FlowStorage.class))})})
    public List<Storage> getAll(@RequestParam(required = false) Integer version) {
        return storageMapper.getAll(version);
    }

    @RequestMapping(path = "/capture", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch all capture storages", description = "Will return empty list if there are no capture storages to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureStorage.class))})})
    public List<CaptureStorage> getAllCapture(@RequestParam(required = false) Integer version) {
        return storageMapper.getAllCapture(version);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Storage deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Storage.class))}),
            @ApiResponse(responseCode = "400", description = "Storage is being used OR Storage does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<String> delete(@PathVariable("id") int id) {
        LOGGER.info("Deleting Storage <[{}]>", id);
        try {
            storageMapper.delete(id);
            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("message", "Storage deleted");
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

    @RequestMapping(path = "/capture/{captureId}/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete capture storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capture storage deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FileProcessing.class))}),
            @ApiResponse(responseCode = "400", description = "Capture storage is being used OR Capture storage does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> deleteCaptureStorage(@PathVariable("captureId") int captureId, @PathVariable("id") int id) {
        LOGGER.info("Deleting capture storage with id <[{}]>", id);
        try {
            storageMapper.deleteCaptureStorage(captureId, id);
            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("message", "Capture storage deleted");
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


