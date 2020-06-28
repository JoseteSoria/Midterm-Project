package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.security.CustomSecurityUser;
import com.ironhack.MidtermProject.service.account.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountControllerImplTestUnit {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean(name = "accountService")
    private AccountService accountService;

    private MockMvc mockMvc;

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2, ah3;
    Admin admin1;
    CheckingAcc ac1, ac2;
    CustomSecurityUser cu1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        add1 = new Address("Spain", "Madrid", "Canal");
        d1 = Date.valueOf("1980-10-12");
        d2 = Date.valueOf("1982-05-18");
        ah1 = new AccountHolder("Simba", "kinglyon", "kinglyon", d1, add1, null);
        ah1.setLoggedIn(true);
        ah2 = new AccountHolder("Hercules", "strongman", "strongman", d2, add1, null);
        admin1 = new Admin("Dreamworks", "dreamworks","dreamworks");
        ah3 = new AccountHolder("Pinocho", "woodman", "woodman", d2, add1, null);
        ac1 = new CheckingAcc(ah1,ah2,new Money(new BigDecimal("5000")), Status.ACTIVE);
        ac2 = new CheckingAcc(ah1,ah3,new Money(new BigDecimal("1000")), Status.ACTIVE);
        List<Account> accounts = Arrays.asList(ac1,ac2);
        when(accountService.findAllAccount()).thenReturn(accounts);
        cu1 = new CustomSecurityUser(new Admin("Dreamworks", "dreamworks","dreamworks"));
    }

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/accounts").with(user(cu1))).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().contains("id");
    }
}