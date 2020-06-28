/**
 * Run individually
 **/

//package com.ironhack.MidtermProject.service.classes;
//
//import com.ironhack.MidtermProject.enums.Status;
//import com.ironhack.MidtermProject.enums.TransactionType;
//import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
//import com.ironhack.MidtermProject.model.classes.Address;
//import com.ironhack.MidtermProject.model.classes.Money;
//import com.ironhack.MidtermProject.model.classes.Transaction;
//import com.ironhack.MidtermProject.model.user.AccountHolder;
//import com.ironhack.MidtermProject.model.user.Admin;
//import com.ironhack.MidtermProject.model.user.ThirdParty;
//import com.ironhack.MidtermProject.repository.account.AccountRepository;
//import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
//import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
//import com.ironhack.MidtermProject.repository.user.AdminRepository;
//import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
//import com.ironhack.MidtermProject.service.account.AccountService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.math.BigDecimal;
//import java.sql.Date;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//class TransactionServiceTestUnit {
//
//    @Autowired
//    private TransactionService transactionService;
//
//    @MockBean(name = "transactionRepository")
//    private TransactionRepository transactionRepository;
//    @MockBean(name = "accountService")
//    private AccountService accountService;
//    @MockBean(name = "accountRepository")
//    private AccountRepository accountRepository;
//    @MockBean(name = "accountHolderRepository")
//    private AccountHolderRepository accountHolderRepository;
//    @MockBean(name = "thirdPartyRepository")
//    private ThirdPartyRepository thirdPartyRepository;
//    @MockBean(name = "adminRepository")
//    private AdminRepository adminRepository;
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    private MockMvc mockMvc;
//
//    Address add1;
//    Date d1, d2;
//    AccountHolder ah1, ah2, ah3;
//    Admin admin1;
//    ThirdParty party1;
//    StudentCheckingAcc ac1, ac2;
//    Transaction t1, t2;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
//        add1 = new Address("Spain", "Madrid", "Canal");
//        d1 = Date.valueOf("1980-10-12");
//        d2 = Date.valueOf("1982-05-18");
//        ah1 = new AccountHolder("Simba", "kinglyon", "kinglyon", d1, add1, null);
//        ah2 = new AccountHolder("Hercules", "strongman", "strongman", d2, add1, null);
//        ah3 = new AccountHolder("Pinocho", "woodman", "woodman", d2, add1, null);
//        admin1 = new Admin("Dreamworks", "dreamworks","dreamworks");
//        party1 = new ThirdParty("Third", "third", "third", "third-hashkey");
//        ac1 = new StudentCheckingAcc(ah1,ah2,new Money(new BigDecimal("1000")), Status.ACTIVE);
//        ac2 = new StudentCheckingAcc(ah3,null,new Money(new BigDecimal("3000")), Status.ACTIVE);
//        t1 = new Transaction(ah1.getId(), ac2, ac1, new Money(new BigDecimal("100")), TransactionType.TRANSFERENCE);
//        when(transactionRepository.findAll()).thenReturn(Collections.singletonList(t1));
//    }
//
//    @Test
//    void findAll(){
//        List<Transaction> transactions = transactionService.findAll();
//        assertEquals(1, transactions.size());
//    }
//
//    @Test
//    void checkTransaction_FirstOne(){
//        Transaction t3 = new Transaction(ah2.getId(), ac2, ac1, new Money(new BigDecimal("100")), TransactionType.TRANSFERENCE);
//        t3.setDate(new Date(System.currentTimeMillis() + 86400000l));
//        when(transactionRepository.findByRoleIdNotLikeAndNotOrderId("ADMIN", t1.getOrderingId()))
//                .thenReturn(Collections.singletonList(t1));
//        when(transactionRepository.findLastTransactionDate(ah2.getId()))
//                .thenReturn(new Date(System.currentTimeMillis()-800000000l));
//        assertTrue(transactionService.checkTransaction(t3));
//    }
//
//}