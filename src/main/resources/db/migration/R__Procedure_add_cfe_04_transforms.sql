use flow;
DELIMITER //
CREATE OR REPLACE PROCEDURE add_new_cfe_04_transforms(p_cfe_04_id int,
                                                      p_name varchar(255),
                                                      p_write_meta boolean,
                                                      p_write_default boolean,
                                                      p_default_value varchar(255),
                                                      p_destination_key varchar(255),
                                                      p_regex varchar(255),
                                                      p_format varchar(255))
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    -- check if row exists before inserting new one, if so then just return that ID
    IF((select count(id) from flow.cfe_04_transforms t where t.cfe_04_id=p_cfe_04_id
                                            AND t.name=p_name
                                            AND t.write_meta=p_write_meta
                                            AND t.write_default=p_write_default
                                            AND t.default_value=p_default_value
                                            AND t.destination_key=p_destination_key
                                            AND t.regex=p_regex
                                            AND t.format=p_format)>0) THEN

        select t.id from flow.cfe_04_transforms t where t.cfe_04_id=p_cfe_04_id
                                            AND t.name=p_name
                                            AND t.write_meta=p_write_meta
                                            AND t.write_default=p_write_default
                                            AND t.default_value=p_default_value
                                            AND t.destination_key=p_destination_key
                                            AND t.regex=p_regex
                                            AND t.format=p_format;
        ELSE
            insert into flow.cfe_04_transforms(cfe_04_id, name, write_meta, write_default, default_value, destination_key, regex, format)
            values(p_cfe_04_id,p_name,p_write_meta,p_write_default,p_default_value,p_destination_key,p_regex,p_format);

            -- returns ID of new row
            select last_insert_id() as id;
    END IF;


    COMMIT;
END;
//
DELIMITER ;