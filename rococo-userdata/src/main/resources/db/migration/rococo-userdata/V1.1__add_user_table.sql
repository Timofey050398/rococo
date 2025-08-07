CREATE TABLE IF NOT EXISTS `user` (
    id       binary(16)    unique not null default (UUID_TO_BIN(UUID(), true)),
    username varchar(50) UNIQUE NOT NULL,
    firstname varchar(255),
    lastname varchar(255),
    photo    longblob,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);