CREATE TABLE IF NOT EXISTS users (
    login          VARCHAR(64) PRIMARY KEY,
    password_hash  VARCHAR(128) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS human_being_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS human_beings (
    id                  BIGINT PRIMARY KEY DEFAULT nextval('human_being_id_seq'),

    name                VARCHAR NOT NULL,

    coord_x             INTEGER NOT NULL,
    coord_y             BIGINT NOT NULL,

    creation_date       TIMESTAMP NOT NULL,

    real_hero           BOOLEAN NOT NULL,
    has_toothpick       BOOLEAN NOT NULL,

    impact_speed        DOUBLE PRECISION NOT NULL,
    soundtrack_name     VARCHAR NOT NULL,
    minutes_of_waiting  INTEGER NOT NULL,

    weapon_type         VARCHAR,
    car_cool            BOOLEAN,

    owner_login         VARCHAR(64) NOT NULL,

    CONSTRAINT fk_human_beings_owner
        FOREIGN KEY (owner_login)
        REFERENCES users(login)
        ON DELETE CASCADE
);