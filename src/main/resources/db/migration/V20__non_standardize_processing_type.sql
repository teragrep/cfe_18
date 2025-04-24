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
alter table cfe_18.capture_meta_file drop constraint metaFileToMetaType;
drop table cfe_18.processing_type;
drop table cfe_18.templates;
drop table cfe_18.ruleset;
drop table cfe_18.newline;
drop table cfe_18.regex;
drop table cfe_18.inputtype;


create table cfe_18.processing_type
(
    id          int auto_increment primary key,
    name        varchar(48),
    inputtype   enum('regex','newline') not null,
    inputvalue  varchar(255) not null,
    ruleset     varchar(1000) not null,
    template    varchar(255) not null,
    index (name),
    unique (inputtype, inputvalue, ruleset, template, name),
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

alter table cfe_18.capture_meta_file add constraint metaFileToMetaType foreign key (processing_type_id) references processing_type (id);

