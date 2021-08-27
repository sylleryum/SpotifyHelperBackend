package com.sylleryum.spotifyhelper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylleryum.spotifyhelper.config.Endpoints;
import com.sylleryum.spotifyhelper.helper.SpotifyObjectsConverter;
import com.sylleryum.spotifyhelper.helper.TraceIdGenerator;
import com.sylleryum.spotifyhelper.model.AccessToken;
import com.sylleryum.spotifyhelper.model.CreatePlaylist;
import com.sylleryum.spotifyhelper.model.FullTrackDetails;
import com.sylleryum.spotifyhelper.model.Uri;
import com.sylleryum.spotifyhelper.model.exception.MissingTokenException;
import com.sylleryum.spotifyhelper.model.jsonResponses.UserPlaylists;
import com.sylleryum.spotifyhelper.model.spotify.playlists.PlaylistItem;
import com.sylleryum.spotifyhelper.model.spotify.playlists.PlaylistList;
import com.sylleryum.spotifyhelper.model.spotify.singlePlaylist.Item;
import com.sylleryum.spotifyhelper.model.spotify.singlePlaylist.PreSinglePlaylist;
import com.sylleryum.spotifyhelper.model.spotify.singlePlaylist.SinglePlaylist;
import com.sylleryum.spotifyhelper.model.spotify.user.User;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

import static com.sylleryum.spotifyhelper.helper.TraceIdGenerator.METHOD_NAME_NOT_FOUND;
import static com.sylleryum.spotifyhelper.helper.TraceIdGenerator.writeDebug;

@Service
public class ServiceApiImpl implements ServiceApi {

//    private AccessToken accessToken;
//    private User user;

    //TODO remove
    private RestTemplate template = new RestTemplate();


    private final Endpoints endpoints;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ServiceApiImpl(Endpoints endpoints, WebClient webClient, ObjectMapper objectMapper) {
        this.endpoints = endpoints;
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }


    @Override
    public AccessToken getAccessToken(String theCode) throws MissingTokenException, URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> bodyParameters = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> requestEntity;
//        cleaner();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", endpoints.authorizationToAccess());

        bodyParameters.add("grant_type", "authorization_code");
        bodyParameters.add("code", theCode);
        bodyParameters.add("redirect_uri", endpoints.redirectUrl);
        requestEntity = new HttpEntity<>(bodyParameters, headers);

        return tokenCall(requestEntity, null);
    }


    /**
     * get users details and set the current user in the service
     *
     * @return User
     */
    @Override
    public User getUserDetails(AccessToken currentAccessToken) throws MissingTokenException, URISyntaxException, JsonProcessingException {
        AccessToken accessToken = beforeCall(currentAccessToken);

        User u = callApiGet(endpoints.userDetails, User.class, accessToken);

        return u;
    }

    @Override
    public PlaylistItem createPlaylist(String playlistName, AccessToken currentAccessToken) throws MissingTokenException, URISyntaxException, JsonProcessingException {
        AccessToken accessToken = beforeCall(currentAccessToken);

        User user = getUserDetails(accessToken);

        PlaylistItem result = webClient.post()
                .uri(endpoints.createPlaylist(user.getId()))
//                .headers(httpHeaders -> httpHeaders.addAll(this.headers))
                .header("Authorization", "Bearer " + accessToken.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new CreatePlaylist(playlistName)), CreatePlaylist.class)
                .retrieve()
                .bodyToMono(PlaylistItem.class).block();

        TraceIdGenerator.writeDebug("Playlist created: " + playlistName, ServiceApiImpl.class, StackWalker.getInstance().walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName)).orElse(METHOD_NAME_NOT_FOUND));
        return result;
    }

    @Override
    public String orderPlaylistRandom(String playlistId, AccessToken currentAccessToken) throws MissingTokenException, URISyntaxException, JsonProcessingException {
        //getPlaylistTracks(playlistId).values().stream().findFirst().get()
        AccessToken accessToken = beforeCall(currentAccessToken);

        Map<String, List<Item>> playlistTracks = getPlaylistTracks(playlistId, accessToken);
        List<String> uriList = playlistTracks.values().stream().findFirst().get()
                .stream().map(item -> item.getTrack().getUri()).collect(Collectors.toList());

        String playlistName = playlistTracks.keySet().stream().findFirst().orElse("playlist") + " Random";
        PlaylistItem playlistItem = createPlaylist(playlistName, accessToken);

        Collections.shuffle(uriList);

        List<String> tooBigList = new ArrayList<>();
        while (uriList.size() > 1) {
            if (uriList.size() < 99) {
                this.webClient.post()
                        .uri(endpoints.addItemsToPlaylist(playlistItem.getId()))
//                        .headers(httpHeaders -> httpHeaders.addAll(this.headers))
                        .header("Authorization", "Bearer " + accessToken.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(new Uri(uriList)), Uri.class)
                        .retrieve()
                        .bodyToMono(String.class).block();
                return playlistName;
            }
            tooBigList = uriList.subList(0, 99);
            uriList = uriList.subList(99, uriList.size());
            this.webClient.post()
                    .uri(endpoints.addItemsToPlaylist(playlistItem.getId()))
//                    .headers(httpHeaders -> httpHeaders.addAll(this.headers))
                    .header("Authorization", "Bearer " + accessToken.getAccessToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(new Uri(tooBigList)), Uri.class)
                    .retrieve()
                    .bodyToMono(String.class).block();
        }
        return playlistName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<Item>> getPlaylistTracks(String playlistId, AccessToken currentAccessToken) throws MissingTokenException, URISyntaxException, JsonProcessingException {
        AccessToken accessToken = beforeCall(currentAccessToken);
        List<Item> itemList = new ArrayList<>();
        String nextOffset;
        User user = getUserDetails(accessToken);

        //SinglePlaylist singlePlaylist = callApiGet(endpoints.singlePlaylist(playlistId, user.getCountry()), SinglePlaylist.class);
        PreSinglePlaylist preSinglePlaylist = callApiGet(endpoints.singlePlaylist(playlistId, user.getCountry()), PreSinglePlaylist.class, accessToken);
        SinglePlaylist singlePlaylist = preSinglePlaylist.getTracks();


        nextOffset = singlePlaylist.getNext();
        itemList.addAll(singlePlaylist.getItems());

        while (nextOffset != null) {

            singlePlaylist = callApiGet(nextOffset, SinglePlaylist.class, accessToken);
            itemList.addAll(singlePlaylist.getItems());
            nextOffset = singlePlaylist.getNext();
        }
        singlePlaylist.setName(preSinglePlaylist.getName());
        return Map.of(singlePlaylist.getName(), itemList);
    }

    @Override
    public List<FullTrackDetails> getUnavailables(String playlistId, AccessToken accessToken) throws MissingTokenException, URISyntaxException, JsonProcessingException {
        //beforeCall invoked inside getPlaylistTracks

        List<Item> itemList = getPlaylistTracks(playlistId, accessToken).values().stream().findFirst().get();
        List<FullTrackDetails> unavailableTracks = new ArrayList<>();

        for (Item item : itemList) {
            if (!item.getTrack().getIsPlayable()) {
                unavailableTracks.add(SpotifyObjectsConverter.itemToFullTrackDetails(item));
            }
        }
        System.out.println();
        return unavailableTracks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserPlaylists> getPlaylists(AccessToken currentAccessToken) throws MissingTokenException, URISyntaxException, JsonProcessingException {
        AccessToken accessToken = beforeCall(currentAccessToken);
        User user = getUserDetails(accessToken);

        PlaylistList playlistList = callApiGet(endpoints.getPlaylists, PlaylistList.class, accessToken);
        System.out.println();

        List<UserPlaylists> userPlaylists = playlistList.getItems().stream()
                .map(item -> new UserPlaylists(item.getName(), item.getId()))
                .collect(Collectors.toList());

        userPlaylists.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        return userPlaylists;
    }

    //=========================internal methods

    /**
     * Called through getAccessToken() or beforeCall(), used to get an access token and set this.user
     *
     * @param requestEntityCall
     * @return an access token
     * @throws RestClientResponseException
     * @throws MissingTokenException
     * @throws URISyntaxException
     */
    private AccessToken tokenCall(HttpEntity<MultiValueMap<String, String>> requestEntityCall, AccessToken accessToken) throws RestClientResponseException, MissingTokenException, URISyntaxException {

        AccessToken responseToken = template.postForObject(endpoints.getAccess, requestEntityCall, AccessToken.class);

        if (accessToken != null && accessToken.getRefreshToken() != null) {
            responseToken.setRefreshToken(accessToken.getRefreshToken());
        }
        responseToken.setValidity(3600000 + System.currentTimeMillis());
        String traceId = TraceIdGenerator.writeDebug("\nAccess token: " + responseToken.getAccessToken() +
                        "\nRefresh token: " + responseToken.getRefreshToken() +
                        "\nvalidity: " + responseToken.getValidity(),
                this.getClass(), StackWalker.getInstance().walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName)).orElse(METHOD_NAME_NOT_FOUND));

        return responseToken;

    }


    @Override
    public AccessToken setRefresh(String refresh) throws MissingTokenException, URISyntaxException {
        AccessToken accessToken = new AccessToken();
        accessToken.setRefreshToken(refresh);
        accessToken.setValidity(-1L);

        return beforeCall(accessToken);
    }


    public <T> T callApiGet(String url, Class<T> objectClass, AccessToken accessToken) throws JsonProcessingException {
//        cleaner();

        String response = webClient.get()
                .uri(url)
//                    .header("Authorization", "Bearer "+accessToken.getAccessToken())
                .header("Authorization", "Bearer " + accessToken.getAccessToken())
                //.headers(httpHeaders -> httpHeaders.addAll(this.headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JavaType javaType = objectMapper.getTypeFactory().constructType(objectClass);
        T result;
        result = objectMapper.readValue(response, javaType);
        System.out.println();
        return result;
//        } catch (RestClientResponseException e) {
//            System.out.println(e.getMessage());
//            System.out.println("Fail " + e.getResponseBodyAsString());
//            return null;
    }

    private AccessToken beforeCall(AccessToken accessToken) throws MissingTokenException, URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> bodyParameters = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> requestEntity;
//        cleaner();
        if (accessToken == null) {
            throw new MissingTokenException("No access or refresh token, please access the following URL to (re)start the authorization: " + endpoints.authorizeUrl());
        }

        if (accessToken.getAccessToken() != null && (accessToken.getValidity() > System.currentTimeMillis())) {
            headers.add("Authorization", "Bearer " + accessToken.getAccessToken());
            return accessToken;
        }

//        if (accessToken.getValidity() < System.currentTimeMillis()) {
        writeDebug("refreshing token", this.getClass(), StackWalker.getInstance().walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName)).orElse(METHOD_NAME_NOT_FOUND));

        headers.add("Authorization", endpoints.authorizationToAccess());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        bodyParameters.add("grant_type", "refresh_token");
        bodyParameters.add("refresh_token", accessToken.getRefreshToken());
        requestEntity = new HttpEntity<>(bodyParameters, headers);
        return tokenCall(requestEntity, accessToken);

    }


//    @Override
//    public RestTemplate interceptorRest() {
//        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
//        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
//        interceptors.add(new LoggingRequestInterceptor());
//        restTemplate.setInterceptors(interceptors);
//        return restTemplate;
//    }

//    private void cleaner() {
//        cleaner(true);
//    }
//
//    private void cleaner(Boolean addSpotifyHeader) {
//        headers.clear();
//        objectMapper = new ObjectMapper();
//        if (accessToken != null && accessToken.getAccessToken() != null && addSpotifyHeader == true) {
//            headers.add("Authorization", "Bearer " + accessToken.getAccessToken());
//        }
//        bodyParameters.clear();
//    }



}
