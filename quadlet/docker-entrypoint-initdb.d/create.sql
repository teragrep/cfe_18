create database if not exists cfe_18 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
create database if not exists cfe_03 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
create database if not exists cfe_01 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
create database if not exists cfe_07 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
create database if not exists cfe_00 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
create database if not exists flow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
create database if not exists location CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

grant all privileges on cfe_18.* to root@'%' identified by 'password';
grant all privileges on cfe_03.* to root@'%' identified by 'password';
grant all privileges on cfe_01.* to root@'%' identified by 'password';
grant all privileges on cfe_07.* to root@'%' identified by 'password';
grant all privileges on cfe_00.* to root@'%' identified by 'password';
grant all privileges on flow.* to root@'%' identified by 'password';
grant all privileges on location.* to root@'%' identified by 'password';
flush privileges;