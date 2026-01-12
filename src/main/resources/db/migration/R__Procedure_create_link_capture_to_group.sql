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


-- Links capture to a group
-- Takes capture_group name and type
-- returns ID of the group created
USE cfe_18;
DELIMITER //
CREATE OR REPLACE PROCEDURE insert_capture_to_group(capture_id INT, capture_group_id INT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;

    -- check if capture_definition ID is valid
    IF ((SELECT COUNT(c.id) FROM cfe_18.capture_definition c WHERE c.id = capture_id) = 0) THEN
        SELECT JSON_OBJECT('id', capture_id, 'message', 'Capture does not exist') INTO @capture;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @capture;
    END IF;

    -- check if capture_group ID is valid
    IF ((SELECT COUNT(cdg.id) FROM cfe_18.capture_def_group cdg WHERE cdg.id = capture_group_id) = 0) THEN
        SELECT JSON_OBJECT('id', capture_group_id, 'message', 'Group does not exist') INTO @group;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @group;
    END IF;

    -- check if types match. Select given capture and type while subquerying capture groups type to see if they match.
    -- If no matching rows is found then type is mismatched.
    IF ((SELECT COUNT(id)
         FROM cfe_18.capture_definition c
         WHERE c.capture_type = (SELECT capture_type FROM cfe_18.capture_def_group cdg WHERE cdg.id = capture_group_id)
           AND c.id = capture_id) = 0) THEN
        SELECT JSON_OBJECT('id', capture_group_id, 'message', 'Type mismatch between capture and group') INTO @mismatch;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @mismatch;
    END IF;

    -- stored variables are readable than subqueries
    SELECT tag_id INTO @tagId FROM cfe_18.capture_definition c WHERE c.id = capture_id;
    SELECT capture_type INTO @type FROM cfe_18.capture_def_group WHERE id = capture_group_id;
    -- flow_id is used from Capture Group rather than Capture Definition.
    SELECT flow_id into @flowId from cfe_18.capture_def_group where id = capture_group_id;

    -- if record does not exist
    IF ((SELECT COUNT(id)
         FROM cfe_18.capture_def_group_x_capture_def
         WHERE capture_def_group_id = capture_group_id
           AND capture_def_id = capture_id
           AND tag_id = @tagId
           AND capture_type = @type) = 0) THEN
        -- subqueries fetch relevant information from capture_def for insertion
        INSERT INTO capture_def_group_x_capture_def(capture_def_id, capture_def_group_id, tag_id, capture_type, flow_id)
        VALUES (capture_id, capture_group_id, @tagId, @type, @flowId);

        -- return ID
        SELECT capture_def_group_id AS id
        FROM cfe_18.capture_def_group_x_capture_def cdgxcd
        WHERE cdgxcd.capture_def_group_id = capture_group_id
          AND cdgxcd.capture_def_id = capture_id;

        -- return ID
    ELSE
        SELECT capture_def_group_id AS id
        FROM cfe_18.capture_def_group_x_capture_def cdgxcd
        WHERE cdgxcd.capture_def_group_id = capture_group_id
          AND cdgxcd.capture_def_id = capture_id;
    END IF;
    COMMIT;
END;
//
DELIMITER ;