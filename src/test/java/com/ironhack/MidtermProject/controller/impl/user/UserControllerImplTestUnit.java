package com.ironhack.MidtermProject.controller.impl.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MidtermProject.enums.Role;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerImplTestUnit {

    @MockBean
    private UserService userService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    User u1, u2;
    private List<User> users;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        u1 = new User("Dreamworks", "dreamworks", "dreamworks", Role.ADMIN);
        u2 = new User("Hercules", "strongman", "strongman", Role.ACCOUNT_HOLDER);
        users = Stream.of(u1, u2).collect(Collectors.toList());
        when(userService.findAll()).thenReturn(users);
    }

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().contains("Dreamworks");
    }
}