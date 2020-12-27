package com.recipecommunity.features.recipe;

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
public interface RecipeRepository extends PagingAndSortingRepository<Recipe, Long> {
    Page<Recipe> findByAuthorUsername(String username, Pageable pageable);

    List<Recipe> findAll();

    Page<Recipe> findAllByTitleContaining(String title, Pageable pageable);
}
