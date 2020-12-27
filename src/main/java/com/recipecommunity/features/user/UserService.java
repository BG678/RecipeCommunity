package com.recipecommunity.features.user;

import com.recipecommunity.features.utils.exception.ResourceNotFoundException;
import com.recipecommunity.features.utils.UserByUsername;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for User feature.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@Service
public class UserService implements UserByUsername {
    private final UserRepository repository;
    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Gets a user with a given username, using UserRepository interface.
     *
     * @param username - name of wanted user
     * @return a proper User object
     */
    @Override
    public User findUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    /**
     * @param user to be saved, using User Repository interface
     * @return User object that was saved
     */
    public User saveUser(User user) {
        LOGGER.debug("Saving a new user");
        return repository.save(user);
    }

    /**
     * Uses UserRepository instance to check if user with given username already exists
     *
     * @param username name of user to check
     * @return Boolean value that was returned by repository's method - existsByUserName method
     */
    public Boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    /**
     * @param pageable PageRequest object that should contain data about wanted page number and size
     * @return wanted Page of Users
     */
    public Page<User> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * @param id id of wanted User object
     * @return proper User object, if user with given id does't exist this method throws new ResourceNotFoundException
     */
    public User findUserById(Long id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }


}

