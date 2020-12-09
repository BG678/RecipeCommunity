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

    /**
     * @param user to be saved, using User Repository interface
     * @return User object that was saved
     */
    public User saveUser(User user){
        return repository.save(user);
    }

    /**
     * Uses UserRepository instance to check if user with given username already exists
     *
     * @param username name of user to check
     * @return Boolean value that was returned by repository's method - existsByUserName method
     */
    public Boolean existsByUsername(String username){
        return repository.existsByUsername(username);
    }

}

