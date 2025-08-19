CREATE TABLE album
(
    id           SERIAL PRIMARY KEY,
    type         VARCHAR(15)  NOT NULL,
    total_tracks INT          NOT NULL,
    url          VARCHAR(255) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    release_date DATE,
    label        VARCHAR(255),
    popularity   INT
);

CREATE TABLE artist
(
    id   SERIAL PRIMARY KEY,
    url  VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    uri  VARCHAR(255) NOT NULL
);

CREATE TABLE track
(
    id           SERIAL PRIMARY KEY,
    duration_s   INT          NOT NULL,
    name         VARCHAR(255) NOT NULL,
    preview_url  VARCHAR(255),
    track_number INT          NOT NULL
);

CREATE TABLE track_album
(
    track_id INT,
    album_id INT,
    PRIMARY KEY (track_id, album_id),
    FOREIGN KEY (track_id) REFERENCES track (id) ON DELETE CASCADE,
    FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE
);


CREATE TABLE artist_album
(
    artist_id INT,
    album_id  INT,
    PRIMARY KEY (artist_id, album_id),
    FOREIGN KEY (artist_id) REFERENCES artist (id) ON DELETE CASCADE,
    FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE
);

CREATE TABLE genre
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE album_genre
(
    album_id INT,
    genre_id INT,
    PRIMARY KEY (album_id, genre_id),
    FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre (id) ON DELETE CASCADE
);



DROP TABLE IF EXISTS album, artist, track, track_album, artist_album, genre, album_genre;