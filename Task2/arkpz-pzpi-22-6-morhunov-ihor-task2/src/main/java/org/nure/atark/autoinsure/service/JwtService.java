package org.nure.atark.autoinsure.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.nure.atark.autoinsure.entity.User;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SECRET_KEY)
                .compact();
    }
}
