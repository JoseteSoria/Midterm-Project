package com.ironhack.MidtermProject.controller.impl.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.repository.user.AdminRepository;
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
import com.ironhack.MidtermProject.security.CustomSecurityUser;
import com.ironhack.MidtermProject.service.user.ThirdPartyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ThirdPartyControllerImplTestUnit {
    @MockBean
    private ThirdPartyService thirdPartyService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    ThirdParty t1,t2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        t1 = new ThirdParty("Simba", "kinglyon", "kinglyon", "kinglyon-hashkey");
        t2 = new ThirdParty("Hercules", "strongman", "strongman", "strongman-hashkey");
        when(thirdPartyService.findAll()).thenReturn(Stream.of(t1,t2).collect(Collectors.toList()));
        ThirdParty t3 = new ThirdParty("Amazon", "amazon", "amazon", "amazon-hashkey");
        when(thirdPartyService.store(t3)).thenReturn(t3);

    }


    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/third-parties")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().contains("Hercules");
    }

    @Test
    void create() throws Exception {
        ThirdParty t3 = new ThirdParty("Amazon", "amazon", "amazon", "amazon-hashkey");
        mockMvc.perform(post("/third-parties").content(objectMapper.writeValueAsString(t3))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString().contains("amazon");
    }
}