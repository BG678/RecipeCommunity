package com.recipecommunity.features.jwt;

import java.io.Serializable;

/**
 * POJO class that represents JwtResponse. Stores just generated Jwt token.
 */
public class JwtResponse implements Serializable {
    private static final long serialVersionUID = 878887554L;
    private final String jwtToken;

    public JwtResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getToken() {
        return this.jwtToken;
    }
}
