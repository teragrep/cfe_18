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

import com.teragrep.cfe18.StorageIndexMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.json.JSONObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
@RequestMapping(path = "/v2/storages/indexes")
@SecurityRequirement(name = "api")
public class StorageIndexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageIndexController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    StorageIndexMapper storageIndexMapper;

    @RequestMapping(
            path = "",
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
    public ResponseEntity<String> create(@RequestParam int storageId, @RequestParam String index) {
        LOGGER.info("About to insert <[{},{}]>", storageId, index);
        try {
            Integer returnedStorageId = storageIndexMapper.create(storageId, index);
            LOGGER.debug("Values returned <[{}]>", returnedStorageId);
            JSONObject s = new JSONObject();
            s.put("id", returnedStorageId);
            s.put("message", "New index linked to storage");
            return new ResponseEntity<>(s.toString(), HttpStatus.CREATED);
        }
        catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", storageId);
            jsonErr.put("message", ex.getCause().toString());
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.NOT_FOUND);
        }
    }
}
