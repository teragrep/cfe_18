USE flow;

CREATE TABLE flow.cfe_04_meta
(
    id          INT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    CONSTRAINT FOREIGN KEY (id) REFERENCES flow.cfe_04 (id),
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE flow.cfe_04_volumes
(
    id                  INT AUTO_INCREMENT PRIMARY KEY, -- separate primary key allowing multiple fields per cfe_04
    cfe_04_id           INT          NOT NULL,
    name                VARCHAR(255) NOT NULL,
    path                VARCHAR(255) NOT NULL,
    maxVolumeDataSizeMB VARCHAR(255) NOT NULL,
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES flow.cfe_04 (id),
    start_trxid         BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid           BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE flow.cfe_04_indexes
(
    cfe_04_id        INT          NOT NULL,
    capture_index_id INT          NOT NULL,
    repFactor        VARCHAR(255) NOT NULL,
    disabled         BOOLEAN      NOT NULL,
    homePath         VARCHAR(255) NOT NULL,
    coldPath         VARCHAR(255) NOT NULL,
    thawedPath       VARCHAR(255) NOT NULL,
    PRIMARY KEY (cfe_04_id, capture_index_id),
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES flow.cfe_04 (id),
    CONSTRAINT FOREIGN KEY (capture_index_id) REFERENCES cfe_18.captureIndex (id),
    start_trxid      BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid        BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;


CREATE TABLE flow.storage_indexes
(
    storage_id INT,
    index_id   INT,
    cfe_type   VARCHAR(6) NOT NULL CHECK (cfe_type IN ('cfe_10', 'cfe_23', 'cfe_04')),
    PRIMARY KEY (storage_id, index_id),
    CONSTRAINT FOREIGN KEY (index_id) REFERENCES cfe_18.captureIndex (id),
    CONSTRAINT FOREIGN KEY (storage_id, cfe_type) REFERENCES flow.storages (id, cfe_type)

);

-- sourcetypes references sourcetypes id
CREATE TABLE flow.cfe_04_sourcetypes
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
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES flow.cfe_04 (id),
    CONSTRAINT FOREIGN KEY (capture_sourcetype_id) REFERENCES cfe_18.captureSourcetype (id),
    start_trxid              BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid                BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE flow.cfe_04_sourcetype_x_transform
(
    cfe_04_transform_id INT NOT NULL,
    sourcetype_id       INT NOT NULL,
    PRIMARY KEY (cfe_04_transform_id, sourcetype_id),
    CONSTRAINT FOREIGN KEY (cfe_04_transform_id) REFERENCES flow.cfe_04_transforms (id),
    CONSTRAINT FOREIGN KEY (cfe_04_transform_id, sourcetype_id) REFERENCES flow.cfe_04_sourcetypes (cfe_04_id, capture_sourcetype_id),
    start_trxid         BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid           BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE flow.storage_sourcetypes
(
    storage_id    INT        NOT NULL,
    sourcetype_id INT        NOT NULL,
    cfe_type      VARCHAR(6) NOT NULL CHECK (cfe_type IN ('cfe_10', 'cfe_23', 'cfe_04')),
    PRIMARY KEY (storage_id, sourcetype_id),
    CONSTRAINT FOREIGN KEY (storage_id, cfe_type) REFERENCES flow.storages (id, cfe_type),
    CONSTRAINT FOREIGN KEY (sourcetype_id) REFERENCES cfe_18.captureSourcetype (id)
);

CREATE TABLE flow.cfe_04_override
(
    cfe_04_id     INT           NOT NULL,
    indexes_id    INT           NOT NULL,
    overrideKey   VARCHAR(1024) NOT NULL,
    overrideValue VARCHAR(1024) NOT NULL,
    PRIMARY KEY (cfe_04_id, indexes_id),
    CONSTRAINT FOREIGN KEY (cfe_04_id, indexes_id) REFERENCES flow.cfe_04_indexes (cfe_04_id, capture_index_id),
    start_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid     BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE flow.cfe_04_fields
(
    id          INT PRIMARY KEY AUTO_INCREMENT, -- separate primary key allowing multiple fields per cfe_04
    cfe_04_id   INT          NOT NULL,
    name        VARCHAR(255) NOT NULL,
    UNIQUE (name, cfe_04_id),
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES flow.cfe_04 (id),
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE flow.cfe_04_global
(
    cfe_04_id         INT PRIMARY KEY, -- not auto-increment because it has to match cfe_04 ID
    last_chance_index VARCHAR(255) NOT NULL,
    max_days_ago      VARCHAR(255) NOT NULL,
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES flow.cfe_04 (id),
    start_trxid       BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid         BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;



CREATE TABLE capture_def_x_flow_targets
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    capture_def_id INT NOT NULL,
    flow_id        INT NOT NULL,
    flow_target_id INT NOT NULL,
    sourcetype_id  INT NOT NULL,
    index_id       INT NOT NULL,
    CONSTRAINT ´target_id_TO_flow_targets´ FOREIGN KEY (flow_id, flow_target_id) REFERENCES flow.flow_targets (flow_id, storage_id),
    CONSTRAINT ´capture_def_id_TO_capture_definition´ FOREIGN KEY (flow_id, capture_def_id) REFERENCES cfe_18.capture_definition (flow_id, id) ON DELETE CASCADE,
    CONSTRAINT ´capture_def_WITH_source_type´ FOREIGN KEY (capture_def_id, sourcetype_id) REFERENCES cfe_18.capture_definition (id, captureSourcetype_id),
    CONSTRAINT ´capture_def_WITH_index´ FOREIGN KEY (capture_def_id, index_id) REFERENCES cfe_18.capture_definition (id, captureIndex_id),
    CONSTRAINT ´check_storage_sourcetype´ FOREIGN KEY (flow_target_id, sourcetype_id) REFERENCES flow.storage_sourcetypes (storage_id, sourcetype_id),
    CONSTRAINT ´check_storage_index´ FOREIGN KEY (flow_target_id, index_id) REFERENCES flow.storage_indexes (storage_id, index_id),
    UNIQUE (capture_def_id, flow_id, flow_target_id),
    start_trxid    BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid      BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

