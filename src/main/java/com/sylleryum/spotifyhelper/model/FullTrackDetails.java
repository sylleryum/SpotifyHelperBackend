package com.sylleryum.spotifyhelper.model;

public class FullTrackDetails {

        String uri;
        String artist;
        String name;

    public FullTrackDetails(String uri, String artist, String name) {
        this.uri = uri;
        this.artist = artist;
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
