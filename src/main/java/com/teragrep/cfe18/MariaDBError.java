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

import org.springframework.http.HttpStatus;

public enum MariaDBError {

    MISSING(-15536, HttpStatus.NOT_FOUND, "Record does not exist"),
    INTEGRATIONTYPECONFLICT(-15526, HttpStatus.CONFLICT, "Integration type mismatch"),
    HOSTSUSEHUB(-15516, HttpStatus.BAD_REQUEST, "Hosts use the hub"),
    TAGMD5SUMERROR(-15506, HttpStatus.INTERNAL_SERVER_ERROR, "Tag mismatches with the given tag_path"),
    DUPLICATEHOST(-15496, HttpStatus.CONFLICT, "Tag already exists on the same host through different channels"),
    HOSTISAHUB(-15476, HttpStatus.CONFLICT, "Host is a hub"),
    MISSINGCONSTRAINT(1452, HttpStatus.NOT_FOUND, "Record does not exist"),
    INUSE(1451, HttpStatus.CONFLICT, "Is in use"),
    DUPLICATEENTRY(1062, HttpStatus.CONFLICT, "Duplicate entry"),
    GENERICERR(1644, HttpStatus.CONFLICT, "Generic user error"),
    UNKNOWN(0, HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"),;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    private MariaDBError(int errorCode, HttpStatus httpStatus, String message) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int errorCode() {
        return errorCode;
    }

    public HttpStatus status() {
        return httpStatus;
    }

    public String message() {
        return message;
    }
}
