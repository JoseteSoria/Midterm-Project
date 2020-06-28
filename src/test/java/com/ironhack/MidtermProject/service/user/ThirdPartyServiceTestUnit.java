package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
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
class ThirdPartyServiceTestUnit {

    @Autowired
    private ThirdPartyService thirdPartyService;

    @MockBean(name = "thirdPartyRepository")
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    ThirdParty t1, t2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        t1 = new ThirdParty("Simba", "kinglyon", "kinglyon", "kinglyon-hashkey");
        t2 = new ThirdParty("Hercules", "strongman", "strongman", "strongman-hashkey");
        when(thirdPartyRepository.findAll()).thenReturn(Stream.of(t1, t2).collect(Collectors.toList()));
        ThirdParty thirdParty = new ThirdParty("Mufasa", "mufasa", "mufasa", "mufasa-hashkey");
        doAnswer(i -> null).when(thirdPartyRepository).save(thirdParty);
    }

    @Test
    void findAll() {
        List<ThirdParty> thirdParties = thirdPartyService.findAll();
        assertEquals(2, thirdParties.size());
    }

    @Test
    void createThirdParty() {
        ThirdParty thirdParty = new ThirdParty("Mufasa", "mufasa", "mufasa", "mufasa-hashkey");
        assertEquals(null, thirdPartyService.store(thirdParty));
    }

}