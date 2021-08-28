package com.sylleryum.spotifyhelper.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sylleryum.spotifyhelper.config.Endpoints;
import com.sylleryum.spotifyhelper.helper.Literals;
import com.sylleryum.spotifyhelper.helper.TraceIdGenerator;
import com.sylleryum.spotifyhelper.model.AccessToken;
import com.sylleryum.spotifyhelper.model.FullTrackDetails;
import com.sylleryum.spotifyhelper.model.exception.MissingArgumentException;
import com.sylleryum.spotifyhelper.model.exception.MissingTokenException;
import com.sylleryum.spotifyhelper.model.jsonResponses.Url;
import com.sylleryum.spotifyhelper.service.ServiceApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import static com.sylleryum.spotifyhelper.helper.Literals.SESSION_ACCESS_TOKEN;
import static com.sylleryum.spotifyhelper.helper.TraceIdGenerator.METHOD_NAME_NOT_FOUND;

@RestController
@RequestMapping
public class AuthController {

    private final Endpoints endpoints;
    private final ServiceApi serviceApi;

    public AuthController(Endpoints endpoints, ServiceApi serviceApi) {
        this.endpoints = endpoints;
        this.serviceApi = serviceApi;
    }

    @GetMapping(value = {"/authentication-url", "authentication-url"})
    public ResponseEntity<?> getAuthorizeUrl(@RequestParam Optional<String> state) {
        String traceId = TraceIdGenerator.writeTrace(this.getClass(),StackWalker.getInstance().walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName)).orElse(METHOD_NAME_NOT_FOUND));

        if (state.isPresent()){
            return ResponseEntity.ok(new Url(endpoints.authorizeUrl()+"&state="+state.get(),
                    traceId));
        }
        return ResponseEntity.ok(new Url(endpoints.authorizeUrl(), traceId));
    }

    //TODO needs to update the access token on every call
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam String code, @RequestParam Optional<String>state, HttpSession session, HttpServletResponse response) throws MissingTokenException, URISyntaxException, IOException {
        String traceId = TraceIdGenerator.writeTrace(this.getClass(),StackWalker.getInstance().walk(frames -> frames.findFirst().map(StackWalker.StackFrame::getMethodName)).orElse(METHOD_NAME_NOT_FOUND));
        AccessToken accessToken = serviceApi.getAccessToken(code);

        session.setAttribute(SESSION_ACCESS_TOKEN, accessToken);

        TraceIdGenerator.writeDebug("Authorized",this.getClass(),null);
        System.out.println("==========sending session cookie");
//        response.setHeader("Set-Cookie", "key=value; HttpOnly; SameSite=None; secure");
        if (state.isPresent()) {
            response.sendRedirect(state.get());
        }
        return ResponseEntity.ok("ok");
    }

    //TODO signout
}
