package com.recipecommunity.features.user;

import com.recipecommunity.features.recipe.RecipeController;
import com.recipecommunity.features.saved_recipe.SavedRecipeController;
import com.recipecommunity.features.utils.exception.PageDoesNotExist;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller class for user feature. Enables client to get all users, currently logged in user and user with given id.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    /**
     * Returns wanted Page of Users and proper Links inside ResponseEntity
     *
     * @param pageNumber  number of wanted page
     * @param userDetails object that contains current user data
     * @return ResponseEntity with ok status and a CollectionModel instance with Page of Users and Links as a body
     */
    @GetMapping
    public ResponseEntity<CollectionModel<User>> getUsers(
            @RequestParam(value = "page", required = false, defaultValue = "0")
                    int pageNumber, @AuthenticationPrincipal UserDetails userDetails) {
        Pageable pageable = PageRequest.of(pageNumber, 10);
        CollectionModel<User> result = null;
        Page<User> page = userService.findAll(pageable);
        int pages = page.getTotalPages();
        for (User user : page
        ) {
            if (user.hasLink("self"))
                continue;
            user.add(linkTo(methodOn(UserController.class)
                    .getUserById(user.getId(), userDetails)).withSelfRel());
        }
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(UserController.class).getCurrentUser(userDetails)).withRel("me"));
        if (pageNumber > 0) {
            links.add(linkTo(methodOn(UserController.class).getUsers(pageNumber - 1, userDetails)).withRel("previous"));
        }
        if (pageNumber + 2 <= pages) {
            links.add(linkTo(methodOn(UserController.class).getUsers(pageNumber + 1, userDetails)).withRel("next"));
        }
        if (pageNumber >= pages && pageNumber != 0) {
            LOGGER.debug("Wanted page does not exist");
            throw new PageDoesNotExist();
        }
        LOGGER.debug("Getting users");
        result = new CollectionModel<>(page, links);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Returns User object that represents a user with given id inside ResponseEntity body.
     * User is retrieved using UserService instance, then this method adds proper Links.
     *
     * @param id of a wanted user
     * @return ResponseEntity with ok status and a proper user object as a body
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUserById(id);
        user.add(linkTo(methodOn(UserController.class).getUserById(id, userDetails)).withSelfRel());
        user.add(linkTo(methodOn(RecipeController.class).getRecipesCreatedByGivenUser(0, user.getUsername(), userDetails))
                .withRel("Recipes created by this user"));
        LOGGER.debug("Getting user");
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    /**
     * Returns User object that represents a currently logged in user inside ResponseEntity body.
     * User is retrieved using UserService instance, then this method adds proper Links.
     *
     * @param userDetails object that contains current user data
     * @return ResponseEntity with ok status and current user as a body
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User me = userService.findUserByUsername(userDetails.getUsername());
        me.add(linkTo(methodOn(UserController.class).getCurrentUser(userDetails)).withSelfRel());
        me.add(linkTo(methodOn(SavedRecipeController.class).getSavedRecipes(0, userDetails)).withRel("My saved recipes"));
        me.add(linkTo(methodOn(RecipeController.class).getCurrentUsersRecipes(0, userDetails)).withRel("My recipes"));
        return ResponseEntity.status(HttpStatus.OK).body(me);
    }
}
