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
CREATE OR REPLACE PROCEDURE insert_cfe_04_storage_sourcetype(p_storage_id INT, p_sourcetype_id INT,
                                                             p_maxdaysago VARCHAR(255), p_category VARCHAR(255),
                                                             p_sourcedescription VARCHAR(255), p_truncate VARCHAR(255),
                                                             p_freeform_indexer_enabled BOOLEAN,
                                                             p_freeform_indexer_text VARCHAR(255),
                                                             p_freeform_lb_enabled BOOLEAN,
                                                             p_freeform_lb_text VARCHAR(255))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    -- record checks
    -- makes sure exact record exists in storage_sourcetypes
    IF ((SELECT COUNT(*)
         FROM cfe_18.storage_sourcetypes
         WHERE storage_id = p_storage_id
           AND sourcetype_id = p_sourcetype_id) = 0) THEN
        -- record does not exist mysql_errno
        SIGNAL SQLSTATE '45000' SET MYSQL_ERRNO = 50000;
    END IF;

    IF ((SELECT COUNT(*) FROM cfe_18.storages WHERE id = p_storage_id) = 0) THEN
        -- record does not exist mysql_errno
        SIGNAL SQLSTATE '45000' SET MYSQL_ERRNO = 50000;
    END IF;

    IF ((SELECT COUNT(*)
         FROM cfe_18.cfe_04_sourcetypes
         WHERE p_storage_id = cfe_04_id
           AND p_sourcetype_id = capture_sourcetype_id
           AND p_maxdaysago = maxdaysago
           AND p_category = category
           AND p_sourcedescription = sourcedescription
           AND p_truncate = truncate
           AND p_freeform_indexer_enabled = freeform_indexer_enabled
           AND p_freeform_indexer_text = freeform_indexer_text
           AND p_freeform_lb_enabled = freeform_lb_enabled
           AND p_freeform_lb_text = freeform_lb_text) = 0) THEN
        INSERT INTO cfe_18.cfe_04_sourcetypes(cfe_04_id, capture_sourcetype_id, maxdaysago, category, sourcedescription,
                                              truncate, freeform_indexer_enabled, freeform_indexer_text,
                                              freeform_lb_enabled, freeform_lb_text)
        VALUES (p_storage_id, p_sourcetype_id, p_maxdaysago, p_category, p_sourcedescription, p_truncate,
                p_freeform_indexer_enabled, p_freeform_indexer_text, p_freeform_lb_enabled, p_freeform_lb_text);

    END IF;

    -- return storage id as signal
    SELECT cfe_04_id AS storage_id
    FROM cfe_18.cfe_04_sourcetypes
    WHERE cfe_04_id = p_storage_id
      AND capture_sourcetype_id = p_sourcetype_id
      AND p_maxdaysago = maxdaysago
      AND p_category = category
      AND p_sourcedescription = sourcedescription
      AND p_truncate = truncate
      AND p_freeform_indexer_enabled = freeform_indexer_enabled
      AND p_freeform_indexer_text = freeform_indexer_text
      AND p_freeform_lb_enabled = freeform_lb_enabled
      AND p_freeform_lb_text = freeform_lb_text;

    COMMIT;
END;
//
DELIMITER ;
