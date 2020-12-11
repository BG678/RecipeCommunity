package com.recipecommunity.features.user;

import com.recipecommunity.features.recipe.RecipeController;
import com.recipecommunity.features.saved_recipe.SavedRecipeController;
import com.recipecommunity.utils.PageDoesNotExist;
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

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns wanted Page of Users and proper Links inside ResponseEntity
     *
     * @param pageNumber number of wanted page
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
                    .getUserById(user.getId())).withSelfRel());
        }
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(UserController.class).getCurrentUser(userDetails)).withRel("me"));
        if (pageNumber > 0) {
            Link link2 = linkTo(methodOn(UserController.class).getUsers(pageNumber - 1, userDetails)).withRel("previous");
            links.add(link2);
        }
        if (pageNumber + 2 <= pages) {
            Link link = linkTo(methodOn(UserController.class).getUsers(pageNumber + 1, userDetails)).withRel("next");
            links.add(link);
        }
        if (pageNumber >= pages) {
            throw new PageDoesNotExist();
        }
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
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        user.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        user.add(linkTo(methodOn(RecipeController.class).getRecipesCreatedByGiveUser(0, user.getUsername()))
                .withRel("Recipes created by this user"));
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    /**
     * Returns User object that represents a current logged in user inside ResponseEntity body.
     * User is retrieved using UserService instance, then this method adds proper Links.
     *
     * @param userDetails object that contains current user data
     * @return ResponseEntity with ok status and current user as a body
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User me = userService.findUserByUsername(userDetails.getUsername());
        me.add(linkTo(methodOn(SavedRecipeController.class).getSavedRecipes(0, userDetails)).withRel("My saved recipes"));
        me.add(linkTo(methodOn(RecipeController.class).getCurrentUsersRecipes(0, userDetails)).withRel("My recipes"));
        me.add(linkTo(methodOn(UserController.class).getCurrentUser(userDetails)).withSelfRel());
        return ResponseEntity.status(HttpStatus.OK).body(me);
    }
}
