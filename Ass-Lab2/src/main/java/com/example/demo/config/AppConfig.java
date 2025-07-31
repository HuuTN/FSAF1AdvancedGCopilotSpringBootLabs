package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.apis.interceptor.RateLimitInterceptor;

@Configuration
@EnableConfigurationProperties(AppConfig.MyProperties.class)
public class AppConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/auth/login");
    }

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
