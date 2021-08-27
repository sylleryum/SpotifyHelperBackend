package com.sylleryum.spotifyhelper.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class SpotifyCredentials {

//    @Value("${spotify.clientId}")
    private final String clientId;
//    @Value("${spotify.secretId}")
    private final String secretId;

    public SpotifyCredentials(@Value("${spotify.clientId}") String clientId, @Value("${spotify.secretId}") String secretId) {
        this.clientId = clientId;
        this.secretId = secretId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getSecretId() {
        return secretId;
    }
}
