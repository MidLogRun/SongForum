import com.fasterxml.jackson.core.JsonProcessingException;
import database.ArtistRepository;
import database.Connector;
import database.NotDeletedException;
import database.NotSavedException;
import http.server.externalapis.LastFmWrapper;
import http.server.externalapis.Requester;
import http.server.externalapis.spotify.ApiGetFailed;
import http.server.externalapis.spotify.LastFmRequestStrategy;
import http.server.json_readers.JsonResponseReaderException;
import http.server.object_files.ArtistId;
import http.server.object_files.FmArtist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArtistRepositoryTest {
    ArtistRepository repo;
    Requester requester;
    LastFmWrapper apiWrapper;


    @BeforeEach
    public void setUp() throws SQLException {
        repo = new ArtistRepository(Connector.getConnection());
        requester = new Requester(new LastFmRequestStrategy());
        apiWrapper = new LastFmWrapper(requester);
    }

    public ArtistId identify(FmArtist artist) {
        return new ArtistId(artist.name());
    }

    @Test
    public void insertOneArtist() throws SQLException, JsonResponseReaderException, ApiGetFailed, JsonProcessingException {
        String name = "Roger Miller";
        FmArtist artist = apiWrapper.getArtist(name);
        try {
            repo.insert(artist);
            assertTrue(repo.exists(identify(artist)));
            artist = repo.getArtistByName(name);
            assertTrue(artist.similarArtists().isEmpty()); //since we are inserting one artist into an empty db, this will be 0
            assertFalse(artist.tags().isEmpty());
            repo.delete(artist);
            assertFalse(repo.exists(identify(artist)));
        } catch (NotSavedException | NotDeletedException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void permanentInsertManyArtists() throws SQLException, JsonResponseReaderException, ApiGetFailed, JsonProcessingException {
        List<String> artistNames = List.of("The Killers", "The Strokes", "AC/DC", "Bruce Springsteen");
        List<FmArtist> artists = apiWrapper.getArtists(artistNames);
        try {
            for (FmArtist artist : artists) {
                repo.insert(artist);
            }
        } catch (NotSavedException e) {
            System.err.println(e.getMessage());

        } catch (SQLException e) {
            System.err.println("Sql exception: " + e.getMessage());
        }
    }

    @Test
    public void getAllArtistsWithTag() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException, SQLException {
        String tagName = "indie";
        List<FmArtist> artists = repo.getArtistsByTag(tagName);
        assertFalse(artists.isEmpty());
        System.out.println(artists);
    }

    @Test
    public void testInsertMultipleArtists() throws SQLException, JsonResponseReaderException, ApiGetFailed, JsonProcessingException {
        String n1 = "Roger Miller";
        String n2 = "Stonewall Jackson";
        String n3 = "Faron Young";

        FmArtist a1 = apiWrapper.getArtist(n1);
        FmArtist a2 = apiWrapper.getArtist(n2);
        FmArtist a3 = apiWrapper.getArtist(n3);
        try {
            repo.insert(a1);
            repo.insert(a2);
            repo.insert(a3);
            assertTrue(repo.exists(identify(a1)));
            assertTrue(repo.exists(identify(a2)));
            assertTrue(repo.exists(identify(a3)));
            a1 = repo.getArtistByName(n1);
            a2 = repo.getArtistByName(n2);
            a3 = repo.getArtistByName(n3);
            assertTrue(a1.similarArtists().isEmpty()); //we inserted a1 before any of the others
            assertFalse(a2.similarArtists().isEmpty());
            assertFalse(a3.similarArtists().isEmpty());
            repo.delete(a1);
            repo.delete(a2);
            repo.delete(a3);
            assertFalse(repo.exists(identify(a1)));
            assertFalse(repo.exists(identify(a2)));
            assertFalse(repo.exists(identify(a3)));
        } catch (NotSavedException | NotDeletedException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void insertMultipleThenCheckAdjacentArtists() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException {
        String n1 = "Bon Iver";
        String n2 = "Sufjan Stevens";

        FmArtist bon = apiWrapper.getArtist(n1);
        FmArtist suf = apiWrapper.getArtist(n2);
        try {
            repo.insert(bon);
            repo.insert(suf);

            repo.updateAdjacencyList(bon);

            bon = repo.getArtistByName(n1);
            suf = repo.getArtistByName(n2);

            System.out.println("Bon Iver adjacency: " + bon.similarArtists());
            System.out.println("Sufjan Stevens adjacency: " + suf.similarArtists());
            assertFalse(bon.similarArtists().isEmpty());
            assertFalse(suf.similarArtists().isEmpty());

            repo.delete(bon);
            repo.delete(suf);
        } catch (NotSavedException e) {
            System.err.println("Not saved exception" + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Sql exception " + e.getMessage());
        } catch (NotDeletedException e) {
            System.err.println("Not deleted exception" + e.getMessage());
        }
    }

    @Test
    public void insertOneThenCheckTags() throws JsonResponseReaderException, ApiGetFailed, JsonProcessingException {
        String name = "Roger Miller";
        FmArtist artist = apiWrapper.getArtist(name);
        assertFalse(artist.tags().isEmpty());

        try {
            repo.insert(artist);
            artist = repo.getArtistByName(name);
            assertTrue(repo.exists(identify(artist)));
            assertFalse(artist.tags().isEmpty());
            repo.delete(artist);
            assertFalse(repo.exists(identify(artist)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NotDeletedException e) {
            throw new RuntimeException(e);
        }
    }


}
