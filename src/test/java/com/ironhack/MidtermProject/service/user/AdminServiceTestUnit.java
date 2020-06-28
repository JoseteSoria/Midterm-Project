package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.repository.user.AdminRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest
class AdminServiceTestUnit {

    @Autowired
    private AdminService adminService;

    @MockBean(name = "adminRepository")
    private AdminRepository adminRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    Admin a1, a2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        a1 = new Admin("Simba", "kinglyon", "kinglyon");
        a2 = new Admin("Hercules", "strongman", "strongman");
        when(adminRepository.findAll()).thenReturn(Stream.of(a1, a2).collect(Collectors.toList()));
        Admin admin = new Admin("Mufasa", "mufasa", "mufasa");
        doAnswer(i -> null).when(adminRepository).save(admin);
    }

    @Test
    void findAll() {
        List<Admin> admins = adminService.findAll();
        assertEquals(2, admins.size());
    }

    @Test
    void createAdmin() {
        Admin admin = new Admin("Mufasa", "mufasa", "mufasa");
        assertEquals(null, adminService.create(admin));
    }
}