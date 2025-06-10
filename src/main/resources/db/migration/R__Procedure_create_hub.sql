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
USE location;
DELIMITER //
CREATE OR REPLACE PROCEDURE insert_cfe_hub(fqhost VARCHAR(128), md5 VARCHAR(32),
                                           ip VARCHAR(255))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    IF ((SELECT COUNT(id)
         FROM location.host h
         WHERE h.MD5 = md5
           AND h.fqhost = fqhost
           AND h.host_type = 'cfe') = 0) THEN

        INSERT INTO location.host(MD5, fqhost, host_type)
        VALUES (md5, fqhost, 'cfe');
        SELECT LAST_INSERT_ID() INTO @hid;
    ELSE
        SELECT id INTO @hid FROM location.host h WHERE h.MD5 = md5 AND h.fqhost = fqhost AND h.host_type = 'cfe';
    END IF;

    IF ((SELECT COUNT(h.host_id)
         FROM cfe_00.hubs h
         WHERE h.host_id = @hid
           AND h.ip = ip
           AND h.host_type = 'cfe') = 0) THEN

        INSERT INTO cfe_00.hubs(host_id, ip, host_type)
        VALUES (@hid, ip, 'cfe');
        SELECT LAST_INSERT_ID() INTO @id;
    ELSE
        SELECT id INTO @id FROM cfe_00.hubs h WHERE h.host_id = @hid AND h.ip = ip AND h.host_type = 'cfe';
    END IF;

    IF ((SELECT COUNT(host_id)
         FROM cfe_00.host_type_cfe htc
         WHERE htc.host_id = @hid
           AND htc.host_type = 'cfe'
           AND htc.hub_id = @id) = 0) THEN

        INSERT INTO cfe_00.host_type_cfe(host_id, host_type, hub_id)
        VALUES (@hid, 'cfe', @id);
    END IF;
    COMMIT;
    SELECT @id AS id;

END;
//
DELIMITER ;