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
CREATE OR REPLACE PROCEDURE insert_cfe_04_transform(p_cfe_04_id INT,
                                                      p_name VARCHAR(255),
                                                      p_write_meta BOOLEAN,
                                                      p_write_default BOOLEAN,
                                                      p_default_value VARCHAR(255),
                                                      p_destination_key VARCHAR(255),
                                                      p_regex VARCHAR(255),
                                                      p_format VARCHAR(255))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    -- check if row exists before inserting new one, if so then just return that ID
    IF ((SELECT COUNT(id)
         FROM flow.cfe_04_transforms t
         WHERE t.cfe_04_id = p_cfe_04_id
           AND t.name = p_name
           AND t.write_meta = p_write_meta
           AND t.write_default = p_write_default
           AND t.default_value = p_default_value
           AND t.destination_key = p_destination_key
           AND t.regex = p_regex
           AND t.format = p_format) > 0) THEN

        SELECT t.id AS id
        FROM flow.cfe_04_transforms t
        WHERE t.cfe_04_id = p_cfe_04_id
          AND t.name = p_name
          AND t.write_meta = p_write_meta
          AND t.write_default = p_write_default
          AND t.default_value = p_default_value
          AND t.destination_key = p_destination_key
          AND t.regex = p_regex
          AND t.format = p_format;
    ELSE
        INSERT INTO flow.cfe_04_transforms(cfe_04_id, name, write_meta, write_default, default_value, destination_key,
                                           regex, format)
        VALUES (p_cfe_04_id, p_name, p_write_meta, p_write_default, p_default_value, p_destination_key, p_regex,
                p_format);

        -- return ID
        SELECT LAST_INSERT_ID() AS id;
    END IF;
    COMMIT;
END;
//
DELIMITER ;