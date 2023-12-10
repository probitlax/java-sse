package com.sytac.dataharvester.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "platform-properties")
@Setter
@Getter
public class PlatformProperties {
    private int connectionTimeout;
    private int readTimeout;
    private Map<String, PlatformDetails> platforms;
}
