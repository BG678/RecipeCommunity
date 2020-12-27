package com.recipecommunity.features.jwt;

import com.recipecommunity.features.user.User;
import com.recipecommunity.features.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service class which implements a UserDetailsService interface, uses UserService and PasswordEncoder instances.
 * This class overrides loadUserByUsername method and provides method that allows saving a new user.
 *
 * @version %I%, %G%
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            LOGGER.error("User not found: " + username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                new ArrayList<>());
    }

    /**
     * Saves a new User object that contains proper data stored in a JwtRequest object, using UserService instance.
     * A newUser's password is encoded using PasswordEncoder.
     *
     * @param request object that stores username and password
     * @return User object that just has been saved
     */
    public User saveNewUser(JwtRequest request) {
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        LOGGER.debug("Saving a new user");
        return userService.saveUser(newUser);
    }

    /**
     * Uses UserService instance to check if user with given username already exists
     *
     * @param username name of user to check
     * @return Boolean value that was returned by userService's method - existsByUserName method
     */
    protected Boolean doesUserAlreadyExist(String username) {
        return userService.existsByUsername(username);
    }
}
