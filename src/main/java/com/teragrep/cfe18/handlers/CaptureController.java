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

import com.teragrep.cfe18.CaptureMapper;
import com.teragrep.cfe18.handlers.entities.CaptureDefinition;
import com.teragrep.cfe18.handlers.entities.CaptureFile;
import com.teragrep.cfe18.handlers.entities.CaptureRelp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.ibatis.exceptions.IbatisException;
import org.json.JSONObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.List;

@RestController
@RequestMapping(path = "/capture")
@SecurityRequirement(name = "api")
public class CaptureController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    CaptureMapper captureMapper;


    @RequestMapping(path = "/jwt", method = RequestMethod.GET, produces = "application/json")
    public String index(@AuthenticationPrincipal Jwt jwt) throws Exception {
        return String.format("Hello, %s!", jwt.getSubject());
    }


    @RequestMapping(path = "/file", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new file based capture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New file based capture created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureFile.class))}),
            @ApiResponse(responseCode = "400", description = "Wrong details",
                    content = @Content)
    })
    public ResponseEntity<String> create(@RequestBody CaptureFile newCapture) {
        LOGGER.info("About to insert <[{}]>",newCapture);
        try {
            CaptureFile c = captureMapper.createFile(
                    newCapture.getTag(),
                    newCapture.getRetentionTime(),
                    newCapture.getCategory(),
                    newCapture.getApplication(),
                    newCapture.getIndex(),
                    newCapture.getSourceType(),
                    newCapture.getProtocol(),
                    newCapture.getFlow(),
                    newCapture.getTagPath(),
                    newCapture.getCapturePath(),
                    newCapture.getFileProcessingTypeId());
            LOGGER.debug("Values returned <[{}]>",c);
            JSONObject jsonObjectFile = new JSONObject();
            jsonObjectFile.put("id", c.getId());
            jsonObjectFile.put("message", "New capture created");
            return new ResponseEntity<>(jsonObjectFile.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newCapture.getId());
            jsonErr.put("message", ex.getCause().toString());
            LOGGER.error(ex.getMessage());
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(path = "/relp", method = RequestMethod.PUT, produces = "application/json")
    @Operation(summary = "Create new relp based capture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New relp based capture created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureFile.class))}),
            @ApiResponse(responseCode = "400", description = "Wrong details",
                    content = @Content)
    })
    public ResponseEntity<String> create(@RequestBody CaptureRelp newCapture) {
        LOGGER.info("About to insert <[{}]>",newCapture);
        try {
            CaptureRelp c = captureMapper.createRelp(
                    newCapture.getTag(),
                    newCapture.getRetentionTime(),
                    newCapture.getCategory(),
                    newCapture.getApplication(),
                    newCapture.getIndex(),
                    newCapture.getSourceType(),
                    newCapture.getProtocol(),
                    newCapture.getFlow());
            LOGGER.debug("Values returned <[{}]>",c);
            JSONObject jsonObjectRelp = new JSONObject();
            jsonObjectRelp.put("id", c.getId());
            jsonObjectRelp.put("message", "New capture created");
            return new ResponseEntity<>(jsonObjectRelp.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newCapture.getId());
            jsonErr.put("message", ex.getCause().toString());
            LOGGER.error(ex.getMessage());
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/file/{id}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch a file capture by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the file capture",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureFile.class))}),
            @ApiResponse(responseCode = "404", description = "Capture not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<?> getFile(@PathVariable("id") int id, @RequestParam(required = false) Integer version) {
        try {
            CaptureFile c = captureMapper.getFile(id,version);
            return new ResponseEntity<>(c, HttpStatus.OK);
        } catch (RuntimeException ex) {
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

    @RequestMapping(path = "/relp/{id}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch a relp capture by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the relp capture",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureRelp.class))}),
            @ApiResponse(responseCode = "404", description = "Capture not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<?> getRelp(@PathVariable("id") int id, @RequestParam(required = false) Integer version) {
        try {
            CaptureRelp c = captureMapper.getRelp(id,version);
            return new ResponseEntity<>(c, HttpStatus.OK);
        } catch (RuntimeException ex) {
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


    @RequestMapping(path = "/file", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch all file captures", description = "Will return empty list if there are no captures to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureFile.class))}),
    })
    public List<CaptureFile> getAllFile(@RequestParam(required = false) Integer version) {
        return captureMapper.getAllFile(version);
    }

    @RequestMapping(path = "/relp", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch all relp captures", description = "Will return empty list if there are no captures to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureRelp.class))}),
    })
    public List<CaptureRelp> getAllRelp(@RequestParam(required = false) Integer version) {
        return captureMapper.getAllRelp(version);
    }



    // Delete
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete capture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Capture deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CaptureDefinition.class))}),
            @ApiResponse(responseCode = "400", description = "Capture does not exist OR Capture is being used",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<String> delete(@PathVariable("id") int id) {
        LOGGER.info("Deleting capture with id <[{}]>", id);

        try {
            captureMapper.delete(id);
            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("message", "Capture deleted");
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
