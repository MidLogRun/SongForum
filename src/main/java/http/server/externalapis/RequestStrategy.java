package http.server.externalapis;

import http.server.externalapis.spotify.ApiGetFailed;

import java.net.http.HttpRequest;

public interface RequestStrategy {
    HttpRequest get(String url) throws ApiGetFailed;
}
