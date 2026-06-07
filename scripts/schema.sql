CREATE TABLE IF NOT EXISTS users (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_name      VARCHAR(64) NOT NULL UNIQUE,
    password_hash  VARCHAR(128) NOT NULL,
    status         user_status NOT NULL DEFAULT 'OFFLINE'
);

CREATE SEQUENCE IF NOT EXISTS human_being_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS human_beings (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                VARCHAR NOT NULL,
    coord_x             INTEGER NOT NULL,
    coord_y             BIGINT NOT NULL,
    creation_date       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    real_hero           BOOLEAN NOT NULL,
    has_toothpick       BOOLEAN NOT NULL,
    impact_speed        DOUBLE PRECISION NOT NULL,
    soundtrack_name     VARCHAR NOT NULL,
    minutes_of_waiting  INTEGER NOT NULL,
    weapon_type         VARCHAR,
    car_cool            BOOLEAN,
    user_id             BIGINT NOT NULL,

    CONSTRAINT fk_human_beings_owner
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);