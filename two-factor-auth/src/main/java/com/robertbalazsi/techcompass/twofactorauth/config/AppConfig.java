package com.robertbalazsi.techcompass.twofactorauth.config;

import com.robertbalazsi.techcompass.twofactorauth.Application;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@ComponentScan(basePackageClasses = Application.class)
public class AppConfig {

    @Bean
    public PropertyPlaceholderConfigurer propertyConfigurer() {
        PropertyPlaceholderConfigurer propertyConfigurer = new PropertyPlaceholderConfigurer();
        propertyConfigurer.setLocation(new ClassPathResource("/persistence.properties"));
        return propertyConfigurer;
    }
}
