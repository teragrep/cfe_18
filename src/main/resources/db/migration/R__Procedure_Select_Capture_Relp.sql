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
CREATE OR REPLACE PROCEDURE select_relp_capture(capture_id INT, tx_id INT)
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

    IF ((SELECT COUNT(id) FROM capture_definition FOR SYSTEM_TIME AS OF TRANSACTION @time WHERE id = capture_id) =
        0) THEN
        SELECT JSON_OBJECT('id', capture_id, 'message', 'Capture does not exist with the given ID') INTO @cid;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @cid;
    END IF;


    SELECT c.id                 AS id,
           t.tag                AS tag,
           rT.retention         AS retention_time,
           c2.category          AS category,
           a.app                AS application,
           cI.captureIndex      AS captureIndex,
           cS.captureSourceType AS source_type,
           L.app_protocol       AS L7,
           f.name               AS flow,
           c.capture_type       AS type
    FROM cfe_18.capture_definition FOR SYSTEM_TIME AS OF TRANSACTION @time c
             INNER JOIN tags FOR SYSTEM_TIME AS OF TRANSACTION @time t ON t.id = c.tag_id
             INNER JOIN application FOR SYSTEM_TIME AS OF TRANSACTION @time a ON c.application_id = a.id
             INNER JOIN captureIndex FOR SYSTEM_TIME AS OF TRANSACTION @time cI ON c.captureIndex_id = cI.id
             INNER JOIN retentionTime FOR SYSTEM_TIME AS OF TRANSACTION @time rT ON c.retentionTime_id = rT.id
             INNER JOIN captureSourcetype FOR SYSTEM_TIME AS OF TRANSACTION @time cS
                        ON c.captureSourcetype_id = cS.id
             INNER JOIN category FOR SYSTEM_TIME AS OF TRANSACTION @time c2 ON c.category_id = c2.id
             INNER JOIN flow.flows FOR SYSTEM_TIME AS OF TRANSACTION @time f ON c.flow_id = f.id
             INNER JOIN flow.L7 FOR SYSTEM_TIME AS OF TRANSACTION @time L ON c.L7_id = L.id
             INNER JOIN capture_type FOR SYSTEM_TIME AS OF TRANSACTION @time ct ON c.capture_type_id = ct.id
    WHERE c.id = capture_id
      AND t.id = c.tag_id
      AND a.id = c.application_id
      AND cI.id = c.captureIndex_id
      AND rT.id = c.retentionTime_id
      AND cS.id = c.captureSourcetype_id
      AND c2.id = c.category_id
      AND f.id = c.flow_id
      AND L.id = c.L7_id
      AND ct.id = c.capture_type_id;
    COMMIT;
END;
//
DELIMITER ;