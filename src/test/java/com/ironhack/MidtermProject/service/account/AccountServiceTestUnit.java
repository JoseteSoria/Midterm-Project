/**
 * Run individually
 **/

//package com.ironhack.MidtermProject.service.account;
//
//import com.ironhack.MidtermProject.enums.Status;
//import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
//import com.ironhack.MidtermProject.exceptions.NoOwnerException;
//import com.ironhack.MidtermProject.exceptions.StatusException;
//import com.ironhack.MidtermProject.model.account.Account;
//import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
//import com.ironhack.MidtermProject.model.classes.Address;
//import com.ironhack.MidtermProject.model.classes.Money;
//import com.ironhack.MidtermProject.model.user.AccountHolder;
//import com.ironhack.MidtermProject.model.user.Admin;
//import com.ironhack.MidtermProject.model.user.ThirdParty;
//import com.ironhack.MidtermProject.repository.account.AccountRepository;
//import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
//import com.ironhack.MidtermProject.repository.user.AdminRepository;
//import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.math.BigDecimal;
//import java.sql.Date;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//class AccountServiceTestUnit {
//
//    @Autowired
//    private AccountService accountService;
//
//    @Autowired
//    private AdminRepository adminRepository;
//
//    @MockBean(name = "accountRepository")
//    private AccountRepository accountRepository;
//    @MockBean(name = "thirdPartyRepository")
//    private ThirdPartyRepository thirdPartyRepository;
//    @MockBean(name = "accountHolderRepository")
//    private AccountHolderRepository accountHolderRepository;
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    private MockMvc mockMvc;
//
//    Address add1;
//    Date d1, d2;
//    AccountHolder ah1, ah2;
//    Admin admin1;
//    ThirdParty party1;
//    Account ac1;
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
//        admin1 = new Admin("Dreamworks", "dreamworks","dreamworks");
//        admin1.setId(3);
//        party1 = new ThirdParty("Third", "third", "third", "third-hashkey");
//        party1.setId(4);
//        ac1 = new StudentCheckingAcc(ah1,ah2,new Money(new BigDecimal("1000")), Status.ACTIVE);
//        ac1.setId(1);
//        when(accountRepository.findAll()).thenReturn(Collections.singletonList(ac1));
//        when(accountRepository.findById(ac1.getId())).thenReturn(Optional.of(ac1));
//        when(thirdPartyRepository.findById(party1.getId())).thenReturn(Optional.of(party1));
//        when(accountHolderRepository.findById(ah1.getId())).thenReturn(Optional.of(ah1));
//        when(accountHolderRepository.findById(ah2.getId())).thenReturn(Optional.of(ah2));
//    }
//
//
//    @Test
//    void findAll(){
//        List<Account> accounts = accountService.findAllAccount();
//        assertEquals(1, accounts.size());
//    }
//
//    @Test
//    void findById(){
//        assertEquals("Simba", accountService.findById(ac1.getId()).getPrimaryOwner().getName());
//
//    }
//
//    @Test
//    void checkLoggedInTest(){
//        Account account = accountRepository.findById(ac1.getId()).get();
//        AccountHolder accountHolder = account.getPrimaryOwner();
//        assertEquals(false, accountService.checkLoggedIn(accountHolder,account));
//        accountHolder.setLoggedIn(true);
//        assertEquals(true, accountService.checkLoggedIn(accountHolder,account));
//    }
//
//    @Test
//    void checkAllowance_NotLoggedIn_Exception(){
//        AccountHolder accountHolder = ac1.getPrimaryOwner();
//        assertThrows(StatusException.class, ()->accountService.checkAllowance(accountHolder, ac1.getId(),"askd","ashd"));
//    }
//
//    @Test
//    void checkAllowance_LoggedIn(){
//        ah1.setLoggedIn(true);
//        Account account1 = accountRepository.findById(ac1.getId()).get();
//        accountService.checkAllowance(account1.getPrimaryOwner(), ac1.getId(),"askd","ashd");
//    }
//
//    @Test
//    void checkAllowance_NoOwner_Exception(){
//        AccountHolder ah3 = new AccountHolder("Pinocho", "woodman", "woodman", d2, add1, null);
//        assertThrows(NoOwnerException.class, ()->accountService.checkAllowance(ah3, ac1.getId(),"askd","ashd"));
//    }
//
//    @Test
//    void checkAllowance_NoThirdParty_Exception(){
//        ThirdParty party2 = new ThirdParty("Amazon", "amazon", "amazon", "amazon-hashkey");
//        party2.setId(thirdPartyRepository.findById(party1.getId()).get().getId()+1);
//        assertThrows(IdNotFoundException.class, ()->accountService.checkAllowance(party2, ac1.getId(),"askd","ashd"));
//    }
//
//    @Test
//    void checkAllowance_NoHeaderOrSecretKey_Exception(){
//        assertThrows(NoOwnerException.class, ()->accountService.checkAllowance(party1, ac1.getId(),null,null));
//    }
//
//    @Test
//    void checkAllowance_HashKeyWrong_Exception(){
//        assertThrows(NoOwnerException.class, ()->accountService.checkAllowance(party1, ac1.getId(),"askd","aksdg"));
//    }
//
//    @Test
//    void checkAllowance_ThirdCorrect_Correct(){
//        accountService.checkAllowance(party1, ac1.getId(),"askd","third-hashkey");
//    }
//
//    @Test
//    void checkAllowance_Admin_Correct(){
//        accountService.checkAllowance(admin1, ac1.getId(),"askd","ty");
//    }
//
//    @Test
//    void checkOwner_BothCorrect(){
//        AccountHolder[] accountHolders = accountService.checkOwner(ac1);
//        assertEquals(ac1.getPrimaryOwner().getName(), accountHolders[0].getName());
//        assertEquals(ac1.getSecondaryOwner().getName(), accountHolders[1].getName());
//    }
//
//    @Test
//    void checkOwner_PrimOwnerNotFound_Exception(){
//        AccountHolder ah3 = new AccountHolder("Little Mermaid", "mermaid", "mermaid", d1, add1, null);
//        ah3.setId(4);
//        Account ac2 = new StudentCheckingAcc(ah3,null,new Money(new BigDecimal("1000")), Status.ACTIVE);
//        assertThrows(IdNotFoundException.class,()->accountService.checkOwner(ac2));
//    }
//
//    @Test
//    void checkOwner_NewPrimOwner_Exception(){
//        AccountHolder ah3 = new AccountHolder("Little Mermaid", "mermaid", "mermaid", d1, add1, null);
//        Account ac2 = new StudentCheckingAcc(ah3,null,new Money(new BigDecimal("1000")), Status.ACTIVE);
//        AccountHolder[] accountHolders = accountService.checkOwner(ac2);
//        assertEquals(ac2.getPrimaryOwner().getName(), accountHolders[0].getName());
//    }
//
//    @Test
//    void checkOwner_SecOwnerNotFound_Exception(){
//        AccountHolder ah3 = new AccountHolder("Little Mermaid", "mermaid", "mermaid", d1, add1, null);
//        ah3.setId(4);
//        Account ac2 = new StudentCheckingAcc(ah2,ah3,new Money(new BigDecimal("1000")), Status.ACTIVE);
//        assertThrows(IdNotFoundException.class,()->accountService.checkOwner(ac2));
//    }
//
//    @Test
//    void checkOwner_NewSecOwner_Exception(){
//        AccountHolder ah3 = new AccountHolder("Little Mermaid", "mermaid", "mermaid", d1, add1, null);
//        Account ac2 = new StudentCheckingAcc(ah2,ah3,new Money(new BigDecimal("1000")), Status.ACTIVE);
//        AccountHolder[] accountHolders = accountService.checkOwner(ac2);
//        assertEquals(ac2.getSecondaryOwner().getName(), accountHolders[1].getName());
//    }
//}