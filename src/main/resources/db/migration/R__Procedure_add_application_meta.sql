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
CREATE OR REPLACE PROCEDURE add_application_meta(application varchar(48),application_meta_key varchar(1024), application_meta_value varchar(1024))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    -- check if application exists for metadata
    if(select id from cfe_18.application where app=application) is null then
        -- standardized JSON error response
        SELECT JSON_OBJECT('id', null, 'message', 'Application does not exist with given ID') into @app;
        signal sqlstate '42000' set message_text = @app;
    end if;

    -- check if similar row exists already to avoid duplication
    if(select a.id
        from cfe_18.application a
                    inner join application_meta am on a.id = am.application_id
                    inner join application_meta_key amk on am.meta_key_id = amk.meta_key_id
                     where a.app=application
                     and am.meta_value=application_meta_value
                     and amk.meta_key_name=application_meta_key) is null then
    -- insert new record
    insert into cfe_18.application_meta_key(meta_key_name) values (application_meta_key);
        select last_insert_id() into @id;
        insert into cfe_18.application_meta(application_id,meta_key_id,meta_value) values(
            (select id from cfe_18.application where app=application)
            ,@id
            ,application_meta_value);
    -- return given application name as signal
    select application as application;
    else
        select a.app as application from cfe_18.application a where a.app=application;
    end if;
    COMMIT;
END;
//
DELIMITER ;