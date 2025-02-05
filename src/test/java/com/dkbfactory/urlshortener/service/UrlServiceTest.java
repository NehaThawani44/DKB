package com.dkbfactory.urlshortener.service;


import com.dkbfactory.urlshortener.Repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.Mockito.when;

import com.dkbfactory.urlshortener.model.Url;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testShortenUrl_Success() {
        String originalUrl = "https://www.example.com";
        String expectedHash = Integer.toHexString(originalUrl.hashCode());
        String expectedShortUrl = "http://short.ly/" + expectedHash;


        String result = urlService.shortenUrl(originalUrl);


        assertThat(result).isEqualTo(expectedShortUrl);
        verify(urlRepository).save(any(Url.class));
    }

    @Test
    void testGetOriginalUrl_FromCache() {
        String hash = "abc123";
        String cachedUrl = "https://www.example.com";

        when(valueOperations.get(hash)).thenReturn(cachedUrl);


        String result = urlService.getOriginalUrl(hash);

        assertThat(result).isEqualTo(cachedUrl);
        verify(valueOperations).get(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void testGetOriginalUrl_FromDatabase() {
        String hash = "abc123";
        String originalUrl = "https://www.example.com";
        Url url = new Url(hash, originalUrl);

        when(valueOperations.get(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash);

        assertThat(result).isEqualTo(originalUrl);
        verify(valueOperations).get(hash);
        verify(urlRepository).findById(hash);
        verify(valueOperations).set(hash, originalUrl);
    }

    @Test
    void testGetOriginalUrl_NotFound() {
        String hash = "nonexistent";

        when(valueOperations.get(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> urlService.getOriginalUrl(hash))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("URL not found");
    }

    @Test
    void testShortenUrl_DifferentUrls() {
        String url1 = "https://www.example1.com";
        String url2 = "https://www.example2.com";

        String result1 = urlService.shortenUrl(url1);
        String result2 = urlService.shortenUrl(url2);

        assertThat(result1).isNotEqualTo(result2);
    }
}
