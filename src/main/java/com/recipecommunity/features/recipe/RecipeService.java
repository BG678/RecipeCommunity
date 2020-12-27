package com.recipecommunity.features.recipe;

import com.recipecommunity.features.utils.exception.ResourceNotFoundException;
import com.recipecommunity.features.utils.UserByUsername;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Service class for recipe feature.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@Service
public class RecipeService {
    @Autowired
    private RecipeRepository repository;
    private UserByUsername userByUsername;
    private final Logger LOGGER = LoggerFactory.getLogger(RecipeService.class);

    /**
     * Deletes Recipe object only if the value of authors's username of the argument is the same as current principal's username.
     *
     * @param recipe Recipe object to be deleted
     */
    @PreAuthorize("#recipe.author.username == authentication.principal.username")
    protected void delete(Recipe recipe) {
        LOGGER.debug("Deleting recipe");
        repository.delete(recipe);
    }

    /**
     * @param id id of a wanted recipe
     * @return proper Recipe object, if it does't exist this method throws new ResourceNotFoundException
     */
    protected Recipe getOneById(Long id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    /**
     * Saves a recipe, but only if the value of authors's username of the argument is the same as current principal's username.
     *
     * @param recipe Recipe object that will be saved
     * @return Recipe object that just has been saved
     */
    @PreAuthorize("#recipe.author.username == authentication.principal.username")
    protected Recipe saveRecipe(Recipe recipe) {
        return repository.save(recipe);
    }

    /**
     * Returns a Page of recipes that were created by an author with a given username.
     *
     * @param username represents a username of an author
     * @param pageable ageRequest object that should contain data about wanted page number and size
     * @return wanted Page of Recipe objects created by author with a given username
     */
    protected Page<Recipe> findRecipesByUsername(String username, Pageable pageable) {
        return repository.findByAuthorUsername(username, pageable);
    }

    /**
     * @param title    of a recipe that client is looking for
     * @param pageable PageRequest object that should contain data about wanted page number and size
     * @return wanted Page of Recipe objects with titles containing title argument value
     */
    protected Page<Recipe> findByTitle(String title, Pageable pageable) {
        return repository.findAllByTitleContaining(title, pageable);
    }

    /**
     * @return a wanted Page of recipes
     */
    protected Page<Recipe> getAllRecipes(Pageable pageable) {
        return repository.findAll(pageable);
    }


}
