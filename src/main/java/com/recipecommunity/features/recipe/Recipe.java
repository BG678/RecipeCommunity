package com.recipecommunity.features.recipe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.recipecommunity.features.saved_recipe.SavedRecipe;
import com.recipecommunity.features.comment.Comment;
import com.recipecommunity.features.user.User;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * POJO class that represents entity Recipe
 *
 * @author Barbara Grabowska
 * @version %I%
 */
@Entity
@Table(name = "recipe")
public class Recipe extends RepresentationModel<Recipe> implements Serializable {
    private static final long serialVersionUID = 123453L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String text;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    @JsonIgnore
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    @JsonIgnore
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedRecipe> savedRecipes;

    public Recipe() {
    }

    public Recipe(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<SavedRecipe> getSavedRecipes() {
        return savedRecipes;
    }

    public void setSavedRecipes(List<SavedRecipe> savedRecipes) {
        this.savedRecipes = savedRecipes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe)) return false;
        Recipe recipe = (Recipe) o;
        if (getId() != null && recipe.getId() != null) {
            return getId().equals(recipe.getId());
        } else {
            return getTitle().equals(recipe.getTitle()) &&
                    getText().equals(recipe.getText()) &&
                    getAuthor().equals(recipe.getAuthor());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getText(), getAuthor(), getComments(), getSavedRecipes());
    }

}
