package com.recipecommunity.features.recipe;

import com.recipecommunity.features.comment.CommentController;
import com.recipecommunity.features.saved_recipe.SavedRecipe;
import com.recipecommunity.features.saved_recipe.SavedRecipeController;
import com.recipecommunity.features.saved_recipe.SavedRecipeRequest;
import com.recipecommunity.features.user.UserController;
import com.recipecommunity.features.utils.exception.PageDoesNotExist;
import com.recipecommunity.features.utils.SavedRecipeByUsernameAndRecipeId;
import com.recipecommunity.features.utils.UserByUsername;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
 * Controller class for a recipe feature. Enables client to: get all recipes, get recipes that belong to a given user,
 * get, edit and delete recipes added by a currently logged in user and add new recipes.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService service;
    private final UserByUsername userByUsername;
    private final SavedRecipeByUsernameAndRecipeId byUsernameAndRecipeId;
    private final Logger LOGGER = LoggerFactory.getLogger(RecipeController.class);

    @Autowired
    public RecipeController(RecipeService service, UserByUsername userByUsername, SavedRecipeByUsernameAndRecipeId byUsernameAndRecipeId) {
        this.service = service;
        this.userByUsername = userByUsername;
        this.byUsernameAndRecipeId = byUsernameAndRecipeId;
    }

    /**
     * If RequestParam search isn't present returns ResponseEntity object with wanted Page of recipes and proper Links.
     * Otherwise this method filters recipes and returns wanted Page of recipes that have titles containing value of a
     * search parameter inside ResponseEntity object, also with proper Links.
     *
     * @param pageNumber  number of wanted page
     * @param userDetails object that contains current user data
     * @param search      represents a request parameter value that enables client to search for a particular recipe by title
     * @return ResponseEntity with ok status and a CollectionModel instance with Page of Recipes and Links as a body
     */
    @GetMapping
    public ResponseEntity<CollectionModel<Recipe>> getRecipes(
            @RequestParam(value = "page", required = false, defaultValue = "0") int pageNumber,
            @AuthenticationPrincipal UserDetails userDetails, @RequestParam(value = "search", required = false) String search) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        Page<Recipe> page;
        if (search != null) {
            page = service.findByTitle(search, pageable);
        } else {
            page = service.getAllRecipes(pageable);
        }
        int pages = page.getTotalPages();
        CollectionModel<Recipe> result;
        for (Recipe recipe : page) {
            if (!recipe.hasLinks()) {
                addLinks(recipe, userDetails);
            }
        }
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(RecipeController.class).getRecipes(pageNumber, userDetails, search)).withSelfRel());
        if (pageNumber > 0) {
            Link link2 = linkTo(methodOn(RecipeController.class).getRecipes(pageNumber - 1, userDetails, search))
                    .withRel("previous");
            links.add(link2);
        }
        if (pageNumber + 2 <= pages) {
            Link link = linkTo(methodOn(RecipeController.class).getRecipes(pageNumber + 1, userDetails, search))
                    .withRel("next");
            links.add(link);
        }
        if (pageNumber >= pages && pageNumber != 0) {
            LOGGER.debug("Wanted page does not exist");
            throw new PageDoesNotExist();
        }
        LOGGER.debug("Getting recipes");
        result = new CollectionModel<>(page, links);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Returns a recipe with given id inside ResponseEntity body, adds proper links to the recipe.
     *
     * @param id of a wanted recipe
     * @return ResponseEntity with ok status and a proper Recipe object as a body
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getOne(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Recipe recipe = service.getOneById(id);
        SavedRecipe savedRecipe = byUsernameAndRecipeId.findByUserUsernameAndRecipeId(userDetails.getUsername(), id);
        if (savedRecipe != null) {
            recipe.add(linkTo(methodOn(SavedRecipeController.class).getSavedRecipeById(savedRecipe.getId(), userDetails))
                    .withRel("Saved recipe"));
        } else {
            recipe.add(linkTo(methodOn(SavedRecipeController.class).save(new SavedRecipeRequest(recipe.getId()), userDetails))
                    .withRel("Save recipe"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(addLinks(recipe, userDetails));
    }

    /**
     * Returns a proper Page of Recipes that were added by currently logged in user inside ResponseEntity object, adds proper links.
     *
     * @param pageNumber  number of wanted page
     * @param userDetails object that contains current user data
     * @return ResponseEntity with ok status and a CollectionModel instance with Page of current user's Recipes as a body
     */
    @GetMapping("/my")
    public ResponseEntity<CollectionModel<Recipe>> getCurrentUsersRecipes(
            @RequestParam(value = "page", required = false, defaultValue = "0")
                    int pageNumber, @AuthenticationPrincipal UserDetails userDetails) {
        Page<Recipe> page = service.findRecipesByUsername(userDetails.getUsername(), PageRequest.of(pageNumber, 10));
        int pages = page.getTotalPages();
        CollectionModel<Recipe> result;
        for (Recipe recipe : page) {
            if (!recipe.hasLinks()) {
                recipe.add(linkTo(methodOn(RecipeController.class).getMyRecipeById(recipe.getId(), userDetails)).withRel("my recipe"));
            }
        }
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(RecipeController.class).getCurrentUsersRecipes(pageNumber, userDetails)).withSelfRel());
        if (pageNumber > 0) {
            links.add(linkTo(methodOn(RecipeController.class).getCurrentUsersRecipes(pageNumber - 1, userDetails))
                    .withRel("previous"));
        }
        if (pageNumber + 2 <= pages) {
            links.add(linkTo(methodOn(RecipeController.class).getCurrentUsersRecipes(pageNumber + 1, userDetails))
                    .withRel("next"));
        }
        if (pageNumber >= pages && pageNumber != 0) {
            LOGGER.debug("Wanted page does not exist");
            throw new PageDoesNotExist();
        }
        LOGGER.debug("Getting recipes");
        result = new CollectionModel<>(page, links);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Saves a new Recipe object with proper data using save method.
     *
     * @param recipeRequest object that contains text and title that will be set as a recipe's text and title
     * @param userDetails   object that contains current user data
     * @return ResponseEntity with created status and a Recipe object that just has been saved
     */
    @PostMapping("/my")
    public ResponseEntity<Recipe> saveNewRecipe(@Valid @RequestBody RecipeRequest recipeRequest,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(save(recipeRequest, userDetails, null));
    }

    /**
     * Returns a recipe with a given id, but only if that recipe was created by currently logged in user.
     * Adds proper Links to the Recipe object.
     *
     * @param id of a wanted recipe
     * @return ResponseEntity with ok status and a proper Recipe object as a body
     */
    @PostAuthorize("returnObject.body.author.username == authentication.principal.username")
    @GetMapping("/my/{id}")
    public ResponseEntity<Recipe> getMyRecipeById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getOneById(id)
                .add(linkTo(methodOn(RecipeController.class).getMyRecipeById(id, userDetails)).withSelfRel())
                .add(linkTo(methodOn(RecipeController.class).getCurrentUsersRecipes(0, userDetails)).withRel("my recipes")));
    }

    /**
     * Deletes a Recipe object with given id, using RecipeService instance, returns no content status.
     *
     * @param id of a recipe that will be deleted
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/my/{id}")
    public void deleteRecipe(@PathVariable Long id) {
        service.delete(service.getOneById(id));
    }

    /**
     * Updates a recipe with given id using save method.
     *
     * @param id            of a recipe that will be updated
     * @param recipeRequest contains data, that will be set as an updating recipe's title and text
     * @param userDetails   object that contains current user data
     * @return Recipe object that has just been saved
     */
    @PutMapping("/my/{id}")
    public ResponseEntity<Recipe> editRecipe(
            @PathVariable Long id, @Valid @RequestBody RecipeRequest recipeRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(save(recipeRequest, userDetails, id));
    }

    /**
     * Returns a proper Page of Recipes that were added by a user with username that equals to a path variable parameter - username.
     * Adds proper links.
     *
     * @param pageNumber  number of wanted page
     * @param username    method parameter that is bound to a URI template variable
     * @param userDetails object that contains current user data
     * @return ResponseEntity with ok status and a CollectionModel instance with Page of a given user's Recipes as a body
     */
    @GetMapping("/created-by-{username}")
    public ResponseEntity<CollectionModel<Recipe>> getRecipesCreatedByGivenUser(
            @RequestParam(value = "page", required = false, defaultValue = "0") int pageNumber,
            @PathVariable String username, @AuthenticationPrincipal UserDetails userDetails) {
        Page<Recipe> page = service.findRecipesByUsername(username, PageRequest.of(pageNumber, 10));
        int pages = page.getTotalPages();
        CollectionModel<Recipe> result;
        for (Recipe recipe : page) {
            if (!recipe.hasLinks()) {
                addLinks(recipe, userDetails);
            }
        }
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(RecipeController.class).getRecipesCreatedByGivenUser(pageNumber, username, userDetails)).withSelfRel());
        if (pageNumber > 0) {
            links.add(linkTo(methodOn(RecipeController.class).getRecipesCreatedByGivenUser
                    (pageNumber - 1, username, userDetails)).withRel("previous"));
        }
        if (pageNumber + 2 <= pages) {
            links.add(linkTo(methodOn(RecipeController.class).getRecipesCreatedByGivenUser
                    (pageNumber + 1, username, userDetails)).withRel("next"));
        }
        if (pageNumber >= pages && pageNumber != 0) {
            LOGGER.debug("Wanted page does not exist");
            throw new PageDoesNotExist();
        }
        LOGGER.debug("Getting recipes");
        result = new CollectionModel<>(page, links);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Saves a new Recipe object or updates already existing one, using values of the arguments that are passed in.
     * Adds links.
     *
     * @param recipeRequest contains title and text that will be set
     * @param userDetails   object that contains current user's data
     * @param id            of a recipe that will be updated. When saving a new recipe this parameter should be null
     * @return Recipe object that just have been saved.
     */
    private Recipe save(RecipeRequest recipeRequest, UserDetails userDetails, Long id) {
        Recipe recipe = new Recipe();
        if (id != null) {
            recipe.setId(id);
        }
        recipe.setText(recipeRequest.getText());
        recipe.setTitle(recipeRequest.getTitle());
        recipe.setAuthor(userByUsername.findUserByUsername(userDetails.getUsername()));
        return service.saveRecipe(recipe)
                .add(linkTo(methodOn(RecipeController.class).getRecipes(0, userDetails, null)).withRel("recipes"));
    }

    /**
     * Adds proper Links to an argument and returns it.
     *
     * @param recipe      Recipe object
     * @param userDetails object that contains current user data
     * @return Recipe object that was passed to this method, but with added links
     */
    protected Recipe addLinks(Recipe recipe, UserDetails userDetails) {
        recipe.add(linkTo(methodOn(RecipeController.class).getOne(recipe.getId(), userDetails)).withSelfRel());
        recipe.add(linkTo(methodOn(UserController.class).getUserById(recipe.getAuthor().getId(), userDetails)).withRel("author"));
        recipe.add(linkTo(methodOn(CommentController.class).getComments(0, recipe.getId(), userDetails)).withRel("comments"));
        LOGGER.debug("Adding links to " + recipe.getId());
        return recipe;
    }
}
