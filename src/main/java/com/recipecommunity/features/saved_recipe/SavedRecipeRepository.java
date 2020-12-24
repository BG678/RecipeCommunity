package com.recipecommunity.features.saved_recipe;

import com.recipecommunity.utils.SavedRecipeByUsernameAndRecipeId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * SavedRecipe Repository interface, extends Paging and Sorting Repository and SavedRecipeByUsernameAndRecipeId.
 *
 * @author Barbara Grabowska
 * @version %I%, %G%
 */
@Repository
public interface SavedRecipeRepository extends PagingAndSortingRepository<SavedRecipe, Long>, SavedRecipeByUsernameAndRecipeId {
    Page<SavedRecipe> findByUserUsername(String username, Pageable pageable);
    @Override
    SavedRecipe findByUserUsernameAndRecipeId(String username, Long id);
}
