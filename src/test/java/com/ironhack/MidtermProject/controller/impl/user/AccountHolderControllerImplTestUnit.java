package com.ironhack.MidtermProject.controller.impl.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MidtermProject.dto.AccountMainFields;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.service.user.AccountHolderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountHolderControllerImplTestUnit {
    @MockBean
    private AccountHolderService accountHolderService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2, ah3;
    Admin admin1;
    ThirdParty party1;
    CheckingAcc ac1, ac2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        add1 = new Address("Spain", "Madrid", "Canal");
        d1 = Date.valueOf("1980-10-12");
        d2 = Date.valueOf("1982-05-18");
        ah1 = new AccountHolder("Simba", "kinglyon", "kinglyon", d1, add1, null);
        ah1.setLoggedIn(true);
        ah1.setId(1);
        ah2 = new AccountHolder("Hercules", "strongman", "strongman", d2, add1, null);
        ah2.setId(2);
        ah3 = new AccountHolder("Pinocho", "woodman", "woodman", d2, add1, null);
        ah3.setId(3);
        admin1 = new Admin("Dreamworks", "dreamworks", "dreamworks");
        party1 = new ThirdParty("Third", "third", "third", "third-hashkey");
        ac1 = new CheckingAcc(ah1, ah2, new Money(new BigDecimal("5000")), Status.ACTIVE);
        ac1.setId(1);
        ac2 = new CheckingAcc(ah1, ah3, new Money(new BigDecimal("1000")), Status.ACTIVE);
        ac2.setId(2);
        when(accountHolderService.findAll()).thenReturn(Stream.of(ah1, ah2, ah3).collect(Collectors.toList()));
        when(accountHolderService.checkFindById(1, ah1)).thenReturn(ah1);
        AccountMainFields af1 = new AccountMainFields(ac1.getId(), ah1.getName(), ac1.getBalance());
        AccountMainFields af2 = new AccountMainFields(ac2.getId(), ah1.getName(), ac2.getBalance());
        when(accountHolderService.findAllAccountAsPrimaryOwnerById(1, ah1)).thenReturn(Stream.of(af1, af2).collect(Collectors.toList()));
        AccountHolder ah4 = new AccountHolder("Cinderella", "cinderella", "cinderella", d1, add1, null);
        when(accountHolderService.store(ah4)).thenReturn(ah4);
        doAnswer(i -> {
            return null;
        }).when(accountHolderService).setLogged(ah1, false);
        doAnswer(i -> {
            return null;
        }).when(accountHolderService).prepareTransference(ah1, ac2.getId(), ac1.getId(), new BigDecimal("100"), null);
    }

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/account-holders")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().contains("Hercules");
    }

    @Test
    void findById() throws Exception {
        mockMvc.perform(get("/account-holders/" + ah1.getId())).andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString().contains("Simba");
    }

    @Test
    void findAllAccountAsPrimaryOwnerById() throws Exception {
        MvcResult result = mockMvc.perform(get("/account-holders/" + ah1.getId() + "/accounts")).andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    void create() throws Exception {
        AccountHolder ah4 = new AccountHolder("Cinderella", "cinderella", "cinderella", d1, add1, null);
        mockMvc.perform(post("/account-holders").content(objectMapper.writeValueAsString(ah4))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString().contains("Cinderella");
    }

    @Test
    void setLogged() throws Exception {
        mockMvc.perform(patch("/account-holders/logged-in/false")).andExpect(status().is2xxSuccessful());
    }

    @Test
    void transference() throws Exception {
        mockMvc.perform(post("/account-holders/transference/" + ac1.getId() + "?receiver-account-id=" + ac2.getId() + "&amount=400")).andExpect(status().is2xxSuccessful());
    }

}