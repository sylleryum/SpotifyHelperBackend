package com.sylleryum.spotifyhelper.model.jsonResponses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sylleryum.spotifyhelper.model.FullTrackDetails;

import java.util.List;

public class UnavailableTracksWrapper {

    @JsonProperty("unavailable_tracks")
    List<FullTrackDetails> unavailableTracks;

    public UnavailableTracksWrapper() {
    }

    public UnavailableTracksWrapper(List<FullTrackDetails> list) {
        this.unavailableTracks = list;
    }

    public List<FullTrackDetails> getUnavailableTracks() {
        return unavailableTracks;
    }

    public void setUnavailableTracks(List<FullTrackDetails> unavailableTracks) {
        this.unavailableTracks = unavailableTracks;
    }
}
