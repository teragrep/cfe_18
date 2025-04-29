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
CREATE OR REPLACE PROCEDURE insert_cfe_host(proc_MD5 VARCHAR(32), proc_fqhost VARCHAR(128), proc_hub_fq VARCHAR(128))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;

    -- type check
    IF ((SELECT COUNT(id) FROM location.host WHERE MD5 = proc_MD5 AND fqhost = proc_fqhost AND host_type != 'cfe') >
        0) THEN
        SELECT JSON_OBJECT('id', (SELECT id FROM location.host WHERE MD5 = proc_MD5 AND fqhost = proc_fqhost),
                           'message', 'Host exists with different type')
        INTO @hid;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @hid;
    END IF;

    IF ((SELECT COUNT(cfe_00.hubs.id)
         FROM cfe_00.hubs
                  INNER JOIN(SELECT id FROM location.host WHERE location.host.fqhost = proc_hub_fq) AS hi
         WHERE hubs.host_id = hi.id) = 0) THEN
        SELECT JSON_OBJECT('id', @hubs_id, 'message', 'Hub does not exist') INTO @hub;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @hub;
    ELSE
        IF ((SELECT COUNT(h.id)
             FROM location.host h
             WHERE h.MD5 = proc_MD5
               AND h.fqhost = proc_fqhost
               AND h.host_type = 'cfe') = 0) THEN
            -- insert base table
            INSERT INTO location.host(MD5, fqhost, host_type)
            VALUES (proc_MD5, proc_fqhost, 'cfe');

            SELECT LAST_INSERT_ID() AS id;

            -- insert type table which links the host to a hub
            INSERT INTO cfe_00.host_type_cfe(host_id, host_type, hub_id)
            VALUES (@id, 'cfe', (SELECT cfe_00.hubs.id
                                 FROM cfe_00.hubs
                                          INNER JOIN(SELECT id
                                                     FROM location.host
                                                     WHERE location.host.fqhost = proc_hub_fq) AS hi
                                 WHERE hubs.host_id = hi.id));

        ELSE
            -- return ID
            SELECT h.id
                       AS id
            FROM location.host h
            WHERE h.MD5 = proc_MD5
              AND h.fqhost = proc_fqhost
              AND h.host_type = 'cfe';
        END IF;
    END IF;
    COMMIT;
END;
//
DELIMITER ;