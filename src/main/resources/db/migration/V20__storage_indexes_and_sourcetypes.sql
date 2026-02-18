USE cfe_18;


CREATE TABLE cfe_18.storage_indexes
(
    storage_id INT,
    index_id   INT,
    PRIMARY KEY (storage_id, index_id),
    CONSTRAINT FOREIGN KEY (index_id) REFERENCES cfe_18.captureIndex (id),
    CONSTRAINT FOREIGN KEY (storage_id) REFERENCES cfe_18.storages (id)

);

CREATE TABLE cfe_18.storage_sourcetypes
(
    storage_id    INT        NOT NULL,
    sourcetype_id INT        NOT NULL,
    PRIMARY KEY (storage_id, sourcetype_id),
    CONSTRAINT FOREIGN KEY (storage_id) REFERENCES cfe_18.storages (id),
    CONSTRAINT FOREIGN KEY (sourcetype_id) REFERENCES cfe_18.captureSourcetype (id)
);



