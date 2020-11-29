package com.recipecommunity.features.saved_recipe;

import com.recipecommunity.features.recipe.Recipe;
import com.recipecommunity.features.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "saved_recipe")
public class SavedRecipe implements Serializable {
    @EmbeddedId
    SavedRecipeKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "saved_by_user_id")
    User user;

    @ManyToOne
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id")
    Recipe recipe;

    public SavedRecipeKey getId() {
        return id;
    }

    public void setId(SavedRecipeKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavedRecipe)) return false;
        SavedRecipe that = (SavedRecipe) o;
        return getId().equals(that.getId()) &&
                getUser().equals(that.getUser()) &&
                getRecipe().equals(that.getRecipe());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser(), getRecipe());
    }
}
