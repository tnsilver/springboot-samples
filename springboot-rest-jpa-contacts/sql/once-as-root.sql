DROP DATABASE IF EXISTS contacts;
CREATE DATABASE IF NOT EXISTS contacts;
USE `contacts`;
Drop User 'user'@'localhost';
Drop User 'user'@'%';
Create User 'user'@'localhost' Identified BY 'password';
Create User 'user'@'%' Identified BY 'password';
Grant All on contacts.* to 'user'@'localhost' with grant option;
Grant All on contacts.* to 'user'@'%' with grant option;
Grant FILE on *.* to 'user'@'localhost';
Grant FILE on *.* to 'user'@'%';

