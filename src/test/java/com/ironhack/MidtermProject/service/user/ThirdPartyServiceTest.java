package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.repository.user.AdminRepository;
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
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
class ThirdPartyServiceTest {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private ThirdPartyService thirdPartyService;

    ThirdParty t1,t2;

    @BeforeEach
    void setUp() {
        t1 = new ThirdParty("Simba", "kinglyon", "kinglyon", "kinglyon-hashkey");
        t2 = new ThirdParty("Hercules", "strongman", "strongman", "strongman-hashkey");
        thirdPartyRepository.saveAll(Stream.of(t1, t2).collect(Collectors.toList()));
    }

    @AfterEach
    void tearDown() {
        thirdPartyRepository.deleteAll();
    }

    @Test
    void findAll(){
        List<ThirdParty> thirdParties = thirdPartyService.findAll();
        assertEquals(2, thirdParties.size());
    }

    @Test
    void createThirdParty() {
        ThirdParty thirdParty = new ThirdParty("Mufasa", "mufasa", "mufasa", "mufasa-hashkey");
        ThirdParty result = thirdPartyService.store(thirdParty);
        assertEquals("mufasa", result.getUsername());
    }
}