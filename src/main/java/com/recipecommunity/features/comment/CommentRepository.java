package com.recipecommunity.features.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
/**
 * Comment Repository interface, extends Paging and Sorting Repository.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@Repository
public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {
Page<Comment> findByRecipeId(Long id, Pageable pageable);
}
