package com.capitec.invoicetracking.configuration;

import com.capitec.invoicetracking.service.JWTUtility;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {
    @Bean
    public JWTUtility jwtUtility() {
        return Mockito.mock(JWTUtility.class);
    }
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry ->
                        registry.anyRequest().permitAll()
                )
                .securityMatcher("/**"); // optional, but keeps filter isolated

        return http.build();
    }
}
