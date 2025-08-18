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
CREATE OR REPLACE PROCEDURE insert_capture_group(group_name VARCHAR(255), type VARCHAR(255), p_flow_id INT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    if ((select COUNT(id) from flow.flows f where f.id =p_flow_id)=0) then
        SELECT JSON_OBJECT('id', p_flow_id, 'message', 'Invalid flow_id') INTO @f;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @f;
    end if;

    IF ((SELECT COUNT(id)
         FROM cfe_18.capture_def_group cdg
         WHERE cdg.capture_def_group_name = group_name) = 0) THEN
        INSERT INTO cfe_18.capture_def_group(capture_def_group_name, capture_type, flow_id)
        VALUES (group_name, type, p_flow_id);
        SELECT LAST_INSERT_ID() AS id;
    ELSE
        SELECT id AS id
        FROM cfe_18.capture_def_group cdg
        WHERE cdg.capture_def_group_name = group_name;
    END IF;
    COMMIT;
END;
//
DELIMITER ;