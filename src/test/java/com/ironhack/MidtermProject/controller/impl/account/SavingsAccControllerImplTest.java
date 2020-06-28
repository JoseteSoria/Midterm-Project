package com.ironhack.MidtermProject.controller.impl.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.repository.account.AccountRepository;
import com.ironhack.MidtermProject.repository.account.SavingsAccRepository;
import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.repository.user.AdminRepository;
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
import com.ironhack.MidtermProject.security.CustomSecurityUser;
import com.ironhack.MidtermProject.service.account.SavingsAccService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SavingsAccControllerImplTest {
    @Autowired
    private SavingsAccService savingsAccService;
    @Autowired
    private SavingsAccRepository savingsAccRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2, ah3;
    Admin admin1;
    ThirdParty party1;
    SavingsAcc ac1, ac2;
    CustomSecurityUser cu1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        add1 = new Address("Spain", "Madrid", "Canal");
        d1 = Date.valueOf("1980-10-12");
        d2 = Date.valueOf("1982-05-18");
        ah1 = new AccountHolder("Simba", "kinglyon", "kinglyon", d1, add1, null);
        ah2 = new AccountHolder("Hercules", "strongman", "strongman", d2, add1, null);
        ah3 = new AccountHolder("Pinocho", "woodman", "woodman", d2, add1, null);
        admin1 = new Admin("Dreamworks", "dreamworks", "dreamworks");
        party1 = new ThirdParty("Third", "third", "third", "third-hashkey");
        accountHolderRepository.saveAll(Stream.of(ah1, ah2, ah3).collect(Collectors.toList()));
        adminRepository.save(admin1);
        thirdPartyRepository.save(party1);
        ac1 = new SavingsAcc(ah1, ah2, new Money(new BigDecimal("5000")), Status.ACTIVE);
        ac2 = new SavingsAcc(ah1, ah3, new Money(new BigDecimal("1000")), Status.ACTIVE);
        savingsAccRepository.saveAll(Stream.of(ac1, ac2).collect(Collectors.toList()));
        cu1 = new CustomSecurityUser(new Admin("Dreamworks", "dreamworks", "dreamworks"));
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        savingsAccRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/savings-accounts").with(user(cu1))).andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString().contains("balance");
    }

    @Test
    void findById() throws Exception {
        mockMvc.perform(get("/savings-accounts/" + ac1.getId()).with(user(cu1))).andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString().contains("Simba");
    }

    @Test
    void addBalance() throws Exception {
        mockMvc.perform(patch("/savings-accounts/" + ac1.getId() + "/credit?amount=" + String.valueOf(100)).with(user(cu1))).andExpect(status().is2xxSuccessful());
    }

    @Test
    void reduceBalance() throws Exception {
        mockMvc.perform(patch("/savings-accounts/" + ac1.getId() + "/debit?amount=" + String.valueOf(100)).with(user(cu1))).andExpect(status().is2xxSuccessful());
    }

    @Test
    void store() throws Exception {
        SavingsAcc ac3 = new SavingsAcc(ah2, ah3, new Money(new BigDecimal("7000")), Status.ACTIVE);
        mockMvc.perform(post("/savings-accounts").with(user(cu1)).content(objectMapper.writeValueAsString(ac3))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString().contains("7000");
    }

    @Test
    void changeStatus() throws Exception {
        mockMvc.perform(put("/savings-accounts/" + ac1.getId() + "/set-status/FROZEN").with(user(cu1))).andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString().contains("FROZEN");
    }


}