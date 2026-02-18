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
USE flow;

CREATE TABLE flow.cfe_04_transforms
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    cfe_04_id       INT          NOT NULL,
    transform_name  VARCHAR(255) NOT NULL,
    write_meta      BOOLEAN      NOT NULL,
    write_default   BOOLEAN      NOT NULL,
    default_value   VARCHAR(255) NOT NULL,
    destination_key VARCHAR(255) NOT NULL,
    regex           VARCHAR(255) NOT NULL,
    format          VARCHAR(255) NOT NULL,
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES flow.cfe_04 (id),
    UNIQUE (cfe_04_id, id),
    UNIQUE (cfe_04_id, transform_name),
    start_trxid     BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid       BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;