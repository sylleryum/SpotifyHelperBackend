package com.sylleryum.spotifyhelper.model.jsonResponses;

public class Url {

    private String url;
    private String traceId;

    public Url() {
    }

    public Url(String url, String traceId) {
        this.url = url;
        this.traceId = traceId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
