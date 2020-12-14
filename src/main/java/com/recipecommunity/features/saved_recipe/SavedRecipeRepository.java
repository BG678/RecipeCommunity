package com.recipecommunity.features.saved_recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SavedRecipe Repository interface, extends Paging and Sorting Repository.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@Repository
public interface SavedRecipeRepository extends PagingAndSortingRepository<SavedRecipe, Long> {
    Page<SavedRecipe> findByUserUsername(String username, Pageable pageable);

}
