USE flow;

CREATE TABLE flow.cfe_04_meta
(
    id          INT          NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    CONSTRAINT FOREIGN KEY (id) REFERENCES flow.cfe_04 (id),
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE flow.cfe_04_volumes
(
    id                  INT AUTO_INCREMENT PRIMARY KEY,
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
    id          INT AUTO_INCREMENT PRIMARY KEY,
    cfe_04_id   INT          NOT NULL,
    name        VARCHAR(255) NOT NULL,
    repFactor   VARCHAR(255) NOT NULL,
    disabled    BOOLEAN      NOT NULL,
    homePath    VARCHAR(255) NOT NULL,
    coldPath    VARCHAR(255) NOT NULL,
    thawedPath  VARCHAR(255) NOT NULL,
    UNIQUE (name, cfe_04_id),
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES flow.cfe_04 (id),
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE flow.cfe_04_override
(
    indexes_id    INT           NOT NULL,
    overrideKey   VARCHAR(1024) NOT NULL,
    overrideValue VARCHAR(1024) NOT NULL,
    CONSTRAINT FOREIGN KEY (indexes_id) REFERENCES flow.cfe_04_indexes (id),
    start_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid     BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE flow.cfe_04_sourcetypes
(
    id                       INT AUTO_INCREMENT PRIMARY KEY,
    cfe_04_id                INT          NOT NULL,
    name                     VARCHAR(255) NOT NULL,
    maxdaysago               VARCHAR(255) NOT NULL,
    category                 VARCHAR(255) NOT NULL,
    sourcedescription        VARCHAR(255) NOT NULL,
    truncate                 VARCHAR(255) NOT NULL,
    freeform_indexer_enabled BOOLEAN      NOT NULL,
    freeform_indexer_text    VARCHAR(255) NOT NULL,
    freeform_lb_enabled      BOOLEAN      NOT NULL,
    freeform_lb_text         VARCHAR(255) NOT NULL,
    UNIQUE KEY (id, cfe_04_id),
    CONSTRAINT UNIQUE (name, cfe_04_id),
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES flow.cfe_04 (id),
    start_trxid              BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid                BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE flow.cfe_04_sourcetype_x_transform
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    transform_id  INT NOT NULL,
    sourcetype_id INT NOT NULL,
    cfe_04_id     INT NOT NULL,
    UNIQUE KEY (transform_id, sourcetype_id),
    CONSTRAINT FOREIGN KEY (transform_id, cfe_04_id) REFERENCES flow.cfe_04_transforms (id, cfe_04_id),
    CONSTRAINT FOREIGN KEY (sourcetype_id, cfe_04_id) REFERENCES flow.cfe_04_sourcetypes (id, cfe_04_id),
    start_trxid   BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid     BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;

CREATE TABLE flow.cfe_04_fields
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
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
    cfe_04_id         INT          NOT NULL UNIQUE,
    last_chance_index VARCHAR(255) NOT NULL,
    max_days_ago      VARCHAR(255) NOT NULL,
    CONSTRAINT FOREIGN KEY (cfe_04_id) REFERENCES flow.cfe_04 (id),
    start_trxid       BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid         BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;
