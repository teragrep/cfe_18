use flow;
DELIMITER //
CREATE OR REPLACE PROCEDURE add_new_cfe_04_transforms(cfe_04_id int,
                                                     name varchar(255),
                                                     write_meta boolean,
                                                     write_default boolean,
                                                     default_value varchar(255),
                                                     destination_key varchar(255),
                                                     regex varchar(255),
                                                     format varchar(255))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    -- check if row exists before inserting new one, if so then just return that ID
    IF(select id from cfe_04_transforms t where t.cfe_04_id=cfe_04_id
                                            AND t.name=name
                                            AND t.write_meta=write_meta
                                            AND t.write_default=write_default
                                            AND t.default_value=default_value
                                            AND t.destination_key=destination_key
                                            AND t.regex=regex
                                            AND t.format=format) IS NOT NULL THEN

        select id from cfe_04_transforms t where t.cfe_04_id=cfe_04_id
                                            AND t.name=name
                                            AND t.write_meta=write_meta
                                            AND t.write_default=write_default
                                            AND t.default_value=default_value
                                            AND t.destination_key=destination_key
                                            AND t.regex=regex
                                            AND t.format=format;

    END IF;

    insert into cfe_04_transforms(cfe_04_id, name, write_meta, write_default, default_value, destination_key, regex, format)
        values(cfe_04_id,name,write_meta,write_default,default_value,destination_key,regex,format);

    -- returns ID of new row
    select last_insert_id() as id;

    COMMIT;
END;
//
DELIMITER ;