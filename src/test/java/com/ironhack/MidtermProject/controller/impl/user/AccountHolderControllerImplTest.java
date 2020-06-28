package com.ironhack.MidtermProject.controller.impl.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.repository.account.*;
import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.repository.user.AdminRepository;
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
import com.ironhack.MidtermProject.security.CustomSecurityUser;
import com.ironhack.MidtermProject.service.user.AccountHolderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountHolderControllerImplTest {
    @Autowired
    private AccountHolderService accountHolderService;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CheckingAccRepository checkingAccRepository;
    @Autowired
    private StudentCheckingAccRepository studentCheckingAccRepository;
    @Autowired
    private CreditCardAccRepository creditCardAccRepository;
    @Autowired
    private SavingsAccRepository savingsAccRepository;
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
    CheckingAcc ac1, ac2;
    CustomSecurityUser cu1, cu2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        add1 = new Address("Spain", "Madrid", "Canal");
        d1 = Date.valueOf("1980-10-12");
        d2 = Date.valueOf("1982-05-18");
        ah1 = new AccountHolder("Simba", "kinglyon", "kinglyon", d1, add1, null);
        ah1.setLoggedIn(true);
        ah2 = new AccountHolder("Hercules", "strongman", "strongman", d2, add1, null);
        ah3 = new AccountHolder("Pinocho", "woodman", "woodman", d2, add1, null);
        admin1 = new Admin("Dreamworks", "dreamworks", "dreamworks");
        party1 = new ThirdParty("Third", "third", "third", "third-hashkey");
        accountHolderRepository.saveAll(Stream.of(ah1, ah2, ah3).collect(Collectors.toList()));
        adminRepository.save(admin1);
        thirdPartyRepository.save(party1);
        ac1 = new CheckingAcc(ah1, ah2, new Money(new BigDecimal("5000")), Status.ACTIVE);
        ac2 = new CheckingAcc(ah1, ah3, new Money(new BigDecimal("1000")), Status.ACTIVE);
        checkingAccRepository.saveAll(Stream.of(ac1, ac2).collect(Collectors.toList()));
        cu1 = new CustomSecurityUser(new Admin("Dreamworks", "dreamworks", "dreamworks"));
        cu2 = new CustomSecurityUser(new AccountHolder("Simba", "kinglyon", "kinglyon", d1, add1, null));
        cu2.setId(ah1.getId());
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        checkingAccRepository.deleteAll();
        studentCheckingAccRepository.deleteAll();
        savingsAccRepository.deleteAll();
        creditCardAccRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/account-holders").with(user(cu1))).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().contains("Hercules");
    }

    @Test
    void findById() throws Exception {
        mockMvc.perform(get("/account-holders/" + ah1.getId()).with(user(cu1))).andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString().contains("Simba");
    }

    @Test
    void findAllAccountAsPrimaryOwnerById() throws Exception {
        MvcResult result = mockMvc.perform(get("/account-holders/" + ah1.getId() + "/accounts").with(user(cu1))).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].ownerName").value("Simba"))
                .andReturn();
    }

    @Test
    void create() throws Exception {
        AccountHolder ah4 = new AccountHolder("Cinderella", "cinderella", "cinderella", d1, add1, null);
        mockMvc.perform(post("/account-holders").with(user(cu1)).content(objectMapper.writeValueAsString(ah4))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Cinderella"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString().contains("Cinderella");
    }

    @Test
    void setLogged() throws Exception {
        mockMvc.perform(patch("/account-holders/logged-in/false").with(user(cu2))).andExpect(status().is2xxSuccessful());
    }

    @Test
    void transference() throws Exception {
        mockMvc.perform(post("/account-holders/transference/" + ac1.getId() + "?receiver-account-id=" + ac2.getId() + "&amount=400").with(user(cu2))).andExpect(status().is2xxSuccessful());
    }

    @Test
    void transference_Fraud() throws Exception {
        CreditCardAcc creditCardAcc = new CreditCardAcc(ah1, null, new Money(new BigDecimal("10000")), new BigDecimal("0.2"));
        creditCardAccRepository.save(creditCardAcc);
        mockMvc.perform(post("/account-holders/transference/" + creditCardAcc.getId() + "?receiver-account-id=" + ac1.getId() + "&amount=400").with(user(cu2))).andExpect(status().is2xxSuccessful());
        Transaction transaction = transactionRepository.findAll().get(0);
        transaction.setDate(new Date(System.currentTimeMillis()));
        transactionRepository.save(transaction);
        mockMvc.perform(post("/account-holders/transference/" + creditCardAcc.getId() + "?receiver-account-id=" + ac2.getId() + "&amount=400").with(user(cu2))).andExpect(status().isNotAcceptable());
    }

}