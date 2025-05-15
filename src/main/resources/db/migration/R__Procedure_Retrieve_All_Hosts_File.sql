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
CREATE OR REPLACE PROCEDURE select_all_file_hosts(tx_id INT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    IF (tx_id) IS NULL THEN
        SET @time = (SELECT MAX(transaction_id) FROM mysql.transaction_registry);
    ELSE
        SET @time = tx_id;
    END IF;
    SELECT h.id      AS id,
           h.md5     AS host_md5,
           h.fqhost  AS host_fq,
           h2.id     AS hub_id,
           h3.fqhost AS hub_fq
    FROM location.host FOR SYSTEM_TIME AS OF TRANSACTION @time h
             LEFT JOIN cfe_00.host_type_cfe FOR SYSTEM_TIME AS OF TRANSACTION @time htc ON h.id = htc.host_id
             LEFT JOIN cfe_00.hubs FOR SYSTEM_TIME AS OF TRANSACTION @time h2 ON htc.hub_id = h2.id
             LEFT JOIN location.host FOR SYSTEM_TIME AS OF TRANSACTION @time h3 ON h2.host_id = h3.id
    WHERE h.host_type = 'cfe';
END;
//
DELIMITER ;
