    /*
 * Main data management system (MDMS) cfe_18
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
CREATE OR REPLACE PROCEDURE retrieve_capture_meta_key_value(meta_key varchar(1024),meta_value varchar(1024))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    -- check for existence of capture meta value before attempting retrieval. Throws custom error.
        if((select count(cmk.meta_key_name) from cfe_18.capture_meta_key cmk where cmk.meta_key_name=meta_key)=0) then
            -- standardized JSON error response
            SELECT JSON_OBJECT('id', 0, 'message', 'Capture meta KEY does not exist with given KEY value') into @nokey;
            signal sqlstate '42000' set message_text = @nokey;
    -- check for existence of capture meta key before attempting retrieval. Throws custom error.
        elseif((select count(cm.meta_value) from cfe_18.capture_meta cm where cm.meta_value=meta_value)=0) then
             -- standardized JSON error response
            SELECT JSON_OBJECT('id', 0, 'message', 'Capture meta VALUE does not exist with given meta VALUE') into @nokey;
            signal sqlstate '42000' set message_text = @nokey;
        end if;
    -- return list of capture_definitions which are linked to the given key value pair.
        select cd.id as capture_id,
               t.tag as tag,
               cS.captureSourceType as sourcetype,
               a.app as application,
               cI.captureIndex as captureIndex
        from cfe_18.capture_definition cd
            inner join capture_meta c on cd.id = c.capture_id
            inner join capture_meta_key cmk on c.meta_key_id = cmk.meta_key_id
            inner join tags t on cd.tag_id=t.id
            inner join captureSourcetype cS on cd.captureSourcetype_id = cS.id
            inner join application a on cd.application_id = a.id
            inner join captureIndex cI  on cd.captureIndex_id = cI.id
                 WHERE c.meta_value=meta_value AND
                      cmk.meta_key_name=meta_key;

    COMMIT;
END;
//
DELIMITER ;