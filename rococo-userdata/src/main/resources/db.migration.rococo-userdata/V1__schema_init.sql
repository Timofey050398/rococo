CREATE TABLE IF NOT EXISTS `user` (
    id       binary(16)    unique not null default (UUID_TO_BIN(UUID(), true)),
    username varchar(50) UNIQUE NOT NULL,
    firstname varchar(255),
    lastname varchar(255),
    photo    longblob,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

create table if not exists `museum`
(
    id          binary(16)    unique not null default (UUID_TO_BIN(UUID(), true)),
    title       varchar(255)  unique not null,
    description varchar(1000),
    city        varchar(255),
    photo       longblob,
    country_id  binary(16)     not null,
    primary key (id),
    constraint fk_country_id foreign key (country_id) references `country` (id)
    );

create table if not exists `artist`
(
    id        binary(16)    unique not null default (UUID_TO_BIN(UUID(), true)),
    name      varchar(255)  unique not null,
    biography varchar(2000) not null,
    photo     longblob,
    primary key (id)
    );

create table if not exists `painting`
(
    id          binary(16)      unique not null default (UUID_TO_BIN(UUID(), true)),
    title       varchar(255)    not null,
    description varchar(1000),
    artist_id   binary(16)     not null,
    museum_id   binary(16),
    content     longblob,
    primary key (id),
    constraint fk_artist_id foreign key (artist_id) references `artist` (id),
    constraint fk_museum_id foreign key (museum_id) references `museum` (id)
    );