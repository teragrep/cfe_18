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


import com.teragrep.cfe18.HostMetaMapper;
import com.teragrep.cfe18.handlers.entities.HostMeta;
import com.teragrep.cfe18.handlers.entities.InterfaceType;
import com.teragrep.cfe18.handlers.entities.IPAddress;
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

@RequestMapping(path = "host")
@RestController
@SecurityRequirement(name = "api")
public class HostMetaController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HostMetaController.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    HostMetaMapper hostMetaMapper;

    @RequestMapping(method = RequestMethod.PUT, path = "/meta", produces = "application/json")
    @Operation(summary = "Create new host meta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New host meta created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostMeta.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> create(@RequestBody HostMeta newHostMeta) {
        LOGGER.info("About to insert <[{}]>", newHostMeta);
        try {
            HostMeta hm = hostMetaMapper.create(
                    newHostMeta.getArch(),
                    newHostMeta.getFlavor(),
                    newHostMeta.getHostname(),
                    newHostMeta.getHostId(),
                    newHostMeta.getOs(),
                    newHostMeta.getReleaseVersion()
            );
            LOGGER.debug("Values returned <[{}]>", hm);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", hm.getId());
            jsonObject.put("message", "New host meta created");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newHostMeta.getId());
            jsonErr.put("message", ex.getCause().toString());
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/meta/interface", produces = "application/json")
    @Operation(summary = "Create interface type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New interface created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InterfaceType.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> createInterface(@RequestBody InterfaceType newInterfaceType) {
        LOGGER.info("About to insert <[{}]>", newInterfaceType);
        try {
            InterfaceType it = hostMetaMapper.createInterface(newInterfaceType.getInterfaceType());
            LOGGER.debug("Values returned <[{}]>", it);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", it.getId());
            jsonObject.put("message", "New interface created");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newInterfaceType.getId());
            jsonErr.put("message", ex.getCause().getMessage());
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/meta/interface/{interfaceId}/{Id}", produces = "application/json")
    @Operation(summary = "Link interface to hostmeta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Interface linked to hostmeta",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InterfaceType.class))}),
            @ApiResponse(responseCode = "400", description = "Interface OR hostmeta does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<String> createInterfaceLink(@PathVariable("interfaceId")int interfaceId, @PathVariable("Id")int id) {
        LOGGER.info("About to insert <[{}]>", interfaceId);
        try {
            InterfaceType it = hostMetaMapper.createLinkInterface(interfaceId, id);
            LOGGER.debug("Values returned <[{}]>", it);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", interfaceId);
            jsonObject.put("message", "Interface linked to hostMeta");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", interfaceId);
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

    @RequestMapping(path = "/meta/ip", method = RequestMethod.PUT, produces = "application/json")
    @Operation(summary = "Create IP address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New IP address created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IPAddress.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<String> createIp(@RequestBody IPAddress newIpAddress) {
        LOGGER.info("About to insert <[{}]>", newIpAddress);
        try {
            IPAddress ia = hostMetaMapper.createIp(newIpAddress.getIpAddress());
            LOGGER.debug("Values returned <[{}]>", ia);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", ia.getId());
            jsonObject.put("message", "New ip address created");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", newIpAddress.getId());
            jsonErr.put("message", ex.getCause().getMessage());
            return new ResponseEntity<>(jsonErr.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/meta/ip/{ipId}/{Id}", produces = "application/json")
    @Operation(summary = "Link IP to hostmeta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "IP linked to hostmeta",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InterfaceType.class))}),
            @ApiResponse(responseCode = "400", description = "IP OR hostmeta does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<String> createIpLink(@PathVariable("ipId") int ipId, @PathVariable("Id") int id) {
        LOGGER.info("About to insert <[{}]>", ipId);
        try {
            IPAddress it = hostMetaMapper.createLinkIp(ipId,id);
            LOGGER.debug("Values returned <[{}]>", it);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", ipId);
            jsonObject.put("message", "IP linked to hostMeta");
            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", ipId);
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

    @RequestMapping(path = "/meta/{id}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch host meta by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Host meta retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostMeta.class))}),
            @ApiResponse(responseCode = "400", description = "Host meta does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<?> getMeta(@PathVariable("id") int id, @RequestParam(required = false) Integer version) {
        try {
            HostMeta hm = hostMetaMapper.getMeta(id, version);
            return new ResponseEntity<>(hm, HttpStatus.OK);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", id);
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

    @RequestMapping(path = "/meta/interface/{id}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch interfaces linked to hostmeta", description = "Will return empty list if there are no interface types to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interface types fetched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InterfaceType.class))})
    })
    public ResponseEntity<?> getMetaInterfaces(@PathVariable("id")int id,@RequestParam(required = false) Integer version) {
        try {
            List<InterfaceType> hm = hostMetaMapper.getInterface(id, version);
            return new ResponseEntity<>(hm, HttpStatus.OK);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", id);
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

    @RequestMapping(path = "/meta/ipAddress/{id}", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch ip addresses linked to hostmeta", description = "Will return empty list if there are no ip addresses to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ip addresses fetched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IPAddress.class))})
    })
    public ResponseEntity<?> getMetaIpAddresses(@PathVariable("id")int id,@RequestParam(required = false) Integer version) {
        try {
            List<IPAddress> hm = hostMetaMapper.getIp(id, version);
            return new ResponseEntity<>(hm, HttpStatus.OK);
        } catch (RuntimeException ex) {
            JSONObject jsonErr = new JSONObject();
            jsonErr.put("id", id);
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

    @RequestMapping(path = "/meta", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch all host metas", description = "Will return empty list if there are no host metas to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostMeta.class))})})
    public List<HostMeta> getAllMeta(@RequestParam(required = false) Integer version) {
        return hostMetaMapper.getAllMeta(version);
    }

    @RequestMapping(path = "/meta/interface", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch all interfaces", description = "Will return empty list if there are no interfaces to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InterfaceType.class))})})
    public List<InterfaceType> getAllInterfaces(@RequestParam(required = false) Integer version) {
        return hostMetaMapper.getAllInterfaces(version);
    }

    @RequestMapping(path = "/meta/ip", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Fetch all ip addresses", description = "Will return empty list if there are no ip addresses to fetch")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IPAddress.class))})})
    public List<IPAddress> getAllIpAddresses(@RequestParam(required = false) Integer version) {
        return hostMetaMapper.getAllIps(version);
    }

    @RequestMapping(path = "/meta/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete host meta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Host meta deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HostMeta.class))}),
            @ApiResponse(responseCode = "400", description = "Host meta is being used OR Host meta does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> deleteMeta(@PathVariable("id") int id) {
        LOGGER.info("Deleting Hostmeta <[{}]>", id);
        try {
            hostMetaMapper.delete(id);
            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("message", "Hostmeta deleted");
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

    @RequestMapping(path = "/meta/interface/{interfaceId}/{hostMetaId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete interface from host meta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interface deleted from host meta",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InterfaceType.class))}),
            @ApiResponse(responseCode = "400", description = "Interface does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<String> deleteLinkInterface(@PathVariable("interfaceId") int id,@PathVariable("hostMetaId") int hostMetaId) {
        LOGGER.info("Deleting interface link <[{}]>", id);
        try {
            hostMetaMapper.deleteLinkInterface(id,hostMetaId);
            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("message", "Interface link deleted");
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

    @RequestMapping(path = "/meta/ip/{ipId}/{hostMetaId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete ip address from host meta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ip address deleted from host meta",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IPAddress.class))}),
            @ApiResponse(responseCode = "400", description = "Ip address does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)})
    public ResponseEntity<String> deleteLinkIp(@PathVariable("ipId") int id,@PathVariable("hostMetaId") int hostMetaId) {
        LOGGER.info("Deleting ip link <[{}]>", id);
        try {
            hostMetaMapper.deleteLinkIp(id,hostMetaId);
            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("message", "Ip link deleted");
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

    @RequestMapping(path = "/meta/interface/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete interface type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interface type deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InterfaceType.class))}),
            @ApiResponse(responseCode = "400", description = "Interface type is being used OR Interface type does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> deleteInterface(@PathVariable("id") int id) {
        LOGGER.info("Deleting Interface <[{}]>", id);

        try {
            hostMetaMapper.deleteInterface(id);
            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("message", "Interface deleted");
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

    @RequestMapping(path = "/meta/ip/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete IP address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "IP address deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IPAddress.class))}),
            @ApiResponse(responseCode = "400", description = "IP address is being used OR IP address does not exist",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error, contact admin", content = @Content)
    })
    public ResponseEntity<String> deleteIp(@PathVariable("id") int id) {
        LOGGER.info("Deleting Host <[{}]>", id);
        try {
            hostMetaMapper.deleteIp(id);
            JSONObject j = new JSONObject();
            j.put("id", id);
            j.put("message", "Ip deleted");
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


