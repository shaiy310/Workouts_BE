package com.YaakoviCorp.Fit1337.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "redis")
public class RedisDetailsConfig {
    String host;
    long port;
    long ttl;

    public RedisDetailsConfig(String host, long port, long ttl) {
        this.host = host;
        this.port = port;
        this.ttl = ttl;
    }
    public String getHost() {
        return host;
    }

    public long getPort() {
        return port;
    }

    public long getTtl() {
        return ttl;
    }
}