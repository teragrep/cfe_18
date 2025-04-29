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
USE cfe_18;
DELIMITER //
CREATE OR REPLACE PROCEDURE insert_g_x_g(proc_host_group_id INT, proc_capture_group_id INT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;

    -- check if types match
    IF ((SELECT DISTINCT capture_type
         FROM cfe_18.capture_def_group_x_capture_def
         WHERE capture_def_group_id = proc_capture_group_id)
        != (SELECT DISTINCT host_type FROM location.host_group_x_host WHERE host_group_id = proc_host_group_id)) THEN

            SELECT JSON_OBJECT('id', proc_capture_group_id, 'message', ' type mismatch between host group and capture group') INTO @gxg;
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @gxg;
    END IF;

    --  Check if host group exists before junction
    IF ((SELECT COUNT(host_group_id)
         FROM location.host_group_x_host
         WHERE host_group_id = proc_host_group_id) = 0) THEN

            SELECT JSON_OBJECT('id', proc_capture_group_id, 'message', ' HOST group does not exist') INTO @gxgh;
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @gxgh;
    END IF;

    --  Check if capture group exists before junction
    IF ((SELECT COUNT(capture_def_group_id)
         FROM cfe_18.capture_def_group_x_capture_def
         WHERE capture_def_group_id = proc_capture_group_id) = 0) THEN

            SELECT JSON_OBJECT('id', proc_capture_group_id, 'message', ' CAPTURE group does not exist') INTO @gxgc;
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @gxgc;
    END IF;

    --  Insert into junction table
    IF ((SELECT COUNT(id)
         FROM cfe_18.host_groups_x_capture_def_group
         WHERE host_group_id = proc_host_group_id
           AND capture_group_id = proc_capture_group_id) = 0) THEN

        INSERT INTO cfe_18.host_groups_x_capture_def_group(host_group_id, capture_group_id)
        VALUES (proc_host_group_id, proc_capture_group_id);
        -- return ID
        SELECT LAST_INSERT_ID() AS id;
    ELSE
        -- return ID
        SELECT hgxcdg.id AS id
        FROM cfe_18.host_groups_x_capture_def_group hgxcdg
        WHERE hgxcdg.capture_group_id = proc_capture_group_id
          AND hgxcdg.host_group_id = proc_host_group_id;
    END IF;
    COMMIT;
END;

//
DELIMITER ;