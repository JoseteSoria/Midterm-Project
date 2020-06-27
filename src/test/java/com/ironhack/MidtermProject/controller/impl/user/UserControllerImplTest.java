package com.ironhack.MidtermProject.controller.impl.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MidtermProject.enums.Role;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.user.UserRepository;
import com.ironhack.MidtermProject.security.CustomSecurityUser;
import com.ironhack.MidtermProject.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerImplTest {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    User u1,u2;
    private List<User> users;
    CustomSecurityUser cu1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        u1 = new User("Dreamworks", "dreamworks", "dreamworks", Role.ADMIN);
        u2 = new User("Hercules", "strongman", "strongman", Role.ACCOUNT_HOLDER);
        users = Stream.of(u1,u2).collect(Collectors.toList());
        userRepository.saveAll(Stream.of(u1, u2).collect(Collectors.toList()));
        cu1 = new CustomSecurityUser(new Admin("Dreamworks", "dreamworks","dreamworks"));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }


    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/users").with(user(cu1))).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().contains("Dreamworks");
    }
}