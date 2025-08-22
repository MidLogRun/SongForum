CREATE TABLE album
(
    id            SERIAL PRIMARY KEY,
    artist        VARCHAR(50)        NOT NULL,
    title         VARCHAR(50) UNIQUE NOT NULL,
    num_tags      INT                NOT NULL,
    num_tracks    INT                NOT NULL,
    url           VARCHAR(100)       NOT NULL,
    summary       TEXT               NOT NULL,
    num_listeners INT                NOT NULL
);

CREATE TABLE artist
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(50) UNIQUE NOT NULL,
    url     VARCHAR(150)       NOT NULL,
    summary text               NOT NULL
);


CREATE TABLE artist_artist
(
    artist_id_1 INT NOT NULL,
    artist_id_2 INT NOT NULL,
    PRIMARY KEY (artist_id_1, artist_id_2),
    FOREIGN KEY (artist_id_1) REFERENCES artist (id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id_2) REFERENCES artist (id) ON DELETE CASCADE,
    CHECK (artist_id_1 <> artist_id_2)
);

CREATE TABLE track
(
    id          SERIAL PRIMARY KEY,
    duration_s  INT          NOT NULL,
    name        VARCHAR(255) NOT NULL,
    preview_url VARCHAR(255),
    album_title VARCHAR(50)  NOT NULL references album (title) ON DELETE CASCADE
);


CREATE TABLE tag
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE album_tag
(
    album_id INT,
    tag_id   INT,
    PRIMARY KEY (album_id, tag_id),
    FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
);

CREATE TABLE artist_tag
(
    artist_id INT,
    tag_id    INT,
    FOREIGN KEY (artist_id) REFERENCES artist (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
);



DROP TABLE IF EXISTS album, artist, artist_tag, artist_artist, track, tag, album_tag;
