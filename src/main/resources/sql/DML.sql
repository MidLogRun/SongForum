SELECT a.artist, a.title, t.name AS track_name FROM album a JOIN track t on t.album_title = a.title;

