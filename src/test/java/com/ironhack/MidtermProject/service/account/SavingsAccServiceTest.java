package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SavingsAccServiceTest {
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

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2, ah3;
    Admin admin1;
    ThirdParty party1;
    SavingsAcc ac1, ac2;

    @BeforeEach
    void setUp() {
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
    void findAll() {
        List<SavingsAcc> savingsAccs = savingsAccService.findAll();
        assertEquals(2, savingsAccs.size());
    }

    @Test
    void checkFindById_AdminAcces() {
        SavingsAcc ac3 = new SavingsAcc(ah1, ah3, new Money(new BigDecimal("10000")), Status.ACTIVE);
        ac3.setDateInterestRate(new Date(System.currentTimeMillis()));
        savingsAccRepository.save(ac3);
        assertEquals("Simba", savingsAccService.checkFindById(ac3.getId(), admin1).getPrimaryOwner().getName());
    }

    @Test
    void checkFindById_OwnerLoggedIn() {
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        accountHolderRepository.save(accountHolder);
        SavingsAcc ac3 = new SavingsAcc(accountHolder, ah3, new Money(new BigDecimal("10000")), Status.ACTIVE);
        ac3.setDateInterestRate(new Date(System.currentTimeMillis()));
        savingsAccRepository.save(ac3);
        assertEquals("Simba", savingsAccService.checkFindById(ac3.getId(), ah1).getPrimaryOwner().getName());
    }

    @Test
    void checkFindById_OwnerNotLoggedIn_Exception() {
        SavingsAcc ac3 = new SavingsAcc(ah1, ah3, new Money(new BigDecimal("10000")), Status.ACTIVE);
        ac3.setDateInterestRate(new Date(System.currentTimeMillis()));
        savingsAccRepository.save(ac3);
        assertThrows(StatusException.class, () -> savingsAccService.checkFindById(ac3.getId(), ah1));
    }

    @Test
    void checkFindById_OwnerNotCorrect_Exception() {
        SavingsAcc ac3 = new SavingsAcc(ah1, ah3, new Money(new BigDecimal("10000")), Status.ACTIVE);
        ac3.setDateInterestRate(new Date(System.currentTimeMillis()));
        savingsAccRepository.save(ac3);
        assertThrows(NoOwnerException.class, () -> savingsAccService.checkFindById(ac3.getId(), ah2));
    }

    @Test
    void create_Correct() {
        SavingsAcc ac3 = new SavingsAcc(ah1, ah3, new Money(new BigDecimal("15000")), Status.ACTIVE);
        SavingsAcc creation = savingsAccService.create(ac3);
        assertEquals(3, savingsAccService.findAll().size());
        assertEquals("Simba", savingsAccRepository.findById(ac2.getId() + 1).get().getPrimaryOwner().getName());
    }

    @Test
    void reduceBalance() {
        SavingsAcc savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        savingsAcc.setSecretKey("abrakadabra");
        savingsAcc.setDateInterestRate(new Date(System.currentTimeMillis() - 86400000));
        accountHolderRepository.save(accountHolder);
        savingsAccRepository.save(savingsAcc);
        savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        savingsAccService.reduceBalance(ah1, savingsAcc.getId(), new BigDecimal("1000"), null, "abrakadabra", null);
        savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("4000.00"), savingsAcc.getBalance().getAmount());
    }

    @Test
    void reduceBalance_AccountNotFound() {
        SavingsAcc savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        savingsAcc.setSecretKey("abrakadabra");
        savingsAcc.setDateInterestRate(new Date(System.currentTimeMillis() - 86400000));
        accountHolderRepository.save(accountHolder);
        savingsAccRepository.save(savingsAcc);
        SavingsAcc savingsAcc1 = savingsAccRepository.findById(ac1.getId()).get();
        assertThrows(IdNotFoundException.class, () -> savingsAccService.reduceBalance(ah1, savingsAcc1.getId() + 100, new BigDecimal("1000"), null, "abrakadabra", null));
    }

    @Test
    void addBalance() {
        SavingsAcc savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        savingsAcc.setSecretKey("abrakadabra");
        savingsAcc.setDateInterestRate(new Date(System.currentTimeMillis() - 86400000));
        accountHolderRepository.save(accountHolder);
        savingsAccRepository.save(savingsAcc);
        savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        savingsAccService.addBalance(ah1, savingsAcc.getId(), new BigDecimal("1000"), null, "abrakadabra", null);
        savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("6000.00"), savingsAcc.getBalance().getAmount());
    }

    @Test
    void addBalance_AccountNotFound() {
        SavingsAcc savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        savingsAcc.setSecretKey("abrakadabra");
        savingsAcc.setDateInterestRate(new Date(System.currentTimeMillis() - 86400000));
        accountHolderRepository.save(accountHolder);
        savingsAccRepository.save(savingsAcc);
        SavingsAcc savingsAcc1 = savingsAccRepository.findById(ac1.getId()).get();
        assertThrows(IdNotFoundException.class, () -> savingsAccService.addBalance(ah1, savingsAcc1.getId() + 100, new BigDecimal("1000"), null, "abrakadabra", null));
    }


    @Test
    void checkAllowance_CorrectOwner() {
        SavingsAcc savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        savingsAcc.setSecretKey("abrakadabra");
        savingsAcc.setDateInterestRate(new Date(System.currentTimeMillis() - 86400000));
        accountHolderRepository.save(accountHolder);
        savingsAccRepository.save(savingsAcc);
        savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        savingsAccService.checkAllowance(savingsAcc.getPrimaryOwner(), ac1.getId(), "abrakadabra", "ashd");
    }

    @Test
    void checkAllowance_Admin() {
        SavingsAcc savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        savingsAcc.setDateInterestRate(new Date(System.currentTimeMillis() - 86400000));
        savingsAccRepository.save(savingsAcc);
        savingsAccService.checkAllowance(admin1, ac1.getId(), "aabra", "ashd");
    }

    @Test
    void checkAllowance_FrozenAccount() {
        SavingsAcc savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        savingsAcc.setSecretKey("abrakadabra");
        savingsAcc.setStatus(Status.FROZEN);
        savingsAcc.setDateInterestRate(new Date(System.currentTimeMillis() - 86400000));
        accountHolderRepository.save(accountHolder);
        savingsAccRepository.save(savingsAcc);
        savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        AccountHolder user = savingsAcc.getPrimaryOwner();
        assertThrows(StatusException.class, () -> savingsAccService.checkAllowance(user, ac1.getId(), "abrakadabra", "ashd"));
    }

    @Test
    void checkAllowance_HashKeyWrong_Exception() {
        SavingsAcc savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        savingsAcc.setSecretKey("abrakadabra");
        savingsAccRepository.save(savingsAcc);
        ac1 = savingsAccRepository.findById(ac1.getId()).get();
        assertThrows(NoOwnerException.class, () -> savingsAccService.checkAllowance(party1, ac1.getId(), "askd", "third-hashkey"));
    }

    @Test
    void changeStatus_Correct() {
        SavingsAcc savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        savingsAccService.changeStatus(savingsAcc.getId(), "FROZEN");
        SavingsAcc savingsAcc1 = savingsAccRepository.findById(savingsAcc.getId()).get();
        assertEquals(Status.FROZEN, savingsAcc1.getStatus());
    }

    @Test
    void changeStatus_Already() {
        SavingsAcc savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        assertThrows(StatusException.class, () -> savingsAccService.changeStatus(savingsAcc.getId(), "ACTIVE"));
    }

    @Test
    void changeStatus_NoStatus() {
        SavingsAcc savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        assertThrows(StatusException.class, () -> savingsAccService.changeStatus(savingsAcc.getId(), "SDFIS"));
    }

    @Test
    void changeStatus_AccountNotFound() {
        SavingsAcc savingsAcc = savingsAccRepository.findById(ac1.getId()).get();
        assertThrows(IdNotFoundException.class, () -> savingsAccService.changeStatus(savingsAcc.getId() + 100, "FROZEN"));
    }
}