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
CREATE OR REPLACE PROCEDURE insert_capture_meta(capture_id INT, capture_meta_key VARCHAR(1024),
                                                capture_meta_value VARCHAR(1024))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    -- check if capture exists for metadata
    IF ((SELECT COUNT(id) FROM cfe_18.capture_definition WHERE id = capture_id) = 0) THEN
        -- standardized JSON error response
        SELECT JSON_OBJECT('id', capture_id, 'message', 'Capture does not exist with given ID') INTO @nocapture;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @nocapture;
    END IF;

    -- check if capture already has key value
    IF ((SELECT COUNT(cd.id)
         FROM cfe_18.capture_definition cd
                  INNER JOIN capture_meta cm ON cd.id = cm.capture_id
                  INNER JOIN capture_meta_key cmk ON cm.meta_key_id = cmk.meta_key_id
         WHERE cd.id = capture_id
           AND cm.meta_value = capture_meta_value
           AND cmk.meta_key_name = capture_meta_key) = 0) THEN
        -- insert new record
        INSERT INTO cfe_18.capture_meta_key(meta_key_name) VALUES (capture_meta_key);
        -- insert with subquery to insert correct key
        INSERT INTO cfe_18.capture_meta(capture_id, meta_key_id, meta_value)
        VALUES (capture_id, (SELECT LAST_INSERT_ID()), capture_meta_value);
        -- return ID
    END IF;

    -- return ID
    SELECT cd.id AS id FROM capture_definition cd WHERE cd.id = capture_id;

    COMMIT;
END;
//
DELIMITER ;