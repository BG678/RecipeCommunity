package com.recipecommunity.features.comment;

import com.recipecommunity.features.utils.exception.ResourceNotFoundException;
import com.recipecommunity.features.utils.UserByUsername;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Service class for Comment feature.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@Service
public class CommentService {
    @Autowired
    private CommentRepository repository;
    @Autowired
    private UserByUsername userByUsername;
    private final Logger LOGGER = LoggerFactory.getLogger(CommentService.class);

    /**
     * Deletes Comment object only if the value of user's username of the argument is the same as current principal's username
     *
     * @param comment Comment object to be deleted
     */
    @PreAuthorize("#comment.user.username == authentication.principal.username")
    protected void delete(Comment comment) {
        LOGGER.debug("Deleting comment");
        repository.delete(comment);
    }

    /**
     * @param commentId id of wanted Comment object
     * @param recipeId  id of a recipe that wanted comment belong to
     * @return proper Comment object, if it does't exist this method throws new ResourceNotFoundException
     */
    protected Comment getOneById(Long commentId, Long recipeId) {
        Comment comment = repository.findById(commentId).orElseThrow(ResourceNotFoundException::new);
        if (comment.getRecipe().getId().equals(recipeId)) {
            return comment;
        }
        LOGGER.warn("Resource not found");
        throw new  ResourceNotFoundException();
    }

    /**
     * @param recipeId id of a recipe that wanted comments belong to
     * @param pageable PageRequest object that should contain data about wanted page number and size
     * @return wanted Page of a given recipe's comments
     */
    protected Page<Comment> findCommentsByRecipeId(Long recipeId, Pageable pageable) {
        return repository.findByRecipeId(recipeId, pageable);
    }

    /**
     * @param comment Comment object to be saved
     * @return Comment object that just has been saved
     */
    protected Comment saveComment(Comment comment) {
        return repository.save(comment);
    }


}
