package com.dkbfactory.urlshortener.controller;

import com.dkbfactory.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UrlController {
    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

//    @PostMapping("/shorten")
//    public ResponseEntity<Map<String, String>> shortenUrl(@RequestBody Map<String, String> request) {
//        String originalUrl = request.get("url");
//        String shortUrl = urlService.shortenUrl(originalUrl);
//        return ResponseEntity.ok(Map.of("shortened_url", shortUrl));
//    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> resolveUrl(@PathVariable String hash, HttpServletResponse response) throws IOException {
        String originalUrl = urlService.getOriginalUrl(hash);
        response.sendRedirect(originalUrl);
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody(required = false) Map<String, String> request) {
        // Validate request body
        if (request == null || !request.containsKey("url") ||
                request.get("url") == null || request.get("url").trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Invalid URL provided"));
        }

        String originalUrl = request.get("url");
        String shortUrl = urlService.shortenUrl(originalUrl);

        return ResponseEntity.ok(Map.of("shortened_url", shortUrl));
    }
}

