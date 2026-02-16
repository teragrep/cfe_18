USE cfe_18;


CREATE TABLE cfe_18.storage_indexes
(
    storage_id INT,
    index_id   INT,
    cfe_type   VARCHAR(6) NOT NULL CHECK (cfe_type IN ('cfe_10', 'cfe_23', 'cfe_04')),
    PRIMARY KEY (storage_id, index_id),
    CONSTRAINT FOREIGN KEY (index_id) REFERENCES cfe_18.captureIndex (id),
    CONSTRAINT FOREIGN KEY (storage_id, cfe_type) REFERENCES cfe_18.storages (id, cfe_type)

);

CREATE TABLE cfe_18.storage_sourcetypes
(
    storage_id    INT        NOT NULL,
    sourcetype_id INT        NOT NULL,
    cfe_type      VARCHAR(6) NOT NULL CHECK (cfe_type IN ('cfe_10', 'cfe_23', 'cfe_04')),
    PRIMARY KEY (storage_id, sourcetype_id),
    CONSTRAINT FOREIGN KEY (storage_id, cfe_type) REFERENCES cfe_18.storages (id, cfe_type),
    CONSTRAINT FOREIGN KEY (sourcetype_id) REFERENCES cfe_18.captureSourcetype (id)
);



