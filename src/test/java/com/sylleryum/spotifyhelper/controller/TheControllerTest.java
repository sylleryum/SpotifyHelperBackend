package com.sylleryum.spotifyhelper.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylleryum.spotifyhelper.model.AccessToken;
import com.sylleryum.spotifyhelper.model.exception.MissingTokenException;
import com.sylleryum.spotifyhelper.model.spotify.user.User;
import com.sylleryum.spotifyhelper.service.ServiceApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class TheControllerTest {

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(theController)
                .setControllerAdvice(new ExceptionHandlerController())
                .build();

        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Mock
    private ServiceApi serviceApi;

    @InjectMocks
    private TheController theController;

    private JacksonTester<Object> jacksonTester;

    @Test
    void noToken() throws Exception {
        given(serviceApi.getUserDetails(null))
                .willThrow(MissingTokenException.class);

        MockHttpServletResponse response = mvc.perform(
                get("/me")
                        .accept(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("errorMessage");
    }

    @Test
    void getUnavailables() throws Exception {
    }

    @Test
    void orderPlaylistRandom() {
    }

    @Test
    void getUser() throws Exception {
        // given
        given(serviceApi.getUserDetails(null))
                .willReturn(new User());

        // when
        MockHttpServletResponse response = mvc.perform(
                        get("/me")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void getPlaylists() {
    }
}