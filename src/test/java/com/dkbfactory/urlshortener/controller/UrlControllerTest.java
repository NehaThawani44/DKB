package com.dkbfactory.urlshortener.controller;

import com.dkbfactory.urlshortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
class UrlControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    void testShortenUrl() throws Exception {
        // Arrange
        String originalUrl = "https://www.example.com";
        String shortUrl = "http://short.url/abc123";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("url", originalUrl);

        when(urlService.shortenUrl(originalUrl)).thenReturn(shortUrl);

        // Act & Assert
        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + originalUrl + "\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortened_url").value(shortUrl));

        verify(urlService).shortenUrl(originalUrl);
    }

    @Test
    void testResolveUrl() throws Exception {
        // Arrange
        String hash = "abc123";
        String originalUrl = "https://www.example.com";

        when(urlService.getOriginalUrl(hash)).thenReturn(originalUrl);

        // Act & Assert
        mockMvc.perform(get("/api/v1/{hash}", hash))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));

        verify(urlService).getOriginalUrl(hash);
    }

//    @Test
//    void testShortenUrlWithInvalidInput() throws Exception {
//        // Act & Assert
//        mockMvc.perform(post("/api/v1/shorten")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"url\":\"\"}"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testShortenUrlWithInvalidInput() throws Exception {
//        // Test null request body
//        mockMvc.perform(post("/api/v1/shorten")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(""))
//                .andExpect(status().isBadRequest());
//
//        // Test empty URL
//        mockMvc.perform(post("/api/v1/shorten")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"url\":\"\"}"))
//                .andExpect(status().isBadRequest());
//
//        // Test null URL
//        mockMvc.perform(post("/api/v1/shorten")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"url\":null}"))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void testShortenUrlWithInvalidInput() throws Exception {
        // Test empty request body
        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        // Test null URL
        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":null}"))
                .andExpect(status().isBadRequest());

        // Test empty URL string
        mockMvc.perform(post("/api/v1/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

}