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

import com.teragrep.cfe18.SinkMapper;
import com.teragrep.cfe18.handlers.entities.Sink;
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
@RequestMapping(path = "sink")
@SecurityRequirement(name = "api")
public class SinkController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SinkController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    SinkMapper sinkMapper;


    @RequestMapping(path = "/id/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getSink(@PathVariable("id") int id) {
        JSONObject jsonErr = new JSONObject();
        jsonErr.put("id", id);
        try {
            Sink s = sinkMapper.getSinkById(id);
            return new ResponseEntity<>(s, HttpStatus.OK);
        } catch (Exception ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                String state = ((SQLException) cause).getSQLState();
                if (state.equals("45000")) {
                    jsonErr.put("message", "Record does not exist with the given sink_id");
                    return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
                }
            }
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // GET ALL Sinks
    @RequestMapping(path = "", method = RequestMethod.GET, produces = "application/json")
    public List<Sink> getAllSink() {
        return sinkMapper.getAllSinks();
    }


    @RequestMapping(path = "/details", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<String> newSink(@RequestBody Sink newSink) {
        LOGGER.info("about to insert <[{}]>",newSink);
        try {
            Sink n = sinkMapper.addNewSink(
                    newSink.getProtocol(),
                    newSink.getIp_address(),
                    newSink.getPort(),
                    newSink.getFlow());
            LOGGER.info("Values returned <[{}]>",n );
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", n.getId());
            jsonObject.put("message", "New sink created");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newSink.getId());
            final Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                LOGGER.error((cause).getMessage());
                // Get specific error type
                int error = ((SQLException) cause).getErrorCode();
                // Link error with state to get accurate error status
                String state = error + "-" + ((SQLException) cause).getSQLState();
                if (state.equals("1062-23000")) {
                    jsonErr.put("message", "Flow and protocol combination already exists");
                } else if (state.equals("1406-22001")) {
                    jsonErr.put("message", "Port length exceeded");
                }
                return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete
    @RequestMapping(path = "/id/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteSink(@PathVariable("id") int id) {
        LOGGER.info("Deleting sink {}", id);
        JSONObject jsonErr = new JSONObject();
        jsonErr.put("id", id);
        try {
            sinkMapper.deleteSinkById(id);
            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("message", "Sink with id = " + id + " deleted.");
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
