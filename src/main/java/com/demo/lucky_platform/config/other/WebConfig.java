package com.demo.lucky_platform.config.other;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class WebConfig {

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
}
