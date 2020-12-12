package com.recipecommunity.features.user;

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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    private MockMvc mockMvc;
    @MockBean
    private UserService service;
    private User myUser;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    protected void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        myUser = new User(5L);
        myUser.setUsername("userr");
    }

    @AfterEach
    protected void tearDown() {
        mockMvc = null;
        myUser = null;
    }

    @Test
    @WithMockUser(username = "userr")
    protected void test_getCurrentUser() throws Exception {
        given(service.findUserByUsername("userr")).willReturn(myUser);
        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(myUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(myUser.getUsername())))
                .andExpect(jsonPath("$._links", aMapWithSize(3)));
    }

    @Test
    protected void test_getUserById() throws Exception {
        given(service.findUserById(5L)).willReturn(myUser);
        mockMvc.perform(get("/api/users/{id}", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(myUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(myUser.getUsername())))
                .andExpect(jsonPath("$._links", aMapWithSize(2)));
    }

    @Test
    protected void test_getUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(myUser);
        Page<User> pag = new PageImpl<>(users);
        given(service.findAll(PageRequest.of(0, 10))).willReturn(pag);
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.users[0].id", is(myUser.getId().intValue())))
                .andExpect(jsonPath("$._embedded.users[0].username", is(myUser.getUsername())))
                .andExpect(jsonPath("$._embedded.users[0]._links", aMapWithSize(1)))
                .andExpect(jsonPath("$._links", aMapWithSize(1)));
    }
    @Test
    protected void test_getUsers_when_wanted_page_does_not_exist(){
        List<User> users = new ArrayList<>();
        users.add(myUser);
        Page<User> pag = new PageImpl<>(users);
        given(service.findAll(PageRequest.of(3, 10))).willReturn(pag);
        assertThrows(NestedServletException.class, () -> mockMvc.perform(get("/api/users?page=3")
                .contentType(MediaType.APPLICATION_JSON)));
    }


}