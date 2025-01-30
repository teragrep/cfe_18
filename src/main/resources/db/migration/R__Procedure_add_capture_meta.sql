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
use cfe_18;
DELIMITER //
CREATE OR REPLACE PROCEDURE add_capture_meta(capture_id int,capture_meta_key varchar(1024), capture_meta_value varchar(1024))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    -- check if capture exists for metadata
    if(select id from cfe_18.capture_definition where id=capture_id) is null then
        -- standardized JSON error response
        SELECT JSON_OBJECT('id', capture_id, 'message', 'Capture does not exist with given ID') into @nocapture;
        signal sqlstate '42000' set message_text = @nocapture;
    end if;

    -- check if similar row exists already to avoid duplication
    if(select cd.id
        from cfe_18.capture_definition cd
                    inner join capture_meta cm on cd.id = cm.capture_id
                    inner join capture_meta_key cmk on cm.meta_key_id = cmk.meta_key_id
                     where cd.id=capture_id
                     and cm.meta_value=capture_meta_value
                     and cmk.meta_key_name=capture_meta_key) is null then
    -- insert new record
    insert into cfe_18.capture_meta_key(meta_key_name) values (capture_meta_key);
        insert into cfe_18.capture_meta(capture_id,meta_key_id,meta_value) values(
            capture_id
            ,(select last_insert_id())
            ,capture_meta_value);
    end if;
    -- return given application name and capture_id as signal
       select cd.id as capture_id
       from capture_definition cd
        where cd.id=capture_id;
    COMMIT;
END;
//
DELIMITER ;