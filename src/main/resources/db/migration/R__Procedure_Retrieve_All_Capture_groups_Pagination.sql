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
CREATE OR REPLACE PROCEDURE retrieve_all_capture_groups_pagination(tx_id int, page_size int, last_id int)
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
    select cdg.capture_def_group_name as group_name,
           cdg.capture_type           as group_type,
           t.tag                      as capture_tag,
           cd.id                      as capture_id
    from cfe_18.capture_def_group for system_time as of transaction @time cdg
             inner join capture_def_group_x_capture_def for system_time as of transaction @time cdgxcd  on cdg.id = cdgxcd.capture_def_group_id
             inner join capture_definition  for system_time as of transaction @time cd
                        on cdgxcd.capture_def_id = cd.id and cdgxcd.capture_type = cd.capture_type
             inner join tags for system_time as of transaction @time t  on cd.tag_id = t.id
             JOIN (SELECT cdg2.id FROM cfe_18.capture_def_group_x_capture_def for SYSTEM_TIME as of TRANSACTION @time cdg2 where cdg2.id>last_id ORDER BY cdg2.id LIMIT page_size)
                 AS page_ids ON cdgxcd.id = page_ids.id
            ORDER BY cdgxcd.id;
end;
//
DELIMITER ;
