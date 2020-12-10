package com.recipecommunity.features.comment;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes/{recipeId}/comments")
public class CommentController {

    @GetMapping
    public ResponseEntity<CollectionModel<Comment>> getComments(
            @RequestParam(value = "page", required = false, defaultValue = "0")
                    int pageNumber, @PathVariable Long recipeId) {
        return null;
    }

    @PostMapping
    public ResponseEntity<Comment> saveNewComment(@RequestBody CommentRequest commentRequest, @PathVariable Long recipeId,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long recipeId, @PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long recipeId, @PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        //Access!
        return null;
    }
}
