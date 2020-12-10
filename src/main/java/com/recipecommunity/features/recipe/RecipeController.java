package com.recipecommunity.features.recipe;

import com.recipecommunity.features.saved_recipe.SavedRecipeController;
import com.recipecommunity.features.saved_recipe.SavedRecipeRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @GetMapping
    public ResponseEntity<CollectionModel<Recipe>> getRecipes(
            @RequestParam(value = "page", required = false, defaultValue = "0") int pageNumber){
        return null;
    }
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getOne(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
      return null;
    }
    @GetMapping("/my")
    public ResponseEntity<CollectionModel<Recipe>> getCurrentUsersRecipes(
            @RequestParam(value = "page", required = false, defaultValue = "0")
                    int pageNumber, @AuthenticationPrincipal UserDetails userDetails){
        return null;
    }
    @PostMapping("/my")
    public ResponseEntity<Recipe> saveNewRecipe(
            @RequestBody RecipeRequest recipeRequest,
            @AuthenticationPrincipal UserDetails userDetails){
        return null;
    }
    @DeleteMapping("/my/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        //access
        return null;
    }
    @PutMapping("/my/{id}")
    public ResponseEntity<Recipe> editRecipe(
            @PathVariable Long id, @RequestBody RecipeRequest recipeRequest,
            @AuthenticationPrincipal UserDetails userDetails){
        //access
        return null;
    }
    @GetMapping("/created-by-{username}")
    public ResponseEntity<CollectionModel<Recipe>> getRecipesCreatedByGiveUser(
            @RequestParam(value = "page", required = false, defaultValue = "0") int pageNumber,
            @PathVariable String username){
        return null;
    }

}
