package com.robertbalazsi.techcompass.twofactorauth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Encapsulates the application's externalized properties.
 */
@ConfigurationProperties
public class ApplicationProperties {

    @Getter
    @Setter
    private String filesDir;
}
