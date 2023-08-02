CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         VARCHAR(200)                            NOT NULL,
    description  VARCHAR(250)                            NOT NULL,
    requester_id BIGINT                                  NOT NULL,
    FOREIGN KEY (requester_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(200)                            NOT NULL,
    description VARCHAR(250)                            NOT NULL,
    available   BOOLEAN,
    owner_id    BIGINT                                  NOT NULL,
    request_id  BIGINT,
    FOREIGN KEY (owner_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (id),
    FOREIGN KEY (request_id)
        REFERENCES requests (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date     TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_date       TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    item_id        BIGINT                                  NOT NULL,
    booker_id      BIGINT                                  NOT NULL,
    booking_status VARCHAR,
    FOREIGN KEY (item_id)
        REFERENCES items (id) ON DELETE CASCADE,
    FOREIGN KEY (booker_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      VARCHAR(500)                            NOT NULL,
    item_id   BIGINT                                  NOT NULL,
    author_id BIGINT                                  NOT NULL,
    created   TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (item_id)
        REFERENCES items (id) ON DELETE CASCADE,
    FOREIGN KEY (author_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);