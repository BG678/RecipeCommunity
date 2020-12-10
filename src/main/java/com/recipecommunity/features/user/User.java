package com.recipecommunity.features.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipecommunity.features.comment.Comment;
import com.recipecommunity.features.recipe.Recipe;
import com.recipecommunity.features.saved_recipe.SavedRecipe;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * POJO class that represents entity User
 *
 * @author Barbara Grabowska
 * @version %I%
 */
@Entity
@Table(name = "user")
public class User extends RepresentationModel<User> implements Serializable {
    private static final long serialVersionUID = 123451L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private Set<Recipe> createdRecipes;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<Comment> comments;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<SavedRecipe> savedRecipes;

    public User() {

    }

    public User(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Recipe> getCreatedRecipes() {
        return createdRecipes;
    }

    public void setCreatedRecipes(Set<Recipe> createdRecipes) {
        this.createdRecipes = createdRecipes;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<SavedRecipe> getSavedRecipes() {
        return savedRecipes;
    }

    public void setSavedRecipes(Set<SavedRecipe> savedRecipes) {
        this.savedRecipes = savedRecipes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                username.equals(user.username) &&
                password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
