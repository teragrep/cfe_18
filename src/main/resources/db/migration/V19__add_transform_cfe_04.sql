use flow;

create table flow.cfe_04_transforms (
    id int primary key auto_increment,
    cfe_04_id int not null,
    name varchar(255) not null,
    write_meta boolean not null,
    write_default boolean not null,
    default_value varchar(255) not null,
    destination_key varchar(255) not null,
    regex varchar(255) not null,
    format varchar(255) not null,
    constraint unique(cfe_04_id,name),
    constraint foreign key (cfe_04_id) references flow.cfe_04(id),
    start_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW START INVISIBLE,
    end_trxid BIGINT UNSIGNED GENERATED ALWAYS AS ROW END INVISIBLE,
    PERIOD FOR SYSTEM_TIME(start_trxid, end_trxid)
) WITH SYSTEM VERSIONING;