package com.recipecommunity.features.saved_recipe;

import com.recipecommunity.features.recipe.Recipe;
import com.recipecommunity.features.user.User;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me/saved-recipes")
public class SavedRecipeController {

    @GetMapping
    public ResponseEntity<CollectionModel<SavedRecipe>> getSavedRecipes(
            @RequestParam(value = "page", required = false, defaultValue = "0")
                    int pageNumber, @AuthenticationPrincipal UserDetails userDetails) {
        return null;
    }

    @PostMapping
    public ResponseEntity<SavedRecipe> save(@RequestBody SavedRecipeRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavedRecipe> getSavedRecipeById(@PathVariable Long id) {
        //access!
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSavedRecipe(@PathVariable Long id) {
        //Access!!!
        return null;
    }
}
