package com.recipecommunity.features.comment;

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private CommentService service;
    private Comment comment;
    private User user;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    protected void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        comment = new Comment(5L);
        Recipe recipe = new Recipe(1L);
        recipe.setAuthor(new User(2L));
        user = new User(3L);
        user.setUsername("test");
        comment.setRecipe(recipe);
        comment.setUser(user);
    }

    @AfterEach
    protected void tearDown() {
        mockMvc = null;
        comment = null;
        user = null;
    }

    @Test
    protected void test_deleteComment() throws Exception {
        given(service.getOneById(5L, 1L)).willReturn(comment);
        mockMvc.perform(delete("/api/recipes/1/comments/{id}", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(service).delete(comment);
    }

    @Test
    protected void test_getCommentById() throws Exception {
        given(service.getOneById(5L, 1L)).willReturn(comment);
        mockMvc.perform(get("/api/recipes/1/comments/{id}", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId().intValue())))
                .andExpect(jsonPath("$._links", aMapWithSize(2)));
    }

    @Test
    @WithMockUser(username = "test")
    protected void test_saveNewComment() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setText("My text");
        Comment toBeSaved = new Comment();
        toBeSaved.setUser(user);
        toBeSaved.setText(request.getText());
        toBeSaved.setDate(LocalDate.now());
        toBeSaved.setRecipe(new Recipe(1L));
        given(userService.findUserByUsername("test")).willReturn(user);
        given(service.saveComment(toBeSaved)).willReturn(comment);
        mockMvc.perform(post("/api/recipes/1/comments")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(comment.getId().intValue())));
    }

    @Test
    protected void test_getComments() throws Exception {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        Page<Comment> page = new PageImpl<>(comments);
        given(service.findCommentsByRecipeId(1L, PageRequest.of(0, 10))).willReturn(page);
        mockMvc.perform(get("/api/recipes/1/comments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.comments[0].id", is(comment.getId().intValue())))
                .andExpect(jsonPath("$._links", aMapWithSize(2)));
    }

    @Test
    protected void test_addLinks() {
        CommentController commentController = new CommentController(service, userService);
        assertTrue(commentController.addLinks(comment).getLinks().hasSize(2));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}