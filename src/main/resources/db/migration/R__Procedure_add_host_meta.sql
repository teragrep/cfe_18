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
USE cfe_03;
DELIMITER //
CREATE OR REPLACE PROCEDURE insert_host_meta(p_arch VARCHAR(255), p_flavor VARCHAR(255), p_hostname VARCHAR(255),
                                             p_host_id INT, p_os VARCHAR(255),
                                             p_release_ver VARCHAR(255))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    -- check if host is valid
    IF ((SELECT COUNT(id) FROM location.host WHERE id = p_host_id) = 0) THEN
        SELECT JSON_OBJECT('id', p_host_id, 'message', 'Host does not exist') INTO @hmid;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @hmid;
    END IF;

    -- check if host already has hostmeta
    IF ((SELECT COUNT(id) FROM cfe_03.host_meta WHERE host_id = p_host_id) > 0) THEN
        SELECT JSON_OBJECT('id', p_host_id, 'message', 'Host already has hostmeta') INTO @hid;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @hid;
    END IF;

    IF ((SELECT COUNT(id) FROM cfe_03.os_type WHERE os = p_os) = 0) THEN
        INSERT INTO cfe_03.os_type(os) VALUES (p_os);
        SELECT LAST_INSERT_ID() INTO @Os_id;
    ELSE
        SELECT id INTO @Os_id FROM cfe_03.os_type WHERE os = p_os;
    END IF;

    IF ((SELECT COUNT(id) FROM cfe_03.release_version WHERE rel_ver = p_release_ver) = 0) THEN
        INSERT INTO cfe_03.release_version(rel_ver) VALUES (p_release_ver);
        SELECT LAST_INSERT_ID() INTO @RelVer_id;
    ELSE
        SELECT id INTO @RelVer_id FROM cfe_03.release_version WHERE rel_ver = p_release_ver;
    END IF;

    IF ((SELECT COUNT(id) FROM cfe_03.arch_type WHERE arch = p_arch) = 0) THEN
        INSERT INTO cfe_03.arch_type(arch) VALUES (p_arch);
        SELECT LAST_INSERT_ID() INTO @Arch_id;
    ELSE
        SELECT id INTO @Arch_id FROM cfe_03.arch_type WHERE arch = p_arch;
    END IF;

    IF ((SELECT COUNT(id) FROM cfe_03.flavor_type WHERE flavor = p_flavor) = 0) THEN
        INSERT INTO cfe_03.flavor_type(flavor) VALUES (p_flavor);
        SELECT LAST_INSERT_ID() INTO @Flavor_id;
    ELSE
        SELECT id INTO @Flavor_id FROM cfe_03.flavor_type WHERE flavor = p_flavor;
    END IF;

    -- if row exists
    IF ((SELECT COUNT(id)
         FROM cfe_03.host_meta
         WHERE arch_id = @Arch_id
           AND flavor_id = @Flavor_id
           AND os_id = @Os_id
           AND release_ver_id = @RelVer_id
           AND hostname = p_hostname
           AND host_id = p_host_id) > 0) THEN
        -- return ID
        SELECT id AS id
        FROM cfe_03.host_meta
        WHERE arch_id = @Arch_id
          AND flavor_id = @Flavor_id
          AND os_id = @Os_id
          AND release_ver_id = @RelVer_id
          AND hostname = p_hostname
          AND host_id = p_host_id;
    ELSE
        INSERT INTO cfe_03.host_meta(arch_id, flavor_id, os_id,
                                     release_ver_id, hostname, host_id)
        VALUES (@Arch_id, @Flavor_id, @Os_id, @RelVer_id, p_hostname, p_host_id);
        -- return ID
        SELECT LAST_INSERT_ID() AS id;
    END IF;
    COMMIT;
END;
//
DELIMITER ;

