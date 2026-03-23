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
USE cfe_18;

CREATE TABLE cfe_04_volumes
(
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    cfe_04_id           INT          NOT NULL,
    volume_name         VARCHAR(255) NOT NULL,
    volume_path         VARCHAR(255) NOT NULL,
    maxVolumeDataSizeMB VARCHAR(255) NOT NULL,
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES cfe_04 (id),
    UNIQUE (cfe_04_id, volume_name),
    UNIQUE (cfe_04_id, volume_path),
    start_trxid         BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid           BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE cfe_04_indexes
(
    cfe_04_id        INT          NOT NULL,
    capture_index_id INT          NOT NULL,
    repFactor        VARCHAR(255) NOT NULL,
    disabled         BOOLEAN      NOT NULL,
    homePath         VARCHAR(255) NOT NULL,
    coldPath         VARCHAR(255) NOT NULL,
    thawedPath       VARCHAR(255) NOT NULL,
    PRIMARY KEY (cfe_04_id, capture_index_id),
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES cfe_04 (id),
    CONSTRAINT FOREIGN KEY (cfe_04_id, capture_index_id) REFERENCES storage_indexes (storage_id, index_id),
    start_trxid      BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid        BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;


CREATE TABLE cfe_04_sourcetypes
(
    cfe_04_id                INT          NOT NULL,
    capture_sourcetype_id    INT          NOT NULL,
    maxdaysago               VARCHAR(255) NOT NULL,
    category                 VARCHAR(255) NOT NULL,
    sourcedescription        VARCHAR(255) NOT NULL,
    truncate                 VARCHAR(255) NOT NULL,
    freeform_indexer_enabled BOOLEAN      NOT NULL,
    freeform_indexer_text    VARCHAR(255) NOT NULL,
    freeform_lb_enabled      BOOLEAN      NOT NULL,
    freeform_lb_text         VARCHAR(255) NOT NULL,
    PRIMARY KEY (cfe_04_id, capture_sourcetype_id),
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES cfe_04 (id),
    CONSTRAINT FOREIGN KEY (cfe_04_id, capture_sourcetype_id) REFERENCES storage_sourcetypes (storage_id, sourcetype_id),
    start_trxid              BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid                BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE cfe_04_sourcetype_x_transform
(
    cfe_04_id            INT NOT NULL,
    cfe_04_sourcetype_id INT NOT NULL,
    cfe_04_transform_id  INT NOT NULL,
    PRIMARY KEY (cfe_04_id, cfe_04_sourcetype_id, cfe_04_transform_id),
    CONSTRAINT FOREIGN KEY (cfe_04_id, cfe_04_transform_id) REFERENCES cfe_04_transforms (cfe_04_id, id),
    CONSTRAINT FOREIGN KEY (cfe_04_id, cfe_04_sourcetype_id) REFERENCES cfe_04_sourcetypes (cfe_04_id, capture_sourcetype_id),
    start_trxid          BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid            BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;


CREATE TABLE cfe_04_override
(
    cfe_04_id     INT           NOT NULL,
    indexes_id    INT           NOT NULL,
    overrideKey   VARCHAR(1024) NOT NULL,
    overrideValue VARCHAR(1024) NOT NULL,
    PRIMARY KEY (cfe_04_id, indexes_id),
    CONSTRAINT FOREIGN KEY (cfe_04_id, indexes_id) REFERENCES cfe_04_indexes (cfe_04_id, capture_index_id),
    start_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid     BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE cfe_04_fields
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    cfe_04_id   INT          NOT NULL,
    fields_name VARCHAR(255) NOT NULL,
    UNIQUE (cfe_04_id, fields_name),
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES cfe_04 (id),
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE cfe_04_global
(
    id                INT PRIMARY KEY AUTO_INCREMENT,
    cfe_04_id         INT          NOT NULL,
    truncate          VARCHAR(255) NOT NULL,
    last_chance_index VARCHAR(255) NOT NULL,
    max_days_ago      VARCHAR(255) NOT NULL,
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES cfe_04 (id),
    start_trxid       BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid         BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE capture_def_x_flow_storages
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    capture_def_id  INT NOT NULL,
    flow_id         INT NOT NULL,
    flow_storage_id INT NOT NULL,
    sourcetype_id   INT NOT NULL,
    index_id        INT NOT NULL,
    CONSTRAINT ´target_id_TO_flow_storages´ FOREIGN KEY (flow_id, flow_storage_id) REFERENCES flow_storages (flow_id, storage_id),
    CONSTRAINT ´capture_def_id_TO_capture_definition´ FOREIGN KEY (flow_id, capture_def_id) REFERENCES capture_definition (flow_id, id),
    CONSTRAINT ´capture_def_WITH_source_type´ FOREIGN KEY (capture_def_id, sourcetype_id) REFERENCES capture_definition (id, captureSourcetype_id),
    CONSTRAINT ´capture_def_WITH_index´ FOREIGN KEY (capture_def_id, index_id) REFERENCES capture_definition (id, captureIndex_id),
    CONSTRAINT ´check_storage_sourcetype´ FOREIGN KEY (flow_storage_id, sourcetype_id) REFERENCES storage_sourcetypes (storage_id, sourcetype_id),
    CONSTRAINT ´check_storage_index´ FOREIGN KEY (flow_storage_id, index_id) REFERENCES storage_indexes (storage_id, index_id),
    UNIQUE (flow_id, flow_storage_id),
    UNIQUE (flow_id, capture_def_id),
    UNIQUE (capture_def_id, flow_id, flow_storage_id),
    start_trxid     BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid       BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

