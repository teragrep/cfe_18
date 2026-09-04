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
CREATE OR REPLACE PROCEDURE insert_cfe_04_storage_index(p_storage_id INT, p_index_id INT, p_repFactor VARCHAR(255),
                                                        p_disabled BOOLEAN, p_homePath VARCHAR(255),
                                                        p_coldPath VARCHAR(255), p_thawedPath VARCHAR(255))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;

    IF ((SELECT COUNT(*) FROM cfe_18.storage_indexes WHERE storage_id = p_storage_id AND index_id = p_index_id) = 0) THEN
            insert into cfe_18.storage_indexes values(p_storage_id,p_index_id);

    END IF;

    IF ((SELECT COUNT(*) FROM cfe_18.storages WHERE id = p_storage_id) = 0) THEN
        -- record does not exist mysql_errno
        SIGNAL SQLSTATE '45000' SET MYSQL_ERRNO = 50000;
    END IF;

    -- only insert if exact row does not exist
    IF ((SELECT COUNT(*)
         FROM cfe_18.cfe_04_indexes
         WHERE cfe_04_id = p_storage_id
           AND capture_index_id = p_index_id
           AND repFactor = p_repFactor
           AND disabled = p_disabled
           AND homePath = p_homePath
           AND coldPath = p_coldPath
           AND thawedPath = p_thawedPath) = 0) THEN
        INSERT INTO cfe_18.cfe_04_indexes(cfe_04_id, capture_index_id, repFactor, disabled, homePath, coldPath,
                                          thawedPath)
        VALUES (p_storage_id, p_index_id, p_repFactor, p_disabled, p_homePath, p_coldPath, p_thawedPath);

    END IF;

    -- return storage id as signal
    SELECT cfe_04_id AS storage_id
    FROM cfe_18.cfe_04_indexes
    WHERE cfe_04_id = p_storage_id
           AND capture_index_id = p_index_id
           AND repFactor = p_repFactor
           AND disabled = p_disabled
           AND homePath = p_homePath
           AND coldPath = p_coldPath
           AND thawedPath = p_thawedPath;
    COMMIT;
END;
//
DELIMITER ;
