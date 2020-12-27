package com.recipecommunity.features.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipecommunity.RecipeCommunityApplication;
import com.recipecommunity.features.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = RecipeCommunityApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class JwtAuthenticationControllerTest {
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtUserDetailsService userDetailsService;
    @MockBean
    private JwtTokenUtil util;

    @BeforeEach
    protected void setUp() {
        JwtAuthenticationController authenticationController = new JwtAuthenticationController(authenticationManager, util, userDetailsService);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @AfterEach
    protected void tearDown() {
        mockMvc = null;
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void test_createAuthenticationToken_when_jwtRequest_is_proper() throws Exception {
        JwtRequest jwtRequest = new JwtRequest("name", "pass");
        UserDetails userDetails = new org.springframework.security.core.userdetails.User("name", "Hashed",
                new ArrayList<>());
        given(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("name", "pass")))
                .willReturn(null);
        given(userDetailsService.loadUserByUsername("name")).willReturn(userDetails);
        given(util.generateToken(userDetails)).willReturn("myCustomToken");
        mockMvc.perform(post("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(jwtRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("myCustomToken")));
    }

    @Test
    public void test_saveUser_when_doesnt_already_exist() throws Exception {
        JwtRequest jwtRequest = new JwtRequest("test", "pass");
        User myUser = new User(1L);
        myUser.setUsername("test");
        given(userDetailsService.doesUserAlreadyExist("test")).willReturn(false);
        given(userDetailsService.saveNewUser(jwtRequest)).willReturn(myUser);
        mockMvc.perform(post("/api/auth/sign-up")
                .content(new ObjectMapper().writeValueAsString(jwtRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(myUser.getId().intValue())))
                .andExpect(jsonPath("$.username", is(myUser.getUsername())));
    }

    @Test
    public void test_saveUser_when_user_already_exists() {
        JwtRequest jwtRequest = new JwtRequest("alreadyExists", "pass");
        given(userDetailsService.doesUserAlreadyExist("alreadyExists")).willReturn(true);
        assertThrows(NestedServletException.class, () -> mockMvc.perform(post("/api/auth/sign-up")
                .content(new ObjectMapper().writeValueAsString(jwtRequest))
                .contentType(MediaType.APPLICATION_JSON)));
    }
}