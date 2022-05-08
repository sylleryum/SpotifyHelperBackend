package com.sylleryum.spotifyhelper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sylleryum.spotifyhelper.model.AccessToken;
import com.sylleryum.spotifyhelper.model.FullTrackDetails;
import com.sylleryum.spotifyhelper.model.exception.MissingTokenException;
import com.sylleryum.spotifyhelper.model.jsonResponses.UserPlaylists;
import com.sylleryum.spotifyhelper.model.spotify.playlists.PlaylistItem;
import com.sylleryum.spotifyhelper.model.spotify.singlePlaylist.Item;
import com.sylleryum.spotifyhelper.model.spotify.user.User;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface ServiceApi {


    AccessToken getAccessToken(String theCode) throws MissingTokenException, URISyntaxException;

    User getUserDetails(AccessToken accessToken) throws MissingTokenException, URISyntaxException, JsonProcessingException;

    PlaylistItem createPlaylist(String playlistName, AccessToken accessToken) throws MissingTokenException, URISyntaxException, JsonProcessingException;

    AccessToken setRefresh(String refresh) throws MissingTokenException, URISyntaxException;

    String orderPlaylistRandom(String playlistId, AccessToken currentAccessToken) throws MissingTokenException, URISyntaxException, JsonProcessingException;

    /**
     * @param playlistId
     * @return singleton map where key=playlist name and value=list<Item>
     * @throws MissingTokenException
     * @throws URISyntaxException
     */
    Map<String, List<Item>> getPlaylistTracks(String playlistId, AccessToken accessToken) throws MissingTokenException, URISyntaxException, JsonProcessingException;

    List<FullTrackDetails> getUnavailables(String playlistId, AccessToken accessToken) throws MissingTokenException, URISyntaxException, JsonProcessingException;

    /**
     *
     * @param accessToken
     * @return returns a sorted order of {@link UserPlaylists} from current user
     * @throws MissingTokenException
     * @throws URISyntaxException
     * @throws JsonProcessingException
     */
    List<UserPlaylists> getPlaylists(AccessToken accessToken) throws MissingTokenException, URISyntaxException, JsonProcessingException;

    //    PlaylistItem createPlaylist(String playlistName) throws MissingTokenException, URISyntaxException;
//    AccessToken beforeCall(AccessToken accessToken) throws MissingTokenException, URISyntaxException;
//    /**
//     * get playlists owned by user
//     *
//     * @return
//     */

//    RestTemplate interceptorRest();
}
