CREATE DATABASE IF NOT EXISTS xilab;
USE xilab;

CREATE TABLE xilab.device_wrapper(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    uuid      VARCHAR(255) NOT NULL,
    name      VARCHAR(255) NOT NULL,
    latitude  DOUBLE       NOT NULL,
    longitude DOUBLE       NOT NULL,
    battery   INT          NOT NULL,
    current   INT          NOT NULL,
    max       INT          NOT NULL,
    min       INT          NOT NULL
);

