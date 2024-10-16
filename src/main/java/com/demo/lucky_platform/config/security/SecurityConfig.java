package com.demo.lucky_platform.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    private final CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtEntryPoint jwtEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) ->
                    auth.requestMatchers("/badge/**").permitAll()
                        .requestMatchers("/images/**", "/favicon.ico").permitAll()

                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
            )
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                                   .maximumSessions(1)
                                                   .maxSessionsPreventsLogin(true)
                                                   .expiredUrl("/")
            )
            .servletApi((servletApi) -> servletApi.rolePrefix(""))
            .formLogin(AbstractHttpConfigurer::disable)
//            .rememberMe((rememberMe) -> rememberMe.rememberMeServices(rememberMeServices()))

            .exceptionHandling((handling) -> handling.authenticationEntryPoint(jwtEntryPoint)
                                                     .accessDeniedHandler(accessDeniedHandler))
            .addFilterBefore(characterEncodingFilter, CsrfFilter.class)
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .securityContext((securityContext) -> {
                        securityContext.securityContextRepository(delegatingSecurityContextRepository());
                        securityContext.requireExplicitSave(true);
                    }
            )
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DelegatingSecurityContextRepository delegatingSecurityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

}