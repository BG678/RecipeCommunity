package com.recipecommunity.features.saved_recipe;

import com.recipecommunity.features.utils.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Service class for SavedRecipe feature.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@Service
public class SavedRecipeService {
    private final SavedRecipeRepository repository;
    private final Logger LOGGER = LoggerFactory.getLogger(SavedRecipeService.class);

    @Autowired
    public SavedRecipeService(SavedRecipeRepository repository) {
        this.repository = repository;
    }

    /**
     * @param savedRecipe SavedRecipe object to be saved
     * @return SavedRecipe object that just has been saved
     */
    protected SavedRecipe save(SavedRecipe savedRecipe) {
        LOGGER.debug("Saving SavedRecipe");
        return repository.save(savedRecipe);
    }

    /**
     * Deletes SavedRecipe object only if the value of user's username of the argument is the same as current principal's username
     *
     * @param savedRecipe SavedRecipe object to be deleted
     */
    @PreAuthorize("#savedRecipe.user.username == authentication.principal.username")
    protected void delete(SavedRecipe savedRecipe) {
        LOGGER.debug("Deleting savedRecipe");
        repository.delete(savedRecipe);
    }

    /**
     * @param id id of wanted SavedRecipe object
     * @return proper SavedRecipe object, if it does't exist this method throws new ResourceNotFoundException
     */
    protected SavedRecipe getOneById(Long id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * @param pageable PageRequest object that should contain data about wanted page number and size
     * @return wanted Page of Saved recipes
     */
    protected Page<SavedRecipe> findUsersSavedRecipes(String username, Pageable pageable) {
        return repository.findByUserUsername(username, pageable);
    }

}
