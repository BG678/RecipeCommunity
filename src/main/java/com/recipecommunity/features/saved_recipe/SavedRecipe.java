package com.recipecommunity.features.saved_recipe;

import com.recipecommunity.features.recipe.Recipe;
import com.recipecommunity.features.user.User;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "saved_recipe")
public class SavedRecipe extends RepresentationModel<SavedRecipe> implements Serializable {
    private static final long serialVersionUID = 1234544L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "saved_by_user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;


    public SavedRecipe() {
    }

    public SavedRecipe(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return getUser().equals(that.getUser()) &&
                getRecipe().equals(that.getRecipe());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser(), getRecipe());
    }
}
