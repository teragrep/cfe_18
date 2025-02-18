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
use cfe_07;

create table cfe_07_promise
(
    id int primary key,
    constraint ´cfe_07_to_promise´ foreign key (id) references cfe_00.promises (id),
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

create table config
(
    id      int auto_increment primary key,
    dir     varchar(25) not null,
    workdir varchar(25) not null,
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

create table instances
(
    id        int auto_increment primary key,
    index (flow_id),
    flow_id   int not null,
    config_id int not null,
    constraint ´instanceToFlow´ foreign key (flow_id) references flow.flows (id),
    constraint ´instanceToConfig´ foreign key (config_id) references config (id),
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;


create table instance_targets
(
    id                     int auto_increment primary key,
    flow_id                int          not null,
    flow_target_id         int          not null,
    instance_id            int          not null,
    enabled                boolean      not null,
    target                 varchar(100) not null,
    target_port            int          not null,
    resumeInterval         int          not null,
    resumeRetryCount       int          not null,
    rebindInterval         int          not null,
    resumerIntervalMax     int          not null,
    userResumerIntervalMax boolean      not null,
    constraint ´TargetToFlowTargets´ foreign key (flow_id, flow_target_id) references flow.flow_targets (flow_id, id),
    constraint ´TargetToInstanceTargets´ foreign key (flow_id, instance_id) references instances (flow_id, id),
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;


create table cfe_07_promise_x_instance
(
    id                int primary key,
    cfe_07_promise_id int not null,
    instance_id       int not null,
    constraint ´cfe_07_promiseToInstance´ foreign key (cfe_07_promise_id) references cfe_07_promise (id),
    constraint ´instance_idToInstance´ foreign key (instance_id) references instances (id),
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

