DROP DATABASE IF EXISTS invest;
CREATE DATABASE invest;

USE invest;

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS users (
	id MEDIUMINT NOT NULL AUTO_INCREMENT, 
	username varchar(50) NOT NULL UNIQUE,
	password_hash varchar(2048) NOT NULL,
	secret varchar(2048) NOT NULL,
	primary key (id)
);
