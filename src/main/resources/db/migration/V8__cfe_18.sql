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
USE `cfe_18`;

CREATE TABLE capture_type
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    capture_type VARCHAR(64) NOT NULL CHECK (capture_type IN
                                             ('aws', 'manual', 'cfe', 'windows', 'hec',
                                              'azure', 'relp')),
    UNIQUE KEY (id, capture_type),
    start_trxid  BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid    BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE application
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    app         VARCHAR(48) NOT NULL UNIQUE,
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE retentionTime
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    retention   VARCHAR(255) NOT NULL UNIQUE,
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE tags
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    tag         VARCHAR(48) NOT NULL UNIQUE,
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE captureIndex
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    captureIndex VARCHAR(48) NOT NULL UNIQUE,
    start_trxid  BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid    BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE captureSourcetype
(
    id                INT AUTO_INCREMENT PRIMARY KEY,
    captureSourceType VARCHAR(255) NOT NULL UNIQUE,
    start_trxid       BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid         BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE category
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    category    VARCHAR(48) NOT NULL UNIQUE,
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;


CREATE TABLE capture_definition
(
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    tag_id               INT         NOT NULL,
    application_id       INT         NOT NULL,
    captureIndex_id      INT         NOT NULL,
    retentionTime_id     INT         NOT NULL,
    captureSourcetype_id INT         NOT NULL,
    category_id          INT         NOT NULL,
    capture_type         VARCHAR(64) NOT NULL CHECK (capture_type IN
                                                     ('aws', 'manual', 'cfe', 'windows', 'hec', 'azure', 'relp')),
    capture_type_id      INT         NOT NULL,
    L7_id                INT         NOT NULL,
    flow_id              INT         NOT NULL,
    CONSTRAINT ´application´ FOREIGN KEY (application_id) REFERENCES application (id),
    CONSTRAINT ´captureIndexTocaptureIndex´ FOREIGN KEY (captureIndex_id) REFERENCES captureIndex (id),
    CONSTRAINT ´retentionTime´ FOREIGN KEY (retentionTime_id) REFERENCES retentionTime (id),
    CONSTRAINT ´captureSourceTypeTocaptureSourcetype´ FOREIGN KEY (captureSourcetype_id) REFERENCES captureSourcetype (id),
    CONSTRAINT ´category_id_TO_category´ FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT ´captureToProcessing´ FOREIGN KEY (capture_type_id) REFERENCES capture_type (id) ON DELETE CASCADE,
    CONSTRAINT ´tag_id_TO_tags´ FOREIGN KEY (tag_id) REFERENCES tags (id),
    CONSTRAINT ´flow_id_TO_flows´ FOREIGN KEY (flow_id) REFERENCES flow.flows (id),
    CONSTRAINT ´flow_L7_id_TO_sink´ FOREIGN KEY (flow_id, L7_id) REFERENCES flow.capture_sink (flow_id, L7_id),
    UNIQUE KEY (id, capture_type),
    UNIQUE KEY (id, tag_id),
    UNIQUE KEY (id, flow_id),
    UNIQUE KEY (id, captureSourcetype_id),
    UNIQUE KEY (id, captureIndex_id),
    UNIQUE KEY (id, capture_type_id),
    INDEX (flow_id),
    start_trxid          BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid            BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;



CREATE TABLE cfe_18.file_processing_type
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(48),
    inputtype   ENUM ('regex','newline') NOT NULL,
    inputvalue  VARCHAR(255)             NOT NULL,
    ruleset     VARCHAR(1000)            NOT NULL,
    template    VARCHAR(255)             NOT NULL,
    INDEX (name),
    UNIQUE (inputtype, inputvalue, ruleset, template, name),
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE capture_meta_file
(
    id                 INT PRIMARY KEY,
    capturePath        VARCHAR(255) NOT NULL,
    tagPath            VARCHAR(255) DEFAULT NULL,
    processing_type_id INT          NOT NULL,
    capture_type       VARCHAR(64)  NOT NULL CHECK (capture_type = 'cfe'),
    CONSTRAINT captureTypeCfe FOREIGN KEY (id, capture_type) REFERENCES capture_type (id, capture_type) ON DELETE CASCADE,
    CONSTRAINT metaFileToMetaType FOREIGN KEY (processing_type_id) REFERENCES file_processing_type (id),
    start_trxid        BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid          BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE capture_meta_aws
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    kinesis_name  VARCHAR(255) NOT NULL,
    capture_group VARCHAR(255) NOT NULL,
    capture_type  VARCHAR(64)  NOT NULL CHECK (capture_type = 'aws'),
    CONSTRAINT captureTypeAws FOREIGN KEY (id, capture_type) REFERENCES capture_type (id, capture_type),
    start_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid     BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE capture_meta_metrics
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    capture_type VARCHAR(64) NOT NULL CHECK (capture_type = 'metrics'),
    CONSTRAINT captureTypeMetrics FOREIGN KEY (id, capture_type) REFERENCES capture_type (id, capture_type),
    start_trxid  BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid    BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE capture_def_group
(
    id                     INT AUTO_INCREMENT PRIMARY KEY,
    capture_def_group_name VARCHAR(64) UNIQUE NOT NULL,
    capture_type           VARCHAR(64)        NOT NULL CHECK (capture_type IN
                                                              ('aws', 'manual', 'cfe', 'windows', 'hec', 'azure',
                                                               'relp')),
    UNIQUE (id, capture_type),
    start_trxid            BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid              BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;


CREATE TABLE capture_def_group_x_capture_def
(
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    capture_def_id       INT         NOT NULL,
    capture_def_group_id INT         NOT NULL,
    tag_id               INT         NOT NULL,
    capture_type         VARCHAR(64) NOT NULL CHECK (capture_type IN
                                                     ('aws', 'manual', 'cfe', 'windows', 'hec',
                                                      'azure', 'relp')),
    CONSTRAINT captureToCaptureGroupType FOREIGN KEY (capture_def_id, capture_type) REFERENCES cfe_18.capture_definition (id, capture_type) ON DELETE CASCADE,
    CONSTRAINT captureTypeToCaptureGroup FOREIGN KEY (capture_def_group_id, capture_type) REFERENCES cfe_18.capture_def_group (id, capture_type) ON DELETE CASCADE,
    UNIQUE KEY (capture_def_group_id, capture_def_id),
    UNIQUE KEY (capture_def_group_id, tag_id),
    INDEX (capture_type, capture_def_group_id),
    start_trxid          BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid            BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;


CREATE TABLE host_groups_x_capture_def_group
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    host_group_id    INT NOT NULL,
    capture_group_id INT NOT NULL,
    CONSTRAINT host_group_id_TO_host_group FOREIGN KEY (host_group_id) REFERENCES location.host_group (id),
    CONSTRAINT ´capture_id_to_junction´ FOREIGN KEY (capture_group_id) REFERENCES capture_def_group (id),
    UNIQUE KEY (host_group_id, capture_group_id),
    start_trxid      BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid        BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

