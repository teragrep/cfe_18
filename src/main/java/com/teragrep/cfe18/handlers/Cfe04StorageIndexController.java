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

import com.teragrep.cfe18.Cfe04StorageIndexMapper;
import com.teragrep.cfe18.handlers.entities.Cfe04StorageIndex;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
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

@RestController
@RequestMapping(path = "/v2/storages/definitions/cfe_04")
@SecurityRequirement(name = "api")
public class Cfe04StorageIndexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Cfe04StorageIndexController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    Cfe04StorageIndexMapper storageIndexMapper;

    @RequestMapping(
            path = "/{id}/indexes",
            method = RequestMethod.PUT,
            produces = "application/json"
    )
    @Operation(summary = "Link existing index to a existing storage")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Index succesfully linked to storage"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Index or Storage is missing",
                    content = @Content
            )
    })
    public ResponseEntity<?> create(
            @PathVariable("id") int storageId,
            @RequestBody Cfe04StorageIndex cfe04StorageIndex
    ) {
        LOGGER.info("About to insert <[{}]>", storageId);
        try {
            Integer returnedStorageId = storageIndexMapper
                    .create(
                            storageId, cfe04StorageIndex.getIndexId(), cfe04StorageIndex.getRepFactor(),
                            cfe04StorageIndex.isDisabled(), cfe04StorageIndex.getHomePath(),
                            cfe04StorageIndex.getColdpath(), cfe04StorageIndex.getThawedPath()
                    );
            LOGGER.debug("Values returned <[{}]>", returnedStorageId);
            JsonObjectBuilder returnJson = Json.createObjectBuilder();
            returnJson.add("id", returnedStorageId);
            returnJson.add("message", "New index linked to storage");
            return new ResponseEntity<>(returnJson.build().toString(), HttpStatus.CREATED);
        }
        catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            JsonObjectBuilder returnJson = Json.createObjectBuilder();
            returnJson.add("id", storageId);
            returnJson.add("message", ex.getCause().getMessage());
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("23000")) {
                    returnJson.add("message", "Record does not exist");
                    return new ResponseEntity<>(returnJson.build().toString(), HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(returnJson.build().toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(
            path = "/{cfe_04_id}/indexes/{indexId}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Delete index from cfe_04 storage")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Index deleted from cfe_04 storage"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Internal server error, contact admin",
                    content = @Content
            )
    })
    public ResponseEntity<String> delete(@PathVariable("cfe_04_id") int cfe04Id, @PathVariable("indexId") int indexId) {
        LOGGER.info("Deleting index from storage <[{}]>", cfe04Id);
        try {
            storageIndexMapper.delete(cfe04Id, indexId);
            JsonObjectBuilder returnJson = Json.createObjectBuilder();
            returnJson.add("id", cfe04Id);
            returnJson.add("message", "Index deleted from cfe_04 storage");
            return new ResponseEntity<>(returnJson.build().toString(), HttpStatus.OK);
        }
        catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            JsonObjectBuilder returnJson = Json.createObjectBuilder();
            returnJson.add("id", cfe04Id);
            returnJson.add("message", ex.getCause().getMessage());
            return new ResponseEntity<>(returnJson.build().toString(), HttpStatus.BAD_REQUEST);
        }
    }

}
