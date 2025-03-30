use flow;
DELIMITER //
CREATE OR REPLACE PROCEDURE retrieve_all_cfe_04_transforms(tx_id int)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
        if(tx_id) is null then
             set @time = (select max(transaction_id) from mysql.transaction_registry);
        else
             set @time=tx_id;
        end if;

        select t.id                 as id,
               t.cfe_04_id          as cfe_04_id,
               t.name               as name,
               t.write_meta         as write_meta,
               t.write_default      as write_default,
               t.default_value      as default_value,
               t.destination_key    as destination_key,
               t.regex              as regex,
               t.format             as format
        from flow.cfe_04_transforms for system_time as of transaction @time t;

END;
//
DELIMITER ;