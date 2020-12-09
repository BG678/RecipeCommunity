package com.recipecommunity.features.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for User feature.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    /**
     * Gets a user with a given username, using UserRepository interface.
     *
     * @param username - name of wanted user
     * @return a proper User object
     */
    public User findUserByUsername(String username) {
        return repository.findByUsername(username);
    }
}

