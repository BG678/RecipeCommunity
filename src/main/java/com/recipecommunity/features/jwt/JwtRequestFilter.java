package com.recipecommunity.features.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JwtRequestFilter class that extends OncePerRequestFilter abstract class. Overrides doFilterInternal method.
 *
 * @version %I%, %G%
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String jwtToken = resolveToken(request);
        String username = getUserNameFromToken(jwtToken);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.isTokenValid(jwtToken, userDetails)) {
                configure(request, userDetails);
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * Retrieves a Jwt token by getting a substring from an Authorization header
     *
     * @param request HttpServletRequest object that contains the request the client has made of the servlet
     * @return Jwt token
     */
    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        LOGGER.warn("JWT Token doesn't begin with Bearer String");
        return null;
    }

    /**
     * @param token Jwt token
     * @return username that is retrieved from token, using JwtTokenUtil instance
     */
    private String getUserNameFromToken(String token) {
        String username = null;
        try {
            username = jwtTokenUtil.getUsernameFromToken(token);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Can't get a Jwt token");
        } catch (ExpiredJwtException e) {
            LOGGER.warn("Jwt token's expired");
        }
        return username;
    }

    /**
     * Sets AuthenticationToken object into the current SecurityContext.
     *
     * @param request     HttpServletRequest object that contains the request the client has made of the servlet
     * @param userDetails UserDetails object
     */
    private void configure(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authenticationToken
                .setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}