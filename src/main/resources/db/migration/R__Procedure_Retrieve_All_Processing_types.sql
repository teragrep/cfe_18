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
CREATE OR REPLACE PROCEDURE retrieve_all_processing_types(tx_id int)
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
    select pt.type_name                 as name,
           i.inputtype                  as inputtype,
           coalesce(r.regex, n.newline) as inputvalue,
           r2.rule                      as ruleset,
           t.template                   as template
    from cfe_18.processing_type for system_time as of transaction @time pt
             inner join inputtype for system_time as of transaction @time i on pt.inputtype_id = i.id
             LEFT JOIN regex for system_time as of transaction @time r ON i.id = r.id AND i.inputtype = r.inputtype
             LEFT JOIN newline for system_time as of transaction @time n ON i.id = n.id AND i.inputtype = n.inputtype
             inner join ruleset for system_time as of transaction @time r2 on pt.ruleset_id = r2.id
             inner join templates for system_time as of transaction @time t on pt.template_id = t.id;
end;
//
DELIMITER ;

