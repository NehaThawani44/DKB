package com.dkbfactory.urlshortener.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;




@Entity
public class Url {
    @Id
    private String hash;

    private String originalUrl;


    public Url() {}

    public Url(String hash, String originalUrl) {
        this.hash = hash;
        this.originalUrl = originalUrl;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}

