package com.recipecommunity.features.saved_recipe;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
/**
 * POJO class that contains payload required to create a new savedRecipe object
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
public class SavedRecipeRequest implements Serializable {
    private static final long serialVersionUID = 12345434344L;
    @NotNull
    @Positive
    private Long recipeToBeSavedId;

    public SavedRecipeRequest() {
    }

    public SavedRecipeRequest(@NotBlank @Positive Long recipeToBeSavedId) {
        this.recipeToBeSavedId = recipeToBeSavedId;
    }

    public Long getRecipeToBeSavedId() {
        return recipeToBeSavedId;
    }

    public void setRecipeToBeSavedId(Long recipeToBeSavedId) {
        this.recipeToBeSavedId = recipeToBeSavedId;
    }
}
