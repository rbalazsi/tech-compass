package com.robertbalazsi.techcompass.twofactorauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Initializes the application.
 */
@Component
public class ApplicationInitializer implements ApplicationRunner {

    @Autowired
    private ApplicationProperties appProps;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Files.createDirectories(Paths.get(appProps.getFilesDir()));
    }
}
