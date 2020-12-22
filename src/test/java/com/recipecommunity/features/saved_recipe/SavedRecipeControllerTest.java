package com.recipecommunity.features.saved_recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipecommunity.features.recipe.Recipe;
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
class SavedRecipeControllerTest {
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private SavedRecipeService service;
    private SavedRecipe savedRecipe;
    private User user;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    protected void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        savedRecipe = new SavedRecipe(5L);
        Recipe recipe = new Recipe(1L);
        recipe.setAuthor(new User(2L));
        user = new User(3L);
        user.setUsername("test");
        savedRecipe.setRecipe(recipe);
        savedRecipe.setUser(user);
    }

    @AfterEach
    protected void tearDown() {
        mockMvc = null;
        savedRecipe = null;
        user = null;
    }

    @WithMockUser(username = "test")
    @Test
    protected void test_deleteSavedRecipe() throws Exception {
        given(service.getOneById(5L)).willReturn(savedRecipe);
        mockMvc.perform(delete("/api/users/me/saved-recipes/{id}", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(service).delete(savedRecipe);
    }

    @WithMockUser(username = "test")
    @Test
    protected void test_getSavedRecipeById_with_proper_user() throws Exception {
        given(service.getOneById(5L)).willReturn(savedRecipe);
        mockMvc.perform(get("/api/users/me/saved-recipes/{id}", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedRecipe.getId().intValue())))
                .andExpect(jsonPath("$._links", aMapWithSize(3)));
    }

    @WithMockUser(username = "wrong name")
    @Test
    protected void test_getSavedRecipeById_with_wrong_user() throws Exception {
        given(service.getOneById(5L)).willReturn(savedRecipe);
        assertThrows(NestedServletException.class, () -> mockMvc.perform(get("/api/users/me/saved-recipes/{id}", "5")));
    }

    @Test
    @WithMockUser(username = "test")
    protected void test_save() throws Exception {
        SavedRecipeRequest savedRecipeRequest = new SavedRecipeRequest();
        savedRecipeRequest.setRecipeToBeSaveId(1L);
        SavedRecipe toBeSaved = new SavedRecipe();
         toBeSaved.setUser(user);
        toBeSaved.setRecipe(new Recipe(1L));
        given(userService.findUserByUsername("test")).willReturn(user);
        given(service.save(toBeSaved)).willReturn(savedRecipe);
        mockMvc.perform(post("/api/users/me/saved-recipes")
                .content(asJsonString(savedRecipeRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(savedRecipe.getId().intValue())));
    }

    @WithMockUser(username = "test")
    @Test
    protected void test_getSavedRecipes() throws Exception {
        List<SavedRecipe> savedRecipes = new ArrayList<>();
        savedRecipes.add(savedRecipe);
        Page<SavedRecipe> page = new PageImpl<>(savedRecipes);
        given(service.findUsersSavedRecipes("test", PageRequest.of(0, 10))).willReturn(page);
        mockMvc.perform(get("/api/users/me/saved-recipes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.savedRecipes[0].id", is(savedRecipe.getId().intValue())))
                .andExpect(jsonPath("$._links", aMapWithSize(2)));
    }

    @Test
    protected void test_addLinks() {
        SavedRecipeController savedRecipeController = new SavedRecipeController(service, userService);
        assertTrue(savedRecipeController.addLinks(savedRecipe).getLinks().hasSize(3));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}