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
CREATE OR REPLACE PROCEDURE insert_file_processing_type(meta_template_filename VARCHAR(255), meta_rule VARCHAR(1000),
                                                        meta_rule_name VARCHAR(255),
                                                        meta_inputtype ENUM ('regex','newline'),
                                                        meta_inputvalue VARCHAR(255))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;

    -- if record does not exist then insert new one
    IF ((SELECT COUNT(id)
         FROM cfe_18.file_processing_type fpt
         WHERE fpt.name = meta_rule_name
           AND fpt.inputtype = meta_inputtype
           AND fpt.inputvalue = meta_inputvalue
           AND fpt.ruleset = meta_rule
           AND fpt.template = meta_template_filename) = 0) THEN

        INSERT INTO cfe_18.file_processing_type(name, inputtype, inputvalue, ruleset, template)
        VALUES (meta_rule_name, meta_inputtype, meta_inputvalue, meta_rule, meta_template_filename);
        SELECT LAST_INSERT_ID() AS id;

        -- if record exists then select the ID
    ELSE
        SELECT id AS id
        FROM cfe_18.file_processing_type fpt
        WHERE fpt.name = meta_rule_name
          AND fpt.inputtype = meta_inputtype
          AND fpt.inputvalue = meta_inputvalue
          AND fpt.ruleset = meta_rule
          AND fpt.template = meta_template_filename;
    END IF;

END;
//
DELIMITER ;