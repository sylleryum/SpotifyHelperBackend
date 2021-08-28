package com.sylleryum.spotifyhelper.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sylleryum.spotifyhelper.config.Endpoints;
import com.sylleryum.spotifyhelper.helper.TraceIdGenerator;
import com.sylleryum.spotifyhelper.model.AccessToken;
import com.sylleryum.spotifyhelper.model.FullTrackDetails;
import com.sylleryum.spotifyhelper.model.jsonResponses.UserPlaylistWrapper;
import com.sylleryum.spotifyhelper.model.jsonResponses.UserPlaylists;
import com.sylleryum.spotifyhelper.model.spotify.user.User;
import com.sylleryum.spotifyhelper.model.jsonResponses.OrderPlaylist;
import com.sylleryum.spotifyhelper.model.jsonResponses.UnavailableTracksWrapper;
import com.sylleryum.spotifyhelper.model.exception.MissingArgumentException;
import com.sylleryum.spotifyhelper.model.exception.MissingTokenException;
import com.sylleryum.spotifyhelper.service.ServiceApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static com.sylleryum.spotifyhelper.helper.Literals.SESSION_ACCESS_TOKEN;
import static com.sylleryum.spotifyhelper.helper.TraceIdGenerator.METHOD_NAME_NOT_FOUND;

@RestController
@RequestMapping
public class TheController {

    private final ServiceApi serviceApi;

    public TheController(ServiceApi serviceApi) {
        this.serviceApi = serviceApi;
    }

    @GetMapping(path = {"/get-unavailables/{playlistId}", "/get-unavailables"})
    public ResponseEntity<?> getUnavailables(@PathVariable Optional<String> playlistId, HttpSession session) throws Exception {
        if (playlistId.isEmpty()) {
            throw new MissingArgumentException("Missing Playlist ID");
        }
        AccessToken accessToken = (AccessToken) session.getAttribute(SESSION_ACCESS_TOKEN);

        List<FullTrackDetails> unavailables = serviceApi.getUnavailables(playlistId.get(), accessToken);

        return ResponseEntity.ok(new UnavailableTracksWrapper(unavailables));
    }

    @GetMapping(path = {"/order-playlist/{playlistId}/random"})
    public ResponseEntity<?> orderPlaylistRandom(@PathVariable Optional<String> playlistId, HttpSession session) throws MissingArgumentException, MissingTokenException, URISyntaxException, JsonProcessingException {
        if (playlistId.isEmpty()) {
            throw new MissingArgumentException("Missing Playlist ID");
        }
        AccessToken accessToken = (AccessToken) session.getAttribute(SESSION_ACCESS_TOKEN);

        String playlistRandom = serviceApi.orderPlaylistRandom(playlistId.get(), accessToken);

        return ResponseEntity.ok(new OrderPlaylist(playlistRandom, "random"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUser(HttpSession session) throws MissingTokenException, URISyntaxException, JsonProcessingException {
        String traceId = TraceIdGenerator.writeTrace(this.getClass(), StackWalker.getInstance().walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName)).orElse(METHOD_NAME_NOT_FOUND));
        AccessToken accessToken = (AccessToken) session.getAttribute(SESSION_ACCESS_TOKEN);
System.out.println("-------------------------");
if (session.getAttribute("accessToken")!=null){
    System.out.println(session.getAttribute("accessToken"));
}
        User user = serviceApi.getUserDetails(accessToken);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/playlists")
    public ResponseEntity<?> getPlaylists(HttpSession session) throws MissingTokenException, URISyntaxException, JsonProcessingException {
        String traceId = TraceIdGenerator.writeTrace(this.getClass(), StackWalker.getInstance().walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName)).orElse(METHOD_NAME_NOT_FOUND));
        AccessToken accessToken = (AccessToken) session.getAttribute(SESSION_ACCESS_TOKEN);

        List<UserPlaylists> playlists = serviceApi.getPlaylists(accessToken);
        return ResponseEntity.ok(new UserPlaylistWrapper(playlists));
    }

}
