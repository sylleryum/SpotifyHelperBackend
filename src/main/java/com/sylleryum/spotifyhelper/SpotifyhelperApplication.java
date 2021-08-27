package com.sylleryum.spotifyhelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylleryum.spotifyhelper.config.Endpoints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class SpotifyhelperApplication {

    final Endpoints endpoints;

    public SpotifyhelperApplication(Endpoints endpoints) {
        this.endpoints = endpoints;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpotifyhelperApplication.class, args);
    }

    @Bean
    public WebClient getWebClient() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder().codecs(c ->
                                c.defaultCodecs()
                                        //.enableLoggingRequestDetails(true)
                                        .maxInMemorySize(50 * 1024 * 1024)
                        )
                                .codecs(c ->
                                        c.defaultCodecs().enableLoggingRequestDetails(true))
                                .build()
                )
                .baseUrl(endpoints.baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .build();
    }

    @Bean
    public ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }
}
