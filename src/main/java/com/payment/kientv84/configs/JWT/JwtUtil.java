package com.payment.kientv84.configs.JWT;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final JwtConfig jwtConfig;

    @Autowired
    public JwtUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    // Tạo token
    public String generateToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withIssuer(jwtConfig.getIssuer())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .sign(Algorithm.HMAC256(jwtConfig.getSecret()));
    }

    // Xác thực token và trả về email
    public String validateToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(jwtConfig.getSecret()))
                .withIssuer(jwtConfig.getIssuer())
                .build()
                .verify(token);

        return decodedJWT.getSubject(); // email
    }
}

