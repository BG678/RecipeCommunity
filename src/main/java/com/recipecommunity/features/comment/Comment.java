package com.recipecommunity.features.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.recipecommunity.features.recipe.Recipe;
import com.recipecommunity.features.user.User;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * POJO class that represents entity Comment
 *
 * @author Barbara Grabowska
 * @version %I%
 */
@Entity
@Table(name = "comment")
public class Comment extends RepresentationModel<Comment> implements Serializable {
    private static final long serialVersionUID = 123452L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String text;
    @NotNull
    private LocalDate date;
    @JsonIgnore
    @NotNull
    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "comment_author_id", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        Comment comment = (Comment) o;
        return getId().equals(comment.getId()) &&
                getText().equals(comment.getText()) &&
                getDate().equals(comment.getDate()) &&
                getRecipe().equals(comment.getRecipe()) &&
                getUser().equals(comment.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getText(), getDate(), getRecipe(), getUser());
    }
}
