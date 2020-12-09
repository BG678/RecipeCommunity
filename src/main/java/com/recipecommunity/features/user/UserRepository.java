package com.recipecommunity.features.user;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * User Repository interface, extends Paging and Sorting Repository.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    /**
     * @param name -  username of wanted user
     * @return User object that has a given username or else null
     */
    User findByUsername(String name);

    /**
     * @param username name of user to check
     * @return true if user with given username already exists, otherwise false
     */
    Boolean existsByUsername(String username);
}
