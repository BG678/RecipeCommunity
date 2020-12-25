package com.recipecommunity.features.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipecommunity.features.saved_recipe.SavedRecipeRepository;
import com.recipecommunity.features.user.User;
import com.recipecommunity.features.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecipeControllerTest {
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private SavedRecipeRepository repository;
    @MockBean
    private RecipeService service;
    private Recipe recipe;
    private User user;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    protected void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        recipe = new Recipe(5L);
        user = new User(3L);
        user.setUsername("test");
        recipe.setAuthor(user);
    }

    @AfterEach
    protected void tearDown() {
        mockMvc = null;
        recipe = null;
        user = null;
    }

    @Test
    protected void test_getRecipes() throws Exception {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(recipe);
        given(service.getAllRecipes()).willReturn(recipes);
        mockMvc.perform(get("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.recipes[0].id", is(recipe.getId().intValue())))
                .andExpect(jsonPath("$._links", aMapWithSize(1)));
    }

    @WithMockUser(username = "test")
    @Test
    protected void test_getOne() throws Exception {
        given(service.getOneById(5L)).willReturn(recipe);
        given(repository.findByUserUsernameAndRecipeId("test", 5L)).willReturn(null);
        mockMvc.perform(get("/api/recipes/{id}", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(recipe.getId().intValue())))
                .andExpect(jsonPath("$._links", aMapWithSize(4)));
    }

    @WithMockUser(username = "test")
    @Test
    protected void test_getCurrentUsersRecipes() throws Exception {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(recipe);
        Page<Recipe> page = new PageImpl<>(recipes);
        given(service.findRecipesByUsername("test", PageRequest.of(0, 10))).willReturn(page);
        mockMvc.perform(get("/api/recipes/my")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.recipes[0].id", is(recipe.getId().intValue())))
                .andExpect(jsonPath("$._links", aMapWithSize(1)));
    }

    @WithMockUser(username = "test")
    @Test
    protected void test_saveNewRecipe() throws Exception {
        RecipeRequest request = new RecipeRequest();
        request.setTitle("title");
        request.setText("My text");
        Recipe toBeSaved = new Recipe();
        toBeSaved.setAuthor(user);
        toBeSaved.setText(request.getText());
        toBeSaved.setTitle(request.getTitle());
        given(userService.findUserByUsername("test")).willReturn(user);
        given(service.saveRecipe(toBeSaved)).willReturn(recipe);
        mockMvc.perform(post("/api/recipes/my")
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(recipe.getId().intValue())));
    }

    @WithMockUser(username = "test")
    @Test
    protected void test_getMyRecipeById_when_user_has_access_to_wanted_resource() throws Exception {
        given(service.getOneById(5L)).willReturn(recipe);
        mockMvc.perform(get("/api/recipes/my/{id}", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(recipe.getId().intValue())))
                .andExpect(jsonPath("$._links", aMapWithSize(2)));
    }

    @WithMockUser(username = "wrong username")
    @Test
    protected void test_getMyRecipeById_when_user_hasnt_got_access_to_wanted_resource() throws Exception {
        given(service.getOneById(5L)).willReturn(recipe);
        assertThrows(NestedServletException.class, () -> mockMvc.perform(get("/api/recipes/my/{id}", "5")));
    }

    @Test
    protected void test_deleteRecipe() throws Exception {
        given(service.getOneById(5L)).willReturn(recipe);
        mockMvc.perform(delete("/api/recipes/my/{id}", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(service).delete(recipe);
    }

    @WithMockUser(username = "test")
    @Test
    protected void test_editRecipe() throws Exception {
        RecipeRequest request = new RecipeRequest();
        request.setTitle("title");
        request.setText("My text");
        Recipe toBeSaved = new Recipe();
        toBeSaved.setAuthor(user);
        toBeSaved.setId(5L);
        toBeSaved.setText(request.getText());
        toBeSaved.setTitle(request.getTitle());
        given(userService.findUserByUsername("test")).willReturn(user);
        given(service.saveRecipe(toBeSaved)).willReturn(recipe);
        given(service.getOneById(5L)).willReturn(recipe);
        mockMvc.perform(put("/api/recipes/my/{id}", "5")
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(recipe.getId().intValue())));
        verify(service).saveRecipe(recipe);
    }

    @Test
    protected void test_getRecipesCreatedByGivenUser() throws Exception {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(recipe);
        Page<Recipe> page = new PageImpl<>(recipes);
        given(service.findRecipesByUsername("test", PageRequest.of(0, 10))).willReturn(page);
        mockMvc.perform(get("/api/recipes/created-by-{username}", "test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.recipes[0].id", is(recipe.getId().intValue())))
                .andExpect(jsonPath("$._links", aMapWithSize(1)));
    }

    @Test
    protected void test_addLinks() {
        RecipeController recipeController = new RecipeController(service, userService, repository);
        assertTrue(recipeController.addLinks(recipe, null).getLinks().hasSize(3));
    }

}