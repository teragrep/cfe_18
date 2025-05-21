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
CREATE OR REPLACE PROCEDURE select_capture_meta_key_value(meta_key VARCHAR(1024), meta_value VARCHAR(1024), tx_id INT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;

    IF (tx_id) IS NULL THEN
        SET @time = (SELECT MAX(transaction_id) FROM mysql.transaction_registry);
    ELSE
        SET @time = tx_id;
    END IF;

    IF ((SELECT COUNT(c.meta_key_name)
         FROM cfe_18.capture_meta_key FOR SYSTEM_TIME AS OF TRANSACTION @time c
                  INNER JOIN cfe_18.capture_meta FOR SYSTEM_TIME AS OF TRANSACTION @time cm
         WHERE c.meta_key_name = meta_key
           AND cm.meta_value = meta_value) = 0) THEN
        SELECT JSON_OBJECT('id', 0, 'message', 'No such key value pair exists') INTO @nokey;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @nokey;
    END IF;
    -- return list of capture_definitions which are linked to the given key value pair.
    SELECT cd.id                AS capture_id,
           t.tag                AS tag,
           cS.captureSourceType AS sourcetype,
           a.app                AS application,
           cI.captureIndex      AS captureIndex
    FROM cfe_18.capture_definition FOR SYSTEM_TIME AS OF TRANSACTION @time cd
             INNER JOIN capture_meta FOR SYSTEM_TIME AS OF TRANSACTION @time c ON cd.id = c.capture_id
             INNER JOIN capture_meta_key FOR SYSTEM_TIME AS OF TRANSACTION @time cmk ON c.meta_key_id = cmk.meta_key_id
             INNER JOIN tags FOR SYSTEM_TIME AS OF TRANSACTION @time t ON cd.tag_id = t.id
             INNER JOIN captureSourcetype FOR SYSTEM_TIME AS OF TRANSACTION @time cS ON cd.captureSourcetype_id = cS.id
             INNER JOIN application FOR SYSTEM_TIME AS OF TRANSACTION @time a ON cd.application_id = a.id
             INNER JOIN captureIndex FOR SYSTEM_TIME AS OF TRANSACTION @time cI ON cd.captureIndex_id = cI.id
    WHERE c.meta_value = meta_value
      AND cmk.meta_key_name = meta_key;
    COMMIT;
END;
//
DELIMITER ;