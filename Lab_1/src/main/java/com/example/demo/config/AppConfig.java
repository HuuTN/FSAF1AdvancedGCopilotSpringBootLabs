package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@EnableConfigurationProperties(AppConfig.MyProperties.class)
public class AppConfig {

    @Bean
    @ConditionalOnProperty(prefix = "my.feature", name = "enabled", havingValue = "true", matchIfMissing = false)
    public String myConditionalBean(MyProperties myProperties) {
        // Example bean, could be any type
        return "Feature is enabled: " + myProperties.getValue();
    }

    @ConfigurationProperties(prefix = "my.feature")
    public static class MyProperties {
        private String value;
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
}
