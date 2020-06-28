package com.ironhack.MidtermProject.controller.impl.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.service.account.SavingsAccService;
import com.ironhack.MidtermProject.service.account.StudentCheckingAccService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class StudentCheckingAccControllerImplTestUnit {
    @MockBean
    private StudentCheckingAccService studentCheckingAccService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext webApplicationContext;

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2, ah3;
    Admin admin1;
    ThirdParty party1;
    StudentCheckingAcc ac1, ac2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        add1 = new Address("Spain", "Madrid", "Canal");
        d1 = Date.valueOf("1980-10-12");
        d2 = Date.valueOf("1982-05-18");
        ah1 = new AccountHolder("Simba", "kinglyon", "kinglyon", d1, add1, null);
        ah1.setId(1);
        ah2 = new AccountHolder("Hercules", "strongman", "strongman", d2, add1, null);
        ah3 = new AccountHolder("Pinocho", "woodman", "woodman", d2, add1, null);
        admin1 = new Admin("Dreamworks", "dreamworks","dreamworks");
        party1 = new ThirdParty("Third", "third", "third", "third-hashkey");
        ac1 = new StudentCheckingAcc(ah1,ah2,new Money(new BigDecimal("5000")), Status.ACTIVE);
        ac1.setId(1);
        ac2 = new StudentCheckingAcc(ah1,ah3,new Money(new BigDecimal("1000")), Status.ACTIVE);
        ac2.setId(2);
        List<StudentCheckingAcc> studentCheckingAccs = Arrays.asList(ac1,ac2);
        when(studentCheckingAccService.findAll()).thenReturn(studentCheckingAccs);
        when(studentCheckingAccService.checkFindById(1, ah1)).thenReturn(ac1);
        doAnswer(i->{return null;}).when(studentCheckingAccService).addBalance(ah1,ac1.getId(),new BigDecimal("100"),null, "ksdhuf","skdhfs");
        doAnswer(i->{return null;}).when(studentCheckingAccService).reduceBalance(ah1,ac1.getId(),new BigDecimal("100"),null, "ksdhuf","skdhfs");
        StudentCheckingAcc ac4 = ac1;
        ac4.setStatus(Status.FROZEN);
        when(studentCheckingAccService.changeStatus(ac1.getId(),"FROZEN")).thenReturn(ac4);
    }


    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/student-checking-accounts")).andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString().contains("balance");
    }

    @Test
    void findById() throws Exception {
        mockMvc.perform(get("/student-checking-accounts/" + ac1.getId())).andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString().contains("Simba");
    }

    @Test
    void addBalance() throws Exception {
        mockMvc.perform(patch("/student-checking-accounts/" + ac1.getId() + "/credit?amount="  + String.valueOf(100))).andExpect(status().is2xxSuccessful());
    }

    @Test
    void reduceBalance() throws Exception {
        mockMvc.perform(patch("/student-checking-accounts/" + ac1.getId() + "/debit?amount="  + String.valueOf(100))).andExpect(status().is2xxSuccessful());
    }

    @Test
    void changeStatus() throws Exception {
        mockMvc.perform(put("/student-checking-accounts/"+ ac1.getId() +"/set-status/FROZEN")).andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString().contains("FROZEN");
    }

}