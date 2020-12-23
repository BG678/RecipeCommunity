package com.recipecommunity.features.comment;

import com.recipecommunity.features.recipe.Recipe;
import com.recipecommunity.features.recipe.RecipeController;
import com.recipecommunity.features.user.UserController;
import com.recipecommunity.utils.PageDoesNotExist;
import com.recipecommunity.utils.UserByUsername;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller class for comment feature. Enables client to get all comments that belong to a recipe with given id,
 * save a new Comment object, delete comment or get one by recipe id and comment id.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@RestController
@RequestMapping("/api/recipes/{recipeId}/comments")
public class CommentController {
    private final CommentService service;
    private final UserByUsername userByUsername;
    private final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    public CommentController(CommentService service, UserByUsername userByUsername) {
        this.service = service;
        this.userByUsername = userByUsername;
    }

    /**
     * Returns wanted Page of comments that belong to a given recipe and proper Links inside ResponseEntity object
     *
     * @param pageNumber number of wanted page
     * @param recipeId   id of a recipe that comments belong to
     * @return ResponseEntity with ok status and a CollectionModel instance with Page of Comments and Links as a body
     */
    @GetMapping
    public ResponseEntity<CollectionModel<Comment>> getComments(
            @RequestParam(value = "page", required = false, defaultValue = "0")
                    int pageNumber, @PathVariable Long recipeId) {
        Page<Comment> page = service.findCommentsByRecipeId(recipeId, PageRequest.of(pageNumber, 10));
        int pages = page.getTotalPages();
        CollectionModel<Comment> result;
        for (Comment comment : page) {
            if (!comment.hasLinks()) {
                addLinks(comment);
            }
        }
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(RecipeController.class).getOne(recipeId)).withRel("recipe"));
        links.add(linkTo(methodOn(CommentController.class).getComments(pageNumber, recipeId)).withSelfRel());
        if (pageNumber > 0) {
            Link link2 = linkTo(methodOn(CommentController.class).getComments(pageNumber - 1, recipeId)).withRel("previous");
            links.add(link2);
        }
        if (pageNumber + 2 <= pages) {
            Link link = linkTo(methodOn(CommentController.class).getComments(pageNumber + 1, recipeId)).withRel("next");
            links.add(link);
        }
        if (pageNumber >= pages) {
            LOGGER.debug("Wanted page does not exist");
            throw new PageDoesNotExist();
        }
        LOGGER.debug("Getting users");
        result = new CollectionModel<>(page, links);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Saves a new Comment object with proper data using values of the arguments that are passed in.
     *
     * @param commentRequest object that contains text which will be set as a comment's text
     * @param recipeId       id of a recipe that comments belong to
     * @param userDetails    object that contains current user data
     * @return ResponseEntity with created status and a Comment object that just has been saved
     */
    @PostMapping
    public ResponseEntity<Comment> saveNewComment(
            @Valid @RequestBody CommentRequest commentRequest, @PathVariable Long recipeId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Comment comment = new Comment();
        comment.setRecipe(new Recipe(recipeId));
        comment.setUser(userByUsername.findUserByUsername(userDetails.getUsername()));
        comment.setText(commentRequest.getText());
        comment.setDate(LocalDate.now());
        LOGGER.debug("Saving a new comment");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveComment(comment)
                .add(linkTo(methodOn(CommentController.class).getComments(0, recipeId)).withRel("comments")));
    }

    /**
     * Returns Comment object that represents a comment with given id, which belongs to a given recipe inside ResponseEntity body.
     * Comment is retrieved using CommentService instance and this method calls addLinks method to add proper Links.
     *
     * @param id of a wanted Comment object
     * @return ResponseEntity with ok status and a proper Comment object as a body
     */
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(
            @PathVariable Long recipeId, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(addLinks(service.getOneById(id, recipeId)));
    }

    /**
     * Deletes a Comment object with given id, using CommentService instance, returns no content status.
     *
     * @param id of a comment that will be deleted
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteComment(
            @PathVariable Long recipeId, @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        LOGGER.debug("Deleting comment");
        service.delete(service.getOneById(id, recipeId));
    }

    /**
     * Adds proper Links to an argument and returns it.
     *
     * @param comment Comment object
     * @return Comment object that was passed to this method, but with added Links
     */
    protected Comment addLinks(Comment comment) {
        comment.add(linkTo(methodOn(UserController.class).getUserById(comment.getUser().getId())).withRel("author"));
        comment.add(linkTo(methodOn(CommentController.class).getCommentById(comment.getRecipe().getId(), comment.getId())).withSelfRel());
        return comment;
    }
}
