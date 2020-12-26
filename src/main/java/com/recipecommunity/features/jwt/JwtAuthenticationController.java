package com.recipecommunity.jwt;
import com.recipecommunity.features.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller class that enables client to sign in and sign up.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class JwtAuthenticationController {
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private JwtUserDetailsService userDetailsService;
    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationController.class);
    @Autowired
    public JwtAuthenticationController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, JwtUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Authenticates user with data provided by jwtRequest. After successful authentication Jwt token is generated
     * using JwtTokenUtil instance. Returns generated token stored by JwtResponse inside ResponseEntity object
     *
     * @param jwtRequest JwtRequest object that should contain valid data
     * @return ResponseEntity object with ok status and JwtResponse object body that contains generated token
     * @throws Exception thrown by authenticate method
     */
    @PostMapping("/sign-in")
    public ResponseEntity<JwtResponse> createAuthenticationToken(@Valid @RequestBody JwtRequest jwtRequest) throws Exception {
        String username = jwtRequest.getUsername();
        authenticate(username, jwtRequest.getPassword());
        String token = jwtTokenUtil.generateToken(
                userDetailsService.loadUserByUsername(username));
        LOGGER.debug("Signed in " + username);
        return ResponseEntity.status(HttpStatus.OK).cacheControl(CacheControl.noCache()).body(new JwtResponse(token));
    }

    /**
     * Saves a new User object that contains data received from client if user with given username
     * doesn't already exist and returns saved User object inside ResponseEntity object.
     * If user with given username already exists, this method throws UsernameIsAlreadyTakenException.
     *
     * @param jwtRequest JwtRequest object that should contain valid data
     * @return ResponseEntity object with created status and body that contains saved user
     */
    @PostMapping("/sign-up")
    public ResponseEntity<User> saveUser(@Valid @RequestBody JwtRequest jwtRequest) {
        if (userDetailsService.doesUserAlreadyExist(jwtRequest.getUsername())){
            LOGGER.warn("User already Exists");
            throw new UsernameIsAlreadyTakenException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }
        LOGGER.debug("Signed up " + jwtRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(userDetailsService.saveNewUser(jwtRequest));
    }

    /**
     * Authenticates user with given username and password using AuthenticationManager instance.
     *
     * @param username to be authenticated
     * @param password that should match given username
     * @throws Exception when authentication fails
     */
    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
