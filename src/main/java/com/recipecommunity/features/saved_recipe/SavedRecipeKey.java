package com.recipecommunity.features.saved_recipe;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SavedRecipeKey implements Serializable {

    @Column(name = "saved_by_user_id")
    Long userId;

    @Column(name = "recipe_id")
    Long recipeId;

    public SavedRecipeKey() {
    }

    public SavedRecipeKey(Long userId, Long recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavedRecipeKey)) return false;
        SavedRecipeKey that = (SavedRecipeKey) o;
        return getUserId().equals(that.getUserId()) &&
                getRecipeId().equals(that.getRecipeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getRecipeId());
    }
}
