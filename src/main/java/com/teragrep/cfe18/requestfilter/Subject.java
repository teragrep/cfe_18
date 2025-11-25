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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import javax.servlet.http.HttpServletRequest;

public class Subject implements JsonAble {

    private final HttpServletRequest request;

    public Subject(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public JsonObjectBuilder toJson() {
        JsonObjectBuilder subjectValueBuilder = Json.createObjectBuilder();
        JsonObjectBuilder authenticationBuilder = Json.createObjectBuilder();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                authenticationBuilder.add("JWTSubClaim", jwt.getClaimAsString("sub"));
                authenticationBuilder.add("Authenticated", true);
            }
            else {
                throw new IllegalArgumentException("Unsupported authentication type.");
            }
        }
        else {
            // Log that user is anonymous, not authenticated.
            authenticationBuilder.add("Authenticated", false);
        }

        subjectValueBuilder.add("User-Agent", request.getHeader("User-Agent"));
        subjectValueBuilder.addAll(authenticationBuilder);
        JsonObjectBuilder subjectBuilder = Json.createObjectBuilder();
        subjectBuilder.add("Subject", subjectValueBuilder);
        return subjectBuilder;
    }
}
