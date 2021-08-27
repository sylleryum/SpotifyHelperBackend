package com.sylleryum.spotifyhelper.model.jsonResponses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderPlaylist {

    @JsonProperty("playlist_name")
    private String playlistName;
    private String order;

    public OrderPlaylist() {
    }

    public OrderPlaylist(String playlistName, String order) {
        this.playlistName = playlistName;
        this.order = order;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
