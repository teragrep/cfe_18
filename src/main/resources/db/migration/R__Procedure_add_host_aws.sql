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
DELIMITER //

CREATE OR REPLACE PROCEDURE insert_aws_host(proc_MD5 VARCHAR(32), proc_fqhost VARCHAR(128),
                                            proc_host_type VARCHAR(20), proc_account_id BIGINT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    IF ((SELECT COUNT(h.id)
         FROM location.host h
                  INNER JOIN location.host_type_aws hta ON hta.id = h.id
         WHERE h.MD5 = proc_MD5
           AND h.fqhost = proc_fqhost
           AND h.host_type = proc_host_type
           AND hta.accountId = proc_account_id) = 0) THEN
        INSERT INTO location.host(MD5, fqhost, host_type) VALUES (proc_MD5, proc_fqhost, proc_host_type);
        SELECT LAST_INSERT_ID() INTO @id;
        INSERT INTO location.host_type_aws VALUES (LAST_INSERT_ID(), proc_account_id, 'aws');
    ELSE
        SELECT h.id
        INTO @id
        FROM location.host h
                 INNER JOIN location.host_type_aws hta ON h.id = hta.id
        WHERE h.MD5 = proc_MD5
          AND h.fqhost = proc_fqhost
          AND h.host_type = proc_host_type
          AND hta.accountId = proc_account_id;
    END IF;
    COMMIT;
    -- return ID
    SELECT @id AS id;
END;
//
DELIMITER ;

