/**
 * Run individually
 **/

//package com.ironhack.MidtermProject.service.account;
//
//import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
//import com.ironhack.MidtermProject.exceptions.NoOwnerException;
//import com.ironhack.MidtermProject.exceptions.StatusException;
//import com.ironhack.MidtermProject.model.account.CreditCardAcc;
//import com.ironhack.MidtermProject.model.classes.Address;
//import com.ironhack.MidtermProject.model.classes.Money;
//import com.ironhack.MidtermProject.model.user.AccountHolder;
//import com.ironhack.MidtermProject.model.user.Admin;
//import com.ironhack.MidtermProject.model.user.ThirdParty;
//import com.ironhack.MidtermProject.repository.account.AccountRepository;
//import com.ironhack.MidtermProject.repository.account.CreditCardAccRepository;
//import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
//import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
//import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
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
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//class CreditCardAccServiceTestUnit {
//
//    @Autowired
//    private CreditCardAccService creditCardAccService;
//
//    @MockBean(name = "creditCardAccRepository")
//    private CreditCardAccRepository creditCardAccRepository;
//    @MockBean(name = "accountHolderRepository")
//    private AccountHolderRepository accountHolderRepository;
//    @MockBean(name = "accountRepository")
//    private AccountRepository accountRepository;
//    @MockBean(name = "transactionRepository")
//    private TransactionRepository transactionRepository;
//    @MockBean(name = "thirdPartyRepository")
//    private ThirdPartyRepository thirdPartyRepository;
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
//    CreditCardAcc ac1, ac2;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
//        add1 = new Address("Spain", "Madrid", "Canal");
//        d1 = Date.valueOf("1980-10-12");
//        d2 = Date.valueOf("1982-05-18");
//        ah1 = new AccountHolder("Simba", "kinglyon", "kinglyon", d1, add1, null);
//        ah1.setId(1);
//        ah2 = new AccountHolder("Hercules", "strongman", "strongman", d2, add1, null);
//        ah2.setId(2);
//        ah3 = new AccountHolder("Pinocho", "woodman", "woodman", d2, add1, null);
//        ah3.setId(3);
//        admin1 = new Admin("Dreamworks", "dreamworks","dreamworks");
//        party1 = new ThirdParty("Third", "third", "third", "third-hashkey");
//        party1.setId(5);
//        ac1 = new CreditCardAcc(ah1,ah2,new Money(new BigDecimal("5000")), new BigDecimal("0.2"));
//        ac1.setId(1);
//        ac2 = new CreditCardAcc(ah1,ah3,new Money(new BigDecimal("1000")), new BigDecimal("0.2"));
//        ac2.setId(2);
//        when(creditCardAccRepository.findAll()).thenReturn(Stream.of(ac1,ac2).collect(Collectors.toList()));
//        when(creditCardAccRepository.findById(ac1.getId())).thenReturn(Optional.of(ac1));
//        when(creditCardAccRepository.findById(ac2.getId())).thenReturn(Optional.of(ac2));
//        when(accountRepository.findById(ac1.getId())).thenReturn(Optional.of(ac1));
//        when(accountRepository.findById(ac2.getId())).thenReturn(Optional.of(ac2));
//        doAnswer(i->null).when(creditCardAccRepository).save(ac1);
//        doAnswer(i->null).when(creditCardAccRepository).save(ac2);
//        when(accountHolderRepository.findById(ah1.getId())).thenReturn(Optional.of(ah1));
//        when(accountHolderRepository.findById(ah2.getId())).thenReturn(Optional.of(ah2));
//        when(accountHolderRepository.findById(ah3.getId())).thenReturn(Optional.of(ah3));
//        CreditCardAcc ac3 = new CreditCardAcc(ah1,ah3,new Money(new BigDecimal("10000")), new BigDecimal("0.2"));
//        doAnswer(i->null).when(creditCardAccRepository).save(ac3);
//        when(thirdPartyRepository.findById(party1.getId())).thenReturn(Optional.of(party1));
//    }
//
//    @Test
//    void findAll(){
//        List<CreditCardAcc> creditCardAccs = creditCardAccService.findAll();
//        assertEquals(2, creditCardAccs.size());
//    }
//
//    @Test
//    void checkFindById_AdminAccess(){
//        assertEquals("Simba", creditCardAccService.checkFindById(ac2.getId(),admin1).getPrimaryOwner().getName());
//    }
//
//    @Test
//    void checkFindById_OwnerLoggedIn(){
//        ah1.setLoggedIn(true);
//        assertEquals("Simba", creditCardAccService.checkFindById(ac1.getId(),ah1).getPrimaryOwner().getName());
//    }
//
//    @Test
//    void checkFindById_OwnerNotLoggedIn_Exception(){
//        assertThrows(StatusException.class, ()->creditCardAccService.checkFindById(ah1.getId(),ah1));
//    }
//
//    @Test
//    void checkFindById_OwnerNotCorrect_Exception(){
//        assertThrows(NoOwnerException.class, ()->creditCardAccService.checkFindById(ac1.getId(),ah3));
//    }
//
//    @Test
//    void create_Correct(){
//        CreditCardAcc ac3 = new CreditCardAcc(ah1,ah3,new Money(new BigDecimal("10000")), new BigDecimal("0.2"));
//        assertEquals(null, creditCardAccService.create(ac3));
//    }
//
//    @Test
//    void reduceBalance(){
//        ah1.setLoggedIn(true);
//        creditCardAccService.creditBalance(ah1, ac1.getId(), new BigDecimal("1000"),null,  "abrakadabra", null);
//        CreditCardAcc creditCardAcc = creditCardAccService.findById(ac1.getId());
//        assertEquals(new BigDecimal("-1000.00"), creditCardAcc.getBalance().getAmount());
//    }
//
//    @Test
//    void reduceBalance_AccountNotFound(){
//        ah1.setLoggedIn(true);
//        assertThrows(IdNotFoundException.class, ()->creditCardAccService.creditBalance(ah1, ac2.getId()+100, new BigDecimal("1000"),null,  "abrakadabra", null));
//    }
//
//    @Test
//    void addBalance(){
//        ah1.setLoggedIn(true);
//        creditCardAccService.debitBalance(ah1, ac1.getId(), new BigDecimal("1000"),null,  "abrakadabra", null);
//        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(ac1.getId()).get();
//        assertEquals(new BigDecimal("1000.00"), creditCardAcc.getBalance().getAmount());
//    }
//
//    @Test
//    void addBalance_AccountNotFound(){
//        ah1.setLoggedIn(true);
//        assertThrows(IdNotFoundException.class, ()->creditCardAccService.debitBalance(ah1, ac2.getId()+100, new BigDecimal("1000"),null,  "abrakadabra", null));
//    }
//
//    @Test
//    void checkAllowance_CorrectOwner(){
//        ah1.setLoggedIn(true);
//        creditCardAccService.checkAllowance(ah1, ac1.getId(),"abrakadabra","ashd");
//    }
//
//    @Test
//    void checkAllowance_Admin(){
//        creditCardAccService.checkAllowance(admin1, ac1.getId(),"aabra","ashd");
//    }
//
//}