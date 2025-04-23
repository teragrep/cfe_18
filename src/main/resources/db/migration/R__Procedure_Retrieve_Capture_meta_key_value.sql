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
CREATE OR REPLACE PROCEDURE retrieve_capture_meta_key_value(meta_key varchar(1024),meta_value varchar(1024),tx_id int)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;

    if(tx_id) is null then
        set @time = (select max(transaction_id) from mysql.transaction_registry);
    else
        set @time=tx_id;
    end if;

        if((select count(c.meta_key_name) from cfe_18.capture_meta_key for system_time as of transaction @time c
                                          inner join cfe_18.capture_meta for system_time as of transaction @time cm
                                          where c.meta_key_name=meta_key AND cm.meta_value=meta_value)=0) then
            -- standardized JSON error response
            SELECT JSON_OBJECT('id', 0, 'message', 'No such key value pair exists') into @nokey;
            signal sqlstate '42000' set message_text = @nokey;
        end if;
    -- return list of capture_definitions which are linked to the given key value pair.
        select cd.id as capture_id,
               t.tag as tag,
               cS.captureSourceType as sourcetype,
               a.app as application,
               cI.captureIndex as captureIndex
        from cfe_18.capture_definition for system_time as of transaction @time cd
            inner join capture_meta for system_time as of transaction @time c on cd.id = c.capture_id
            inner join capture_meta_key for system_time as of transaction @time cmk on c.meta_key_id = cmk.meta_key_id
            inner join tags for system_time as of transaction @time t on cd.tag_id=t.id
            inner join captureSourcetype for system_time as of transaction @time cS on cd.captureSourcetype_id = cS.id
            inner join application for system_time as of transaction @time a on cd.application_id = a.id
            inner join captureIndex for system_time as of transaction @time cI  on cd.captureIndex_id = cI.id
                 WHERE c.meta_value=meta_value AND
                      cmk.meta_key_name=meta_key;

    COMMIT;
END;
//
DELIMITER ;