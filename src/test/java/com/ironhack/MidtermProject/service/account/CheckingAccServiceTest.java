package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.dto.CheckingAccCreation;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.account.AccountRepository;
import com.ironhack.MidtermProject.repository.account.CheckingAccRepository;
import com.ironhack.MidtermProject.repository.account.StudentCheckingAccRepository;
import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.repository.user.AdminRepository;
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CheckingAccServiceTest {
    @Autowired
    private CheckingAccService checkingAccService;
    @Autowired
    private CheckingAccRepository checkingAccRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private StudentCheckingAccRepository studentCheckingAccRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2, ah3;
    Admin admin1;
    ThirdParty party1;
    CheckingAcc ac1, ac2;

    @BeforeEach
    void setUp() {
        add1 = new Address("Spain", "Madrid", "Canal");
        d1 = Date.valueOf("1980-10-12");
        d2 = Date.valueOf("1982-05-18");
        ah1 = new AccountHolder("Simba", "kinglyon", "kinglyon", d1, add1, null);
        ah2 = new AccountHolder("Hercules", "strongman", "strongman", d2, add1, null);
        ah3 = new AccountHolder("Pinocho", "woodman", "woodman", d2, add1, null);
        admin1 = new Admin("Dreamworks", "dreamworks","dreamworks");
        party1 = new ThirdParty("Third", "third", "third", "third-hashkey");
        accountHolderRepository.saveAll(Stream.of(ah1, ah2, ah3).collect(Collectors.toList()));
        adminRepository.save(admin1);
        thirdPartyRepository.save(party1);
        ac1 = new CheckingAcc(ah1,ah2,new Money(new BigDecimal("5000")), Status.ACTIVE);
        ac2 = new CheckingAcc(ah1,ah3,new Money(new BigDecimal("1000")), Status.ACTIVE);
        checkingAccRepository.saveAll(Stream.of(ac1, ac2).collect(Collectors.toList()));
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        checkingAccRepository.deleteAll();
        studentCheckingAccRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    void findAll(){
        List<CheckingAcc> checkingAccServices = checkingAccService.findAll();
        assertEquals(2, checkingAccServices.size());
    }

    @Test
    void checkFindById_AdminAccess(){
        CheckingAcc ac3 = new CheckingAcc(ah1,ah3,new Money(new BigDecimal("10000")), Status.ACTIVE);
        ac3.setDateMonthlyMaintenance(new Date(System.currentTimeMillis()));
        checkingAccRepository.save(ac3);
        assertEquals("Simba", checkingAccService.checkFindById(ac3.getId(),admin1).getPrimaryOwner().getName());
    }

    @Test
    void checkFindById_OwnerLoggedIn(){
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        accountHolderRepository.save(accountHolder);
        CheckingAcc ac3 = new CheckingAcc(accountHolder,ah3,new Money(new BigDecimal("10000")), Status.ACTIVE);
        ac3.setDateMonthlyMaintenance(new Date(System.currentTimeMillis()));
        checkingAccRepository.save(ac3);
        assertEquals("Simba", checkingAccService.checkFindById(ac3.getId(),ah1).getPrimaryOwner().getName());
    }

    @Test
    void checkFindById_OwnerNotLoggedIn_Exception(){
        CheckingAcc ac3 = new CheckingAcc(ah1,ah3,new Money(new BigDecimal("10000")), Status.ACTIVE);
        ac3.setDateMonthlyMaintenance(new Date(System.currentTimeMillis()));
        checkingAccRepository.save(ac3);
        assertThrows(StatusException.class, ()->checkingAccService.checkFindById(ac3.getId(),ah1));
    }


    @Test
    void checkFindById_OwnerNotCorrect_Exception(){
        CheckingAcc ac3 = new CheckingAcc(ah1,ah3,new Money(new BigDecimal("10000")), Status.ACTIVE);
        ac3.setDateMonthlyMaintenance(new Date(System.currentTimeMillis()));
        checkingAccRepository.save(ac3);
        assertThrows(NoOwnerException.class, ()->checkingAccService.checkFindById(ac3.getId(),ah2));
    }

    @Test
    void create_OlderThan24(){
        CheckingAccCreation ac3 = new CheckingAccCreation(ah1,ah3,new Money(new BigDecimal("15000")));
        CheckingAccCreation creation = checkingAccService.create(ac3);
        assertEquals(3, checkingAccService.findAll().size());
        assertEquals("Simba", checkingAccRepository.findById(ac2.getId()+1).get().getPrimaryOwner().getName());
    }

    @Test
    void create_YoungerThan24(){
        Date d3 = new Date(System.currentTimeMillis()-31556926l*16);
        AccountHolder ah4 = new AccountHolder("Mowgli", "mowgli", "mowgli", d3, add1, null);
        accountHolderRepository.save(ah4);
        CheckingAccCreation ac3 = new CheckingAccCreation(ah4,ah3,new Money(new BigDecimal("15000")));
        CheckingAccCreation creation = checkingAccService.create(ac3);
        assertEquals(1, studentCheckingAccRepository.findAll().size());
        assertEquals("Mowgli", studentCheckingAccRepository.findById(ac2.getId()+1).get().getPrimaryOwner().getName());
    }

    @Test
    void reduceBalance(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        checkingAcc.setSecretKey("abrakadabra");
        checkingAcc.setDateMonthlyMaintenance(new Date(System.currentTimeMillis()-86400000));
        accountHolderRepository.save(accountHolder);
        checkingAccRepository.save(checkingAcc);
        checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        checkingAccService.reduceBalance(ah1, checkingAcc.getId(), new BigDecimal("1000"),null,  "abrakadabra", null);
        checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("4000.00"), checkingAcc.getBalance().getAmount());
    }

    @Test
    void reduceBalance_AccountNotFound(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        checkingAcc.setSecretKey("abrakadabra");
        checkingAcc.setDateMonthlyMaintenance(new Date(System.currentTimeMillis()-86400000));
        accountHolderRepository.save(accountHolder);
        checkingAccRepository.save(checkingAcc);
        CheckingAcc checkingAcc1 = checkingAccRepository.findById(ac1.getId()).get();
        assertThrows(IdNotFoundException.class, ()->checkingAccService.reduceBalance(ah1, checkingAcc1.getId()+100, new BigDecimal("1000"),null,  "abrakadabra", null));
    }

//    @Test
//    void reduceBalance_AccountFrozen(){
//        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
//        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
//        accountHolder.setLoggedIn(true);
//        checkingAcc.setSecretKey("abrakadabra");
//        checkingAcc.setDateMonthlyMaintenance(new Date(System.currentTimeMillis()-86400000));
//        accountHolderRepository.save(accountHolder);
//        checkingAccRepository.save(checkingAcc);
//        checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
//        for(int i = 0; i<8; i++) {
//            checkingAccService.reduceBalance(ah1, checkingAcc.getId(), new BigDecimal("1000"), null, "abrakadabra", null);
//        }
//        checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
//        assertEquals(Status.FROZEN, checkingAcc.getStatus());
//    }

    @Test
    void addBalance(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        checkingAcc.setSecretKey("abrakadabra");
        checkingAcc.setDateMonthlyMaintenance(new Date(System.currentTimeMillis()-86400000));
        accountHolderRepository.save(accountHolder);
        checkingAccRepository.save(checkingAcc);
        checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        checkingAccService.addBalance(ah1, checkingAcc.getId(), new BigDecimal("1000"),null,  "abrakadabra", null);
        checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("6000.00"), checkingAcc.getBalance().getAmount());
    }

    @Test
    void addBalance_AccountNotFound(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        checkingAcc.setSecretKey("abrakadabra");
        checkingAcc.setDateMonthlyMaintenance(new Date(System.currentTimeMillis()-86400000));
        accountHolderRepository.save(accountHolder);
        checkingAccRepository.save(checkingAcc);
        CheckingAcc checkingAcc1 = checkingAccRepository.findById(ac1.getId()).get();
        assertThrows(IdNotFoundException.class, ()->checkingAccService.addBalance(ah1, checkingAcc1.getId()+100, new BigDecimal("1000"),null,  "abrakadabra", null));
    }

    @Test
    void checkAllowance_CorrectOwner(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        checkingAcc.setSecretKey("abrakadabra");
        checkingAcc.setDateMonthlyMaintenance(new Date(System.currentTimeMillis()-86400000));
        accountHolderRepository.save(accountHolder);
        checkingAccRepository.save(checkingAcc);
        checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        checkingAccService.checkAllowance(checkingAcc.getPrimaryOwner(), ac1.getId(),"abrakadabra","ashd");
    }

    @Test
    void checkAllowance_Admin(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        checkingAcc.setDateMonthlyMaintenance(new Date(System.currentTimeMillis()-86400000));
        checkingAccRepository.save(checkingAcc);
        checkingAccService.checkAllowance(admin1, ac1.getId(),"aabra","ashd");
    }

    @Test
    void checkAllowance_FrozenAccount(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        checkingAcc.setSecretKey("abrakadabra");
        checkingAcc.setStatus(Status.FROZEN);
        checkingAcc.setDateMonthlyMaintenance(new Date(System.currentTimeMillis()-86400000));
        accountHolderRepository.save(accountHolder);
        checkingAccRepository.save(checkingAcc);
        checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        AccountHolder user = checkingAcc.getPrimaryOwner();
        assertThrows(StatusException.class, ()->checkingAccService.checkAllowance(user, ac1.getId(),"abrakadabra","ashd"));
    }

    @Test
    void checkAllowance_HashKeyWrong_Exception(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        checkingAcc.setSecretKey("abrakadabra");
        checkingAccRepository.save(checkingAcc);
        ac1 = checkingAccRepository.findById(ac1.getId()).get();
        assertThrows(NoOwnerException.class, ()->checkingAccService.checkAllowance(party1, ac1.getId(),"askd","third-hashkey"));
    }

    @Test
    void changeStatus_Correct(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        checkingAccService.changeStatus(checkingAcc.getId(),"FROZEN");
        CheckingAcc checkingAcc1 = checkingAccRepository.findById(checkingAcc.getId()).get();
        assertEquals(Status.FROZEN, checkingAcc1.getStatus());
    }

    @Test
    void changeStatus_Already(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertThrows(StatusException.class, ()->checkingAccService.changeStatus(checkingAcc.getId(),"ACTIVE"));
    }

    @Test
    void changeStatus_NoStatus(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertThrows(StatusException.class, ()->checkingAccService.changeStatus(checkingAcc.getId(),"SDFIS"));
    }

    @Test
    void changeStatus_AccountNotFound(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertThrows(IdNotFoundException.class, ()->checkingAccService.changeStatus(checkingAcc.getId()+100,"FROZEN"));
    }

}