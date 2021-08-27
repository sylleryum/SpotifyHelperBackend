package com.sylleryum.spotifyhelper.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class Endpoints {

    private final SpotifyCredentials spotifyCredentials;


    public final String baseUrl = "https://api.spotify.com/";
    public final String redirectUrl;
    public final String getAccess = "https://accounts.spotify.com/api/token";
    public final String userDetails = "v1/me";
    public final String getPlaylists = "https://api.spotify.com/v1/me/playlists";

    private final String authorization;
    private final String authorizationToAccess;
    private final String authorizeUrl;

    @Autowired
    public Endpoints(SpotifyCredentials spotifyCredentials, @Value("${spotify.redirectUrl}") String redirectUrl) {
        this.spotifyCredentials = spotifyCredentials;
        this.redirectUrl = redirectUrl;
        authorization = Base64.getEncoder().encodeToString((spotifyCredentials.getClientId() + ":" + spotifyCredentials.getSecretId()).getBytes());
        authorizationToAccess = "Basic " + authorization;
        authorizeUrl = "https://accounts.spotify.com/authorize?client_id=" +
                spotifyCredentials.getClientId() + "&response_type=code&redirect_uri=" +
                URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8) +
                "&scope=user-read-private%20user-read-email%20playlist-read-private%20playlist-modify-public%20playlist-modify-private";
    }

    /**
     * https://api.spotify.com/v1/playlists/{playlistId}/tracks
     *
     * @param playlistId
     * @return
     */
    public String addItemsToPlaylist(String playlistId) {
        return String.format("v1/playlists/%s/tracks", playlistId);

    }

    /**
     * https://api.spotify.com/v1/playlists/{playlistId}/tracks?offset={offset}&limit=100&market={market}
     *
     * @param playlistId
     * @param market
     * @return
     */
    public String singlePlaylist(String playlistId, String market) {
        //https://api.spotify.com/v1/playlists/%s?market=%s
        //https://api.spotify.com/v1/playlists/%s/tracks?limit=100&market=%s
        return String.format("v1/playlists/%s?market=%s", playlistId, market);
    }

    /**
     * https://api.spotify.com/v1/users/{userId}/playlists
     *
     * @param userId
     * @return
     */
    public String createPlaylist(String userId) {
        return String.format("v1/users/%s/playlists", userId);
    }

    /**
     * @return base64 <client id:secret id>
     */
    public String authorization() {
        return authorization;
    }

    /**
     * @return Basic base64 <client id:secret id>
     */
    public String authorizationToAccess() {
        return authorizationToAccess;
    }

    /**
     * @return Url to ask authorization
     */
    public String authorizeUrl() {
        return authorizeUrl;
    }

}
