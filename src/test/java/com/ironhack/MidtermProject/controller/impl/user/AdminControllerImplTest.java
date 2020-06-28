package com.ironhack.MidtermProject.controller.impl.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.repository.user.AdminRepository;
import com.ironhack.MidtermProject.security.CustomSecurityUser;
import com.ironhack.MidtermProject.service.user.AdminService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AdminControllerImplTest {

    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminRepository adminRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    Admin admin1;
    CustomSecurityUser cu1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        admin1 = new Admin("Dreamworks", "dreamworks", "dreamworks");
        adminRepository.save(admin1);
        cu1 = new CustomSecurityUser(new Admin("Dreamworks", "dreamworks", "dreamworks"));
    }

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
    }

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/admins").with(user(cu1))).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().contains("Dreamworks");
    }

    @Test
    void create() throws Exception {
        Admin admin2 = new Admin("Amazon", "amazon", "amazon");
        mockMvc.perform(post("/admins").with(user(cu1)).content(objectMapper.writeValueAsString(admin2))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString().contains("Amazon");
    }


}