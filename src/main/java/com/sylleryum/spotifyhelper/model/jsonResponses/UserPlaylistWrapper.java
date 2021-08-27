package com.sylleryum.spotifyhelper.model.jsonResponses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserPlaylistWrapper {
    @JsonProperty("user_playlists")
    List<UserPlaylists> userPlaylists;

    public UserPlaylistWrapper() {
    }

    public UserPlaylistWrapper(List<UserPlaylists> userPlaylists) {
        this.userPlaylists = userPlaylists;
    }

    public List<UserPlaylists> getUserPlaylists() {
        return userPlaylists;
    }

    public void setUserPlaylists(List<UserPlaylists> userPlaylists) {
        this.userPlaylists = userPlaylists;
    }
}
