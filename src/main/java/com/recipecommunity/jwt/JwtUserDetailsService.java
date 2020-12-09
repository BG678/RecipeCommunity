package com.recipecommunity.jwt;

import com.recipecommunity.features.user.User;
import com.recipecommunity.features.user.UserService;
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
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findUserByUsername(username);
        if (user == null) {
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
        return userService.saveUser(newUser);
    }
}
