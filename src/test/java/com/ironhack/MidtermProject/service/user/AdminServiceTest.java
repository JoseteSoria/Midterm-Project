package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.enums.Role;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.user.AdminRepository;
import com.ironhack.MidtermProject.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdminServiceTest {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private AdminService adminService;

    Admin a1,a2;

    @BeforeEach
    void setUp() {
        a1 = new Admin("Simba", "kinglyon", "kinglyon");
        a2 = new Admin("Hercules", "strongman", "strongman");
        adminRepository.saveAll(Stream.of(a1, a2).collect(Collectors.toList()));
    }

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
    }

    @Test
    void findAll(){
        List<Admin> admins = adminService.findAll();
        assertEquals(2, admins.size());
    }

    @Test
    void createAdmin() {
        Admin admin = new Admin("Mufasa", "mufasa", "mufasa");
        Admin result = adminService.create(admin);
        assertEquals("mufasa", result.getUsername());
    }

}