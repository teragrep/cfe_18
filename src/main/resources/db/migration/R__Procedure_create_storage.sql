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
USE flow;
DELIMITER //
CREATE OR REPLACE PROCEDURE insert_storage(proc_cfe_type VARCHAR(6), proc_storage_name VARCHAR(255))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    IF ((SELECT COUNT(id)
         FROM flow.storages
         WHERE cfe_type = proc_cfe_type
           AND storage_name = proc_storage_name) > 0) THEN
        SELECT id AS id FROM flow.storages WHERE cfe_type = proc_cfe_type AND storage_name = proc_storage_name;
    ELSE
        IF (proc_cfe_type = 'cfe_04') THEN
            INSERT INTO flow.storages(cfe_type, storage_name) VALUES (proc_cfe_type, proc_storage_name);
            SELECT LAST_INSERT_ID() INTO @id;
            INSERT INTO flow.cfe_04 VALUES (@id, 'cfe_04');
        ELSEIF (proc_cfe_type = 'cfe_10') THEN
            INSERT INTO flow.storages(cfe_type, storage_name) VALUES (proc_cfe_type, proc_storage_name);
            SELECT LAST_INSERT_ID() INTO @id;
            INSERT INTO flow.cfe_10 VALUES (@id, 'cfe_10', 'spool');
        ELSEIF (proc_cfe_type = 'cfe_11') THEN
            INSERT INTO flow.storages(cfe_type, storage_name) VALUES (proc_cfe_type, proc_storage_name);
            SELECT LAST_INSERT_ID() INTO @id;
            INSERT INTO flow.cfe_11 VALUES (@id, 'cfe_11', 'inspection');
        ELSEIF (proc_cfe_type = 'cfe_12') THEN
            INSERT INTO flow.storages(cfe_type, storage_name) VALUES (proc_cfe_type, proc_storage_name);
            SELECT LAST_INSERT_ID() INTO @id;
            INSERT INTO flow.cfe_12 VALUES (@id, 'cfe_12');
        ELSEIF (proc_cfe_type = 'cfe_19') THEN
            INSERT INTO flow.storages(cfe_type, storage_name) VALUES (proc_cfe_type, proc_storage_name);
            SELECT LAST_INSERT_ID() INTO @id;
            INSERT INTO flow.cfe_19 VALUES (@id, 'cfe_19');
        ELSEIF (proc_cfe_type = 'cfe_23') THEN
            INSERT INTO flow.storages(cfe_type, storage_name) VALUES (proc_cfe_type, proc_storage_name);
            SELECT LAST_INSERT_ID() INTO @id;
            INSERT INTO flow.cfe_23 VALUES (@id, 'cfe_23');
        ELSE
            -- This is presuming that no storage can be created with the type thus no ID can be returned.
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid storage type';
        END IF;
        SELECT @id AS id;

    END IF;
    COMMIT;
END;
//
DELIMITER ;
