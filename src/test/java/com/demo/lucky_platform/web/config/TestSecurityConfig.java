package com.demo.lucky_platform.web.config;

import com.demo.lucky_platform.web.user.domain.AuthenticatedUser;
import com.demo.lucky_platform.web.user.domain.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Collections;

/**
 * Test security configuration that permits all requests but still supports authentication.
 * This is used in integration tests to bypass authentication for most requests,
 * but still support @WithMockUser for tests that need an authenticated user.
 */
@TestConfiguration
@Profile("test")
@EnableMethodSecurity
public class TestSecurityConfig {

    /**
     * Creates a security filter chain that permits all requests but still supports authentication.
     * This overrides the security configuration in the main application.
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    /**
     * Creates a UserDetailsService that returns a mock user for tests.
     * This is used by @WithMockUser to create an authenticated user.
     *
     * @return the UserDetailsService
     */
    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        // Create a mock user for testing
        User mockUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .nickname("testuser")
                .build();

        // Create an AuthenticatedUser from the mock user
        UserDetails authenticatedUser = new AuthenticatedUser(mockUser);

        return new InMemoryUserDetailsManager(Collections.singletonList(authenticatedUser));
    }
}
