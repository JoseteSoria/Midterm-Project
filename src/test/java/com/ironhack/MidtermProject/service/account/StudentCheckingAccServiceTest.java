package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StudentCheckingAccServiceTest {
    @Autowired
    private StudentCheckingAccService studentCheckingAccService;
    @Autowired
    private StudentCheckingAccRepository studentCheckingAccRepository;
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

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2, ah3;
    Admin admin1;
    ThirdParty party1;
    StudentCheckingAcc ac1, ac2;

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
        ac1 = new StudentCheckingAcc(ah1,ah2,new Money(new BigDecimal("5000")), Status.ACTIVE);
        ac2 = new StudentCheckingAcc(ah1,ah3,new Money(new BigDecimal("1000")), Status.ACTIVE);
        studentCheckingAccRepository.saveAll(Stream.of(ac1, ac2).collect(Collectors.toList()));
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        studentCheckingAccRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    void findAll(){
        List<StudentCheckingAcc> studentCheckingAccs = studentCheckingAccService.findAll();
        assertEquals(2, studentCheckingAccs.size());
    }

    @Test
    void checkFindById_AdminAccess(){
        StudentCheckingAcc ac3 = new StudentCheckingAcc(ah1,ah3,new Money(new BigDecimal("10000")), Status.ACTIVE);
        studentCheckingAccRepository.save(ac3);
        assertEquals("Simba", studentCheckingAccService.checkFindById(ac3.getId(),admin1).getPrimaryOwner().getName());
    }

    @Test
    void checkFindById_OwnerLoggedIn(){
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        accountHolderRepository.save(accountHolder);
        StudentCheckingAcc ac3 = new StudentCheckingAcc(accountHolder,ah3,new Money(new BigDecimal("10000")), Status.ACTIVE);
        studentCheckingAccRepository.save(ac3);
        assertEquals("Simba", studentCheckingAccService.checkFindById(ac3.getId(),accountHolder).getPrimaryOwner().getName());
    }

    @Test
    void checkFindById_OwnerNotLoggedIn_Exception(){
        StudentCheckingAcc ac3 = new StudentCheckingAcc(ah1,ah3,new Money(new BigDecimal("10000")), Status.ACTIVE);
        studentCheckingAccRepository.save(ac3);
        assertThrows(StatusException.class, ()->studentCheckingAccService.checkFindById(ac3.getId(),ah1));
    }


    @Test
    void checkFindById_OwnerNotCorrect_Exception(){
        StudentCheckingAcc ac3 = new StudentCheckingAcc(ah1,ah3,new Money(new BigDecimal("10000")), Status.ACTIVE);
        studentCheckingAccRepository.save(ac3);
        assertThrows(NoOwnerException.class, ()->studentCheckingAccService.checkFindById(ac3.getId(),ah2));
    }

    @Test
    void reduceBalance(){
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        studentCheckingAcc.setSecretKey("abrakadabra");
        accountHolderRepository.save(accountHolder);
        studentCheckingAccRepository.save(studentCheckingAcc);
        studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        studentCheckingAccService.reduceBalance(ah1, studentCheckingAcc.getId(), new BigDecimal("1000"),null,  "abrakadabra", null);
        studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("4000.00"), studentCheckingAcc.getBalance().getAmount());
    }

    @Test
    void reduceBalance_AccountNotFound(){
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        studentCheckingAcc.setSecretKey("abrakadabra");
        accountHolderRepository.save(accountHolder);
        studentCheckingAccRepository.save(studentCheckingAcc);
        StudentCheckingAcc studentCheckingAcc1 = studentCheckingAccRepository.findById(ac1.getId()).get();
        assertThrows(IdNotFoundException.class, ()->studentCheckingAccService.reduceBalance(ah1, studentCheckingAcc1.getId()+100, new BigDecimal("1000"),null,  "abrakadabra", null));
    }

    @Test
    void addBalance(){
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        studentCheckingAcc.setSecretKey("abrakadabra");
        accountHolderRepository.save(accountHolder);
        studentCheckingAccRepository.save(studentCheckingAcc);
        studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        studentCheckingAccService.addBalance(ah1, studentCheckingAcc.getId(), new BigDecimal("1000"),null,  "abrakadabra", null);
        studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("6000.00"), studentCheckingAcc.getBalance().getAmount());
    }

    @Test
    void addBalance_AccountNotFound(){
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        studentCheckingAcc.setSecretKey("abrakadabra");
        accountHolderRepository.save(accountHolder);
        studentCheckingAccRepository.save(studentCheckingAcc);
        StudentCheckingAcc studentCheckingAcc1 = studentCheckingAccRepository.findById(ac1.getId()).get();
        assertThrows(IdNotFoundException.class, ()->studentCheckingAccService.addBalance(ah1, studentCheckingAcc1.getId()+100, new BigDecimal("1000"),null,  "abrakadabra", null));
    }




    @Test
    void checkAllowance_CorrectOwner(){
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        studentCheckingAcc.setSecretKey("abrakadabra");
        accountHolderRepository.save(accountHolder);
        studentCheckingAccRepository.save(studentCheckingAcc);
        studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        studentCheckingAccService.checkAllowance(studentCheckingAcc.getPrimaryOwner(), ac1.getId(),"abrakadabra","ashd");
    }

    @Test
    void checkAllowance_Admin(){
        StudentCheckingAcc checkingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        studentCheckingAccRepository.save(checkingAcc);
        studentCheckingAccService.checkAllowance(admin1, ac1.getId(),"aabra","ashd");
    }

    @Test
    void checkAllowance_FrozenAccount(){
        StudentCheckingAcc checkingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        checkingAcc.setSecretKey("abrakadabra");
        checkingAcc.setStatus(Status.FROZEN);
        accountHolderRepository.save(accountHolder);
        studentCheckingAccRepository.save(checkingAcc);
        StudentCheckingAcc checkingAcc1 = studentCheckingAccRepository.findById(ac1.getId()).get();
        assertThrows(StatusException.class, ()->studentCheckingAccService.checkAllowance(accountHolder, checkingAcc1.getId(),"abrakadabra","ashd"));
    }

    @Test
    void checkAllowance_HashKeyWrong_Exception(){
        StudentCheckingAcc checkingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        checkingAcc.setSecretKey("abrakadabra");
        studentCheckingAccRepository.save(checkingAcc);
        ac1 = studentCheckingAccRepository.findById(ac1.getId()).get();
        assertThrows(NoOwnerException.class, ()->studentCheckingAccService.checkAllowance(party1, ac1.getId(),"askd","third-hashkey"));
    }

    @Test
    void changeStatus_Correct(){
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        studentCheckingAccService.changeStatus(studentCheckingAcc.getId(),"FROZEN");
        StudentCheckingAcc studentCheckingAcc1 = studentCheckingAccRepository.findById(studentCheckingAcc.getId()).get();
        assertEquals(Status.FROZEN, studentCheckingAcc1.getStatus());
    }

    @Test
    void changeStatus_Already(){
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        assertThrows(StatusException.class, ()->studentCheckingAccService.changeStatus(studentCheckingAcc.getId(),"ACTIVE"));
    }

    @Test
    void changeStatus_NoStatus(){
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        assertThrows(StatusException.class, ()->studentCheckingAccService.changeStatus(studentCheckingAcc.getId(),"SDFIS"));
    }

    @Test
    void changeStatus_AccountNotFound(){
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(ac1.getId()).get();
        assertThrows(IdNotFoundException.class, ()->studentCheckingAccService.changeStatus(studentCheckingAcc.getId()+100,"FROZEN"));
    }
}