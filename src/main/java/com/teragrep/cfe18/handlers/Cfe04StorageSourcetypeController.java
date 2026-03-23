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

import com.teragrep.cfe18.Cfe04StorageSourcetypeMapper;
import com.teragrep.cfe18.handlers.entities.Cfe04StorageSourcetype;
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
public class Cfe04StorageSourcetypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Cfe04StorageSourcetypeController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    Cfe04StorageSourcetypeMapper cfe04StorageSourcetypeMapper;

    @RequestMapping(
            path = "/{id}/sourcetypes",
            method = RequestMethod.PUT,
            produces = "application/json"
    )
    @Operation(summary = "Link existing sourcetype to a existing storage")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Sourcetype succesfully linked to storage"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sourcetype or Storage is missing",
                    content = @Content
            )
    })
    public ResponseEntity<String> create(
            @PathVariable("id") int storageId,
            @RequestBody Cfe04StorageSourcetype cfe04StorageSourcetype
    ) {
        LOGGER.info("About to insert <[{}]>", storageId);
        try {
            Integer returnedStorageId = cfe04StorageSourcetypeMapper
                    .create(
                            storageId, cfe04StorageSourcetype.getSourcetypeId(), cfe04StorageSourcetype.getMaxDaysAgo(),
                            cfe04StorageSourcetype.getCategory(), cfe04StorageSourcetype.getSourceDescription(),
                            cfe04StorageSourcetype.getTruncate(), cfe04StorageSourcetype.isFreeformIndexerEnabled(),
                            cfe04StorageSourcetype.getFreeformIndexerText(),
                            cfe04StorageSourcetype.isFreeformLbEnabled(), cfe04StorageSourcetype.getFreeformLbText()
                    );
            LOGGER.debug("Values returned <[{}]>", returnedStorageId);
            JsonObjectBuilder returnJson = Json.createObjectBuilder();
            returnJson.add("id", storageId);
            returnJson.add("message", "New sourcetype linked to storage");
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
            path = "/{cfe_04_id}/sourcetypes/{sourceTypeId}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Delete sourcetype from cfe_04 storage")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sourcetype deleted from cfe_04 storage"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Internal server error, contact admin",
                    content = @Content
            )
    })
    public ResponseEntity<?> delete(
            @PathVariable("cfe_04_id") int cfe04Id,
            @PathVariable("sourceTypeId") int sourceTypeId
    ) {
        LOGGER.info("Deleting cfe 04 storage sourcetype <[{}]>", cfe04Id);
        try {
            cfe04StorageSourcetypeMapper.delete(cfe04Id, sourceTypeId);
            JsonObjectBuilder returnJson = Json.createObjectBuilder();
            returnJson.add("id", cfe04Id);
            returnJson.add("message", "Sourcetype deleted from cfe_04 storage");
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
