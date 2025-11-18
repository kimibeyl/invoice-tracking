package com.capitec.invoicetracking.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTUtility {
    String generateToken(UserDetails userDetails);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);
}
