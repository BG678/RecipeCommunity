package com.recipecommunity.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Util class that provides methods required to generate a Jwt token, check token's validity,
 * and retrieve token's claims.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = 9098986554L;
    public static final long JWT_TOKEN_VALIDITY = 3 * 60 * 60 * 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * @param token Jwt token
     * @return username that is stored in a given token
     */
    public String getUsernameFromToken(String token) {
        return getAllClaimsFormToken(token).getSubject();
    }

    /**
     * @param token Jwt token
     * @return token's expiration time
     */
    public Date getExpirationTimeFromToken(String token) {
        return getAllClaimsFormToken(token).getExpiration();
    }

    /**
     * @param token Jwt token
     * @return Claims associated with given token
     */
    protected Claims getAllClaimsFormToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    /**
     * @param token Jwt token
     * @return true if token is not expired, otherwise false
     */
    private Boolean isTokenExpired(String token) {
        return getExpirationTimeFromToken(token).before(new Date());
    }

    /**
     * Generates a Jwt token that contains username taken from UserDetails object
     * as a subject, and validity which equals 3 hours as additional claims.
     *
     * @param userDetails UserDetails object that contains required data
     * @return Jwt token
     */
    public String generateToken(UserDetails userDetails) {
        LOGGER.debug("Generating token");
        return Jwts.builder().setClaims(new HashMap<>()).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();
    }

    /**
     * @param token       Jwt token
     * @param userDetails UserDetails object that contains required data
     * @return true if a username retrieved from a given token equals username stored
     * in a given userDetails object and token is not expired, otherwise false.
     */
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        return (getUsernameFromToken(token).equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}