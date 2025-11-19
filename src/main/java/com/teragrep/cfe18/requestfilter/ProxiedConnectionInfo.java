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
package com.teragrep.cfe18.requestfilter;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

import javax.servlet.http.HttpServletRequest;

public class ProxiedConnectionInfo implements JsonAble {

    private final HttpServletRequest request;


    public ProxiedConnectionInfo(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public JsonObjectBuilder toJson() {
        JsonObjectBuilder proxiedConnectionInfo = Json.createObjectBuilder();
        if (request.getHeader("X-Forwarded-For") != null) {
            proxiedConnectionInfo.add("X-Forwarded-For", request.getHeader("X-Forwarded-For"));
        } else {
            proxiedConnectionInfo.addNull("X-Forwarded-For");
        }
        if (request.getHeader("Forwarded") != null) {
            proxiedConnectionInfo.add("Forwarded", request.getHeader("Forwarded"));
        } else {
            proxiedConnectionInfo.addNull("Forwarded");
        }
        if (request.getHeader("X-Forwarded-Proto") != null) {
            proxiedConnectionInfo.add("X-Forwarded-Proto", request.getHeader("X-Forwarded-Proto"));
        } else {
            proxiedConnectionInfo.addNull("X-Forwarded-Proto");
        }
        if (request.getHeader("X-Forwarded-Port") != null) {
            proxiedConnectionInfo.add("X-Forwarded-Port", request.getHeader("X-Forwarded-Port"));
        } else {
            proxiedConnectionInfo.addNull("X-Forwarded-Port");
        }
        if (request.getHeader("X-Real-IP") != null) {
            proxiedConnectionInfo.add("X-Real-IP", request.getHeader("X-Real-IP"));
        } else {
            proxiedConnectionInfo.addNull("X-Real-IP");
        }
        if (request.getHeader("Via") != null) {
            proxiedConnectionInfo.add("Via", request.getHeader("Via"));
        } else {
            proxiedConnectionInfo.addNull("Via");
        }
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("ProxiedConnectionInfo", proxiedConnectionInfo);

        return jsonObjectBuilder;
    }
}
