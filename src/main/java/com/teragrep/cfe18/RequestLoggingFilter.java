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
package com.teragrep.cfe18;

import com.teragrep.cfe18.requestfilter.*;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        JsonObjectBuilder causalityInformationBuilder = Json.createObjectBuilder();

        // Type : Cause+Effect
        Type type = new Type();
        causalityInformationBuilder.addAll(type.toJson());

        // Cause identifier : UUID
        RequestIdentity requestIdentity = new RequestIdentity();
        causalityInformationBuilder.addAll(requestIdentity.toJson());

        // Cause : Header connection
        Cause cause = new Cause(request);
        causalityInformationBuilder.addAll(cause.toJson());

        // Timestamp
        Timestamp timestamp = new Timestamp();
        causalityInformationBuilder.addAll(timestamp.toJson());

        // Location : Current host?
        Location location = new Location();
        causalityInformationBuilder.addAll(location.toJson());

        // Granularity : Informational?
        Granularity granularity = new Granularity();
        causalityInformationBuilder.addAll(granularity.toJson());

        // Classification : All request logs are currently access logs?
        Classification classification = new Classification();
        causalityInformationBuilder.addAll(classification.toJson());

        // Subject
        Subject subject = new Subject(request);
        causalityInformationBuilder.addAll(subject.toJson());

        // Action : Request Type, GET POST...
        Action action = new Action(request);
        causalityInformationBuilder.addAll(action.toJson());

        // Object
        RequestObject requestObject = new RequestObject(request);
        causalityInformationBuilder.addAll(requestObject.toJson());

        // Result
        Result result = new Result(response);
        causalityInformationBuilder.addAll(result.toJson());

        // Effect : HTTP response code, response size
        Effect effect = new Effect(response);
        causalityInformationBuilder.addAll(effect.toJson());

        LOGGER.info(causalityInformationBuilder.build().toString());

        filterChain.doFilter(request, response);
    }
}
