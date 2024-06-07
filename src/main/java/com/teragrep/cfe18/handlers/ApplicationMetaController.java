/*
 * Main data management system (MDMS) cfe_18
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

import com.teragrep.cfe18.ApplicationMetaMapper;
import com.teragrep.cfe18.handlers.entities.ApplicationMeta;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(path="/application")
@SecurityRequirement(name="api")
public class ApplicationMetaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationMetaController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    ApplicationMetaMapper applicationMetaMapper;


    @RequestMapping(path="/{application}",method= RequestMethod.GET, produces="application/json")
    @Operation(summary = "Fetch applications meta by application name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the application meta",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationMeta.class))}),
            @ApiResponse(responseCode = "400", description = "Application meta does not exist",
                    content = @Content)
    })
    public ResponseEntity<?> getApplicationMeta(@PathVariable("application") String application) {
        try {
            List<ApplicationMeta> am = applicationMetaMapper.getApplicationMeta(application);
            return new ResponseEntity<>(am, HttpStatus.OK);
        } catch(Exception ex){
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", 0);
            jsonErr.put("message", "Unexpected error");
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path="/",method=RequestMethod.PUT,produces="application/json")
    @Operation(summary = "Insert new application meta for application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application meta created for application",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationMeta.class))}),
            @ApiResponse(responseCode = "400", description = "Application does not exist for inserting metadata",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> newApplicationMeta(@RequestBody ApplicationMeta newApplicationMeta){
        LOGGER.info("About to insert <[{}]>",newApplicationMeta);
        JSONObject jsonErr = new JSONObject();
        jsonErr.put("id", 0);
        try {
            ApplicationMeta am = applicationMetaMapper.addNewApplicationMeta(
                    newApplicationMeta.getApplication(),
                    newApplicationMeta.getApplication_meta_key(),
                    newApplicationMeta.getApplication_meta_value()
            );
            LOGGER.debug("Values returned <[{}]>",am);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", 0);
            jsonObject.put("message", "New application meta created for application = " + am.getApplication());
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (Exception ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("42000")) {
                    jsonErr.put("message", "Application does not exist");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
                }
            }
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
