package com.recipecommunity.features.user;

import com.recipecommunity.features.utils.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class UserServiceTest {
    @MockBean
    private UserRepository repository;
    private UserService userService;

    @BeforeEach
    protected void setUp() {
      userService = new UserService(repository);
    }

    @AfterEach
    protected void tearDown() {
        userService = null;
    }
    @Test
    protected void test_findUserById_when_user_does_not_exist(){
        given(repository.findById(1L)).willReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.findUserById(1L));
    }

}