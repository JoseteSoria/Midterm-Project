package com.ironhack.MidtermProject.controller.impl.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.repository.account.AccountRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.repository.user.AdminRepository;
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
import com.ironhack.MidtermProject.security.CustomSecurityUser;
import com.ironhack.MidtermProject.service.account.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountControllerImplTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private AdminRepository adminRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2;
    Admin admin1;
    ThirdParty party1;
    Account ac1;
    CustomSecurityUser cu1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        add1 = new Address("Spain", "Madrid", "Canal");
        d1 = Date.valueOf("1980-10-12");
        d2 = Date.valueOf("1982-05-18");
        ah1 = new AccountHolder("Simba", "kinglyon", "kinglyon", d1, add1, null);
        ah2 = new AccountHolder("Hercules", "strongman", "strongman", d2, add1, null);
        admin1 = new Admin("Dreamworks", "dreamworks", "dreamworks");
        party1 = new ThirdParty("Third", "third", "third", "third-hashkey");
        accountHolderRepository.saveAll(Stream.of(ah1, ah2).collect(Collectors.toList()));
        adminRepository.save(admin1);
        thirdPartyRepository.save(party1);
        ac1 = new StudentCheckingAcc(ah1, ah2, new Money(new BigDecimal("1000")), Status.ACTIVE);
        accountRepository.save(ac1);
        cu1 = new CustomSecurityUser(new Admin("Dreamworks", "dreamworks", "dreamworks"));
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/accounts").with(user(cu1))).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().contains("id");
    }

}