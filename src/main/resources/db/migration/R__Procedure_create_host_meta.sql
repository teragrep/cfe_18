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
CREATE OR REPLACE PROCEDURE insert_host_meta_data(p_host_id INT, host_meta_key VARCHAR(1024),
                                                  host_meta_value VARCHAR(1024))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;

    -- check if host exists for metadata
    IF ((SELECT COUNT(id) FROM cfe_18.host WHERE id = p_host_id) = 0) THEN
        SIGNAL SQLSTATE '45000' SET MYSQL_ERRNO = 50000;
    END IF;

    -- check if key exists
    IF ((SELECT COUNT(meta_key_id) FROM host_meta_key WHERE host_meta_key = meta_key_name) = 0) THEN
        INSERT INTO cfe_18.host_meta_key(meta_key_name) VALUES (host_meta_key);
    END IF;

    -- select key into variable
    SELECT meta_key_id INTO @keyId FROM cfe_18.host_meta_key WHERE meta_key_name = host_meta_key;

    INSERT INTO cfe_18.host_meta(host_id, meta_key_id, meta_value) VALUES (p_host_id, @keyId, host_meta_value);

    -- return host_id as signal
    SELECT distinct hm.host_id AS host_id from cfe_18.host_meta hm where hm.host_id=p_host_id;
    COMMIT;
END;
//
DELIMITER ;
