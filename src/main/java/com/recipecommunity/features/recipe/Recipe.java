package com.recipecommunity.features.recipe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.recipecommunity.features.saved_recipe.SavedRecipe;
import com.recipecommunity.features.comment.Comment;
import com.recipecommunity.features.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * POJO class that represents entity Recipe
 *
 * @author Barbara Grabowska
 * @version %I%
 */
@Entity
@Table(name = "recipe")
public class Recipe implements Serializable {
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
    @OneToMany(mappedBy = "recipe")
    private Set<Comment> comments;
    @JsonIgnore
    @OneToMany(mappedBy = "recipe")
    private Set<SavedRecipe> savedRecipes;

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

    public Set<SavedRecipe> getSavedRecipes() {
        return savedRecipes;
    }

    public void setSavedRecipes(Set<SavedRecipe> savedRecipes) {
        this.savedRecipes = savedRecipes;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe)) return false;
        Recipe recipe = (Recipe) o;
        return getId().equals(recipe.getId()) &&
                getTitle().equals(recipe.getTitle()) &&
                getText().equals(recipe.getText()) &&
                getAuthor().equals(recipe.getAuthor()) &&
                getComments().equals(recipe.getComments()) &&
                getSavedRecipes().equals(recipe.getSavedRecipes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getText(), getAuthor(), getComments(), getSavedRecipes());
    }

}
