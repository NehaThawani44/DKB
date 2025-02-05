package com.dkbfactory.urlshortener.service;

import com.dkbfactory.urlshortener.Repository.UrlRepository;
import com.dkbfactory.urlshortener.model.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public UrlService(UrlRepository urlRepository, RedisTemplate<String, String> redisTemplate) {
        this.urlRepository = urlRepository;
        this.redisTemplate = redisTemplate;
    }

    public String shortenUrl(String originalUrl) {
        String hash = Integer.toHexString(originalUrl.hashCode());
        urlRepository.save(new Url(hash, originalUrl));
        return "http://short.ly/" + hash;
    }

    public String getOriginalUrl(String hash) {
        String cachedUrl = redisTemplate.opsForValue().get(hash);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        Url url = urlRepository.findById(hash)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        redisTemplate.opsForValue().set(hash, url.getOriginalUrl());
        return url.getOriginalUrl();
    }
}