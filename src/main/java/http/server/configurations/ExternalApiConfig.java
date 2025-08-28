package http.server.configurations;

import http.server.externalapis.LastFmWrapper;
import http.server.externalapis.Requester;
import http.server.externalapis.spotify.LastFmRequestStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalApiConfig {
    @Bean
    public LastFmWrapper lastFmWrappers() {
        return new LastFmWrapper(new Requester(new LastFmRequestStrategy()));
    }
}
