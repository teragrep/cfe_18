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
CREATE OR REPLACE PROCEDURE select_hosts_in_group(host_group_id INT, tx_id INT)
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
    IF ((SELECT COUNT(hg.id)
         FROM location.host_group FOR SYSTEM_TIME AS OF TRANSACTION @time hg
         WHERE hg.id = host_group_id) = 0) THEN
        SELECT JSON_OBJECT('id', host_group_id, 'message', 'Host group not found') INTO @hg;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @hg;
    END IF;

    SELECT h.id AS host_id
    FROM location.host FOR SYSTEM_TIME AS OF TRANSACTION @time h
             INNER JOIN location.host_group_x_host FOR SYSTEM_TIME AS OF TRANSACTION @time hgxh ON h.id = hgxh.host_id
             INNER JOIN location.host_group FOR SYSTEM_TIME AS OF TRANSACTION @time hg ON hgxh.host_group_id = hg.id
    WHERE hg.id = host_group_id;
    COMMIT;
END;
//
DELIMITER ;