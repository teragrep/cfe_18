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
CREATE OR REPLACE PROCEDURE retrieve_all_captures_pagination(tx_id int, page_size int, last_id int)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        end;
        if(tx_id) is null then
             set @time = (select max(transaction_id) from mysql.transaction_registry);
        else
             set @time=tx_id;
        end if;
    select cd.id                     as captureID,
           a.app                     as application,
           c.category                as category,
           cS.captureSourceType      as sourcetype,
           cI.captureIndex           as captureIndex,
           rT.retention              as retention,
           t.tag                     as tag,
           f.name                    as flow,
           L7.app_protocol           as L7,
           ct.capture_type           as type,
           cg.capture_def_group_name as groupName,
           cmf.tagPath               as tagpath,
           cmf.capturePath           as capturepath,
           pt.type_name              as processing_type
    from cfe_18.capture_definition  for system_time as of transaction  @time  cd
             inner join application for system_time as of transaction @time a on cd.application_id = a.id
             inner join category for system_time as of transaction @time c on cd.category_id = c.id
             inner join captureSourcetype for system_time as of transaction @time cS on cd.captureSourcetype_id = cS.id
             inner join captureIndex for system_time as of transaction @time cI on cd.captureIndex_id = cI.id
             inner join retentionTime for system_time as of transaction @time rT on cd.retentionTime_id = rT.id
             inner join tags for system_time as of transaction @time t on cd.tag_id = t.id
             inner join flow.flows for system_time as of transaction @time f on cd.flow_id = f.id
             inner join flow.capture_sink for system_time as of transaction @time cas on cd.flow_id = cas.flow_id and cd.L7_id = cas.L7_id
             inner join flow.L7 for system_time as of transaction @time L7 on cd.L7_id = L7.id
             left join capture_type for system_time as of transaction @time ct on cd.capture_type_id = ct.id
             left join capture_def_group_x_capture_def for system_time as of transaction @time cdgxcd
                       on cd.id = cdgxcd.capture_def_id and cd.capture_type = cdgxcd.capture_type and
                          t.id = cdgxcd.tag_id
             left join capture_def_group for system_time as of transaction @time cg on cdgxcd.capture_def_group_id = cg.id
             left join capture_meta_file for system_time as of transaction @time cmf on ct.id = cmf.id and ct.capture_type = cmf.capture_type
             left join processing_type for system_time as of transaction @time pt on cmf.processing_type_id = pt.id
             JOIN (SELECT cd2.id FROM cfe_18.capture_definition for SYSTEM_TIME as of TRANSACTION @time cd2 where cd2.id>last_id ORDER BY cd2.id LIMIT page_size)
                 AS page_ids ON cd.id = page_ids.id
            ORDER BY cd.id;

end;
//
DELIMITER ;
