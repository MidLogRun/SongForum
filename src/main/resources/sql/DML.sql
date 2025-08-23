SELECT a.artist, a.title, t.name AS track_name FROM album a JOIN track t on t.album_title = a.title;

SELECT a1.id   AS artist1_id,
       a1.name AS artist1_name,
       a2.id   AS artist2_id,
       a2.name AS artist2_name
FROM artist_artist aa
         JOIN artist a1 ON aa.artist_id_1 = a1.id
         JOIN artist a2 ON aa.artist_id_2 = a2.id
ORDER BY a1.name, a2.name;


SELECT a.id , a.name, a.url
FROM artist a
         JOIN artist_tag at ON at.artist_id = a.id
         JOIN tag tg ON tg.id = at.tag_id
WHERE tg.name = 'indie';


SELECT al.id, al.title, al.artist, tg.name
FROM album al
    JOIN album_tag at ON at.album_id = al.id
    JOIN tag tg ON tg.id = at.tag_id
               WHERE tg.name = 'experimental';
