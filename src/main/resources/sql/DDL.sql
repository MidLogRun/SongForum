CREATE TABLE album
(
    id           VARCHAR(255) PRIMARY KEY,
    type         VARCHAR(15)  NOT NULL,
    total_tracks INT          NOT NULL,
    url          VARCHAR(255) NOT NULL,
    href         VARCHAR(255) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    release_date DATE,
    label        VARCHAR(255),
    popularity   INT
);

CREATE TABLE artist
(
    id   VARCHAR(255) PRIMARY KEY,
    href VARCHAR(255),
    url  VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    uri  VARCHAR(255) NOT NULL
);

CREATE TABLE track
(
    id           VARCHAR(255) PRIMARY KEY,
    duration_ms  INT          NOT NULL,
    explicit     BOOL,
    name         VARCHAR(255) NOT NULL,
    preview_url  VARCHAR(255),
    track_number INT          NOT NULL
);

CREATE TABLE artist_album
(
    artist_id VARCHAR(255),
    album_id  VARCHAR(255),
    PRIMARY KEY (artist_id, album_id),
    FOREIGN KEY (artist_id) REFERENCES artist (id) ON DELETE CASCADE,
    FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE
)

