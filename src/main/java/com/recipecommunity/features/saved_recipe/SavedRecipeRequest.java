package com.recipecommunity.features.saved_recipe;

import javax.validation.constraints.NotBlank;
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
    @NotBlank
    @Positive
    private Long recipeToBeSaveId;

    public SavedRecipeRequest() {
    }

    public SavedRecipeRequest(@NotBlank @Positive Long recipeToBeSaveId) {
        this.recipeToBeSaveId = recipeToBeSaveId;
    }

    public Long getRecipeToBeSaveId() {
        return recipeToBeSaveId;
    }

    public void setRecipeToBeSaveId(Long recipeToBeSaveId) {
        this.recipeToBeSaveId = recipeToBeSaveId;
    }
}
