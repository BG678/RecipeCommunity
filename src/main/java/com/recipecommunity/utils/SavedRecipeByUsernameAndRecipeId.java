package com.recipecommunity.utils;

import com.recipecommunity.features.saved_recipe.SavedRecipe;

public interface SavedRecipeByUsernameAndRecipeId {
    SavedRecipe findByUserUsernameAndRecipeId(String username, Long id);

}
