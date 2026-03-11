create database if not exists cfe_18 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
grant all privileges on cfe_18.* to root@'%' identified by 'password';
flush privileges;