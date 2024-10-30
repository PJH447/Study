package com.demo.lucky_platform.config.other;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.iamport.api-key}")
    String iamportApiKey;

    @Value("${app.iamport.api-secret}")
    String iamportApiSecret;

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(iamportApiKey, iamportApiSecret);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .exposedHeaders("header1", "header2", "Authorization", "Set-Cookie")
                .allowedHeaders("*")
                .allowCredentials(true).maxAge(3600);

//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
//                .exposedHeaders("header1", "header2")
//                .allowedHeaders("*")
//                .allowCredentials(false).maxAge(3600);

    }
}
