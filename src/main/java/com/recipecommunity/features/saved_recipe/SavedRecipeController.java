package com.recipecommunity.features.saved_recipe;

import com.recipecommunity.features.recipe.Recipe;
import com.recipecommunity.features.recipe.RecipeController;
import com.recipecommunity.features.user.UserController;
import com.recipecommunity.features.utils.exception.PageDoesNotExist;
import com.recipecommunity.features.utils.UserByUsername;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller class for savedRecipe feature. Enables client to get all currently logged in user's savedRecipes,
 * save a new SavedRecipe object, delete savedRecipe or get one by id.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@RestController
@RequestMapping("/api/users/me/saved-recipes")
public class SavedRecipeController {
    private final SavedRecipeService service;
    private final UserByUsername userByUsername;
    private final Logger LOGGER = LoggerFactory.getLogger(SavedRecipeController.class);

    @Autowired
    public SavedRecipeController(SavedRecipeService service, UserByUsername userByUsername) {
        this.service = service;
        this.userByUsername = userByUsername;
    }

    /**
     * Returns wanted Page of savedRecipes that belong to currently logged in user and proper Links inside ResponseEntity object
     *
     * @param pageNumber  number of wanted page
     * @param userDetails object that contains current user data
     * @return ResponseEntity with ok status and a CollectionModel instance with Page of SavedRecipes and Links as a body
     */
    @GetMapping
    public ResponseEntity<CollectionModel<SavedRecipe>> getSavedRecipes(
            @RequestParam(value = "page", required = false, defaultValue = "0")
                    int pageNumber, @AuthenticationPrincipal UserDetails userDetails) {
        Page<SavedRecipe> page = service.findUsersSavedRecipes(userDetails.getUsername(), PageRequest.of(pageNumber, 10));
        int pages = page.getTotalPages();
        CollectionModel<SavedRecipe> result;
        for (SavedRecipe savedRecipe : page) {
            if (!savedRecipe.hasLinks()) {
                addLinks(savedRecipe, userDetails);
            }
        }
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(UserController.class).getCurrentUser(userDetails)).withRel("me"));
        links.add(linkTo(methodOn(SavedRecipeController.class).getSavedRecipes(pageNumber, userDetails)).withSelfRel());
        if (pageNumber > 0) {
            links.add(linkTo(methodOn(SavedRecipeController.class).getSavedRecipes(pageNumber - 1, userDetails))
                    .withRel("previous"));
        }
        if (pageNumber + 2 <= pages) {
            links.add(linkTo(methodOn(SavedRecipeController.class).getSavedRecipes(pageNumber + 1, userDetails))
                    .withRel("next"));
        }
        if (pageNumber >= pages && pageNumber != 0) {
            LOGGER.debug("Wanted page does not exist");
            throw new PageDoesNotExist();
        }
        LOGGER.debug("Getting saved recipes");
        result = new CollectionModel<>(page, links);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Saves a new SavedRecipe object with proper data using values of the arguments that are passed in.
     *
     * @param request     SavedRecipeRequest object that contains id of a recipe that will be set as savedRecipe's recipe
     * @param userDetails object that contains current user data
     * @return ResponseEntity with created status and a SavedRecipe object that just has been saved
     */
    @PostMapping
    public ResponseEntity<SavedRecipe> save(
            @Valid @RequestBody SavedRecipeRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        SavedRecipe savedRecipe = new SavedRecipe();
        savedRecipe.setUser(userByUsername.findUserByUsername(userDetails.getUsername()));
        savedRecipe.setRecipe(new Recipe(request.getRecipeToBeSaveId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(savedRecipe)
                .add(linkTo(methodOn(SavedRecipeController.class).getSavedRecipes(0, userDetails)).withRel("saved recipes")));
    }

    /**
     * Returns SavedRecipe object that represents a savedRecipe with given id inside ResponseEntity body, but only if if the
     * value of returnObject's user's username is the same as current principal's username.
     * SavedRecipe is retrieved using SavedRecipeService instance and this method calls addLinks method to add proper Links.
     *
     * @param id of a wanted savedRecipe
     * @return ResponseEntity with ok status and a proper SavedRecipe object as a body
     */
    @PostAuthorize("returnObject.body.user.username == authentication.principal.username")
    @GetMapping("/{id}")
    public ResponseEntity<SavedRecipe> getSavedRecipeById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(addLinks(service.getOneById(id), userDetails));
    }

    /**
     * Deletes a SavedRecipe object with given id, using service instance, returns no content status.
     *
     * @param id of a savedRecipe that will be deleted
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteSavedRecipe(@PathVariable Long id) {
        service.delete(service.getOneById(id));
    }

    /**
     * Adds proper Links to an argument and returns it.
     *
     * @param savedRecipe SavedRecipe object
     * @return SavedRecipe object that was passed to this method, but with added Links
     */
    protected SavedRecipe addLinks(SavedRecipe savedRecipe, UserDetails userDetails) {
        savedRecipe.add(linkTo(methodOn(SavedRecipeController.class)
                .getSavedRecipeById(savedRecipe.getId(), userDetails)).withSelfRel());
        savedRecipe.add(linkTo(methodOn(RecipeController.class).getOne(savedRecipe.getRecipe().getId(), userDetails)).withRel("recipe"));
        savedRecipe.add(linkTo(methodOn(UserController.class).
                getUserById(savedRecipe.getRecipe().getAuthor().getId(), userDetails)).withRel("author"));
        LOGGER.debug("Adding links to " + savedRecipe.getId());
        return savedRecipe;
    }
}
