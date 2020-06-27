package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.dto.AccountMainFields;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.account.*;
import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.repository.user.AdminRepository;
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
import com.ironhack.MidtermProject.service.account.CheckingAccService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountHolderServiceTest {
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
        ah1.setLoggedIn(true);
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
        savingsAccRepository.deleteAll();
        creditCardAccRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    void findAll(){
        List<AccountHolder> accountHolders = accountHolderService.findAll();
        assertEquals(3, accountHolders.size());
    }

    @Test
    void checkFindById_AdminAccess(){
        assertEquals("Simba", accountHolderService.checkFindById(ah1.getId(),admin1).getName());
    }

    @Test
    void checkFindById_OwnerAccess(){
        assertEquals("Simba", accountHolderService.checkFindById(ah1.getId(),ah1).getName());
    }

    @Test
    void checkFindById_NotOwnerAccess_Exception(){
        assertThrows(NoOwnerException.class, ()->accountHolderService.checkFindById(ah1.getId(),ah2).getName());
    }

    @Test
    void checkFindById_ThirdPartyAccess_Exception(){
        assertThrows(NoOwnerException.class, ()->accountHolderService.checkFindById(ah1.getId(),party1).getName());
    }

    @Test
    void create(){
        AccountHolder ah4 = new AccountHolder("Balu", "mowglifriend", "mowglifriend", d2, add1, null);
        AccountHolder result = accountHolderService.store(ah4);
        assertEquals("Balu", result.getName());
    }

    @Test
    void findAllAccountAsPrimaryOwnerById(){
        List<AccountMainFields> accountMainFieldsList = accountHolderService.findAllAccountAsPrimaryOwnerById(ah1.getId(), ah1);
        assertEquals(2, accountMainFieldsList.size());
    }

    @Test
    void findAllAccountAsPrimaryOwnerById_NotOwner(){
        assertThrows(NoOwnerException.class, ()->accountHolderService.findAllAccountAsPrimaryOwnerById(ah1.getId(), ah2));
    }

    @Test
    void setLogged_Correct(){
        accountHolderService.setLogged(ah1, false);
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        assertEquals(false, accountHolder.isLoggedIn());
    }

    @Test
    void setLogged_Already_Exception(){
        assertThrows(StatusException.class, ()->accountHolderService.setLogged(ah1, true));
    }

    @Test
    void prepareTransference_CheckCheck_EverythingCorrect(){
        accountHolderService.prepareTransference(ah1, ac1.getId(), ac2.getId(), new BigDecimal("100"), null);
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("4900.00"), checkingAcc.getBalance().getAmount());
    }

    @Test
    void prepareTransference_CheckCheckNotLoggedIn_Exception(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(false);
        accountHolderRepository.save(accountHolder);
        checkingAccRepository.save(checkingAcc);
        CheckingAcc checkingAcc1 = checkingAccRepository.findById(checkingAcc.getId()).get();
        assertThrows(StatusException.class, ()->accountHolderService.prepareTransference(ah1, checkingAcc1.getId(), ac2.getId(), new BigDecimal("100"), null));
    }

    @Test
    void prepareTransference_EUR_Correct(){
        accountHolderService.prepareTransference(ah1, ac1.getId(), ac2.getId(), new BigDecimal("100"), Currency.getInstance("EUR"));
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("4888.00"), checkingAcc.getBalance().getAmount());
    }

    @Test
    void prepareTransference_NotOwner_Exception(){
        assertThrows(NoOwnerException.class, () -> accountHolderService.prepareTransference(ah3, ac1.getId(), ac2.getId(), new BigDecimal("100"), Currency.getInstance("EUR")));
    }

    @Test
    void prepareTransference_CheckStuCheck_EverythingCorrect(){
        StudentCheckingAcc ac3 = new StudentCheckingAcc(ah1,ah3,new Money(new BigDecimal("10000")), Status.ACTIVE);
        studentCheckingAccRepository.save(ac3);
        accountHolderService.prepareTransference(ah1, ac1.getId(), ac3.getId(), new BigDecimal("100"), null);
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("4900.00"), checkingAcc.getBalance().getAmount());
    }

    @Test
    void prepareTransference_CheckCredCard_EverythingCorrect(){
        CreditCardAcc ac3 = new CreditCardAcc(ah1,ah3,new Money(new BigDecimal("10000")), new BigDecimal("0.2"));
        ac3.setDateInterestRate(new Date(System.currentTimeMillis()));
        creditCardAccRepository.save(ac3);
        accountHolderService.prepareTransference(ah1, ac1.getId(), ac3.getId(), new BigDecimal("100"), null);
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("4900.00"), checkingAcc.getBalance().getAmount());
    }

    @Test
    void prepareTransference_CheckSav_EverythingCorrect(){
        SavingsAcc ac3 = new SavingsAcc(ah1,ah3,new Money(new BigDecimal("1000")),Status.ACTIVE);
        ac3.setDateInterestRate(new Date(System.currentTimeMillis()));
        savingsAccRepository.save(ac3);
        accountHolderService.prepareTransference(ah1, ac1.getId(), ac3.getId(), new BigDecimal("100"), null);
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("4900.00"), checkingAcc.getBalance().getAmount());
    }

    @Test
    void prepareTransference_StuCheckCheck_EverythingCorrect(){
        StudentCheckingAcc ac3 = new StudentCheckingAcc(ah1,ah3,new Money(new BigDecimal("10000")), Status.ACTIVE);
        studentCheckingAccRepository.save(ac3);
        accountHolderService.prepareTransference(ah1, ac3.getId(), ac1.getId(), new BigDecimal("100"), null);
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("5100.00"), checkingAcc.getBalance().getAmount());
    }

    @Test
    void prepareTransference_CredCardCheck_EverythingCorrect(){
        CreditCardAcc ac3 = new CreditCardAcc(ah1,ah3,new Money(new BigDecimal("10000")), new BigDecimal("0.2"));
        ac3.setDateInterestRate(new Date(System.currentTimeMillis()));
        creditCardAccRepository.save(ac3);
        accountHolderService.prepareTransference(ah1, ac3.getId(), ac1.getId(), new BigDecimal("100"), null);
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("5100.00"), checkingAcc.getBalance().getAmount());
    }

    @Test
    void prepareTransference_SavCheck_EverythingCorrect(){
        SavingsAcc ac3 = new SavingsAcc(ah1,ah3,new Money(new BigDecimal("10000")),Status.ACTIVE);
        ac3.setDateInterestRate(new Date(System.currentTimeMillis()));
        savingsAccRepository.save(ac3);
        accountHolderService.prepareTransference(ah1, ac3.getId(), ac1.getId(), new BigDecimal("100"), null);
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("5100.00"), checkingAcc.getBalance().getAmount());
    }

    @Test
    void prepareTransference_CheckFROZENCheck_Exception(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        checkingAcc.setStatus(Status.FROZEN);
        checkingAccRepository.save(checkingAcc);
        CheckingAcc checkingAcc1 = checkingAccRepository.findById(checkingAcc.getId()).get();
        assertThrows(StatusException.class, ()->accountHolderService.prepareTransference(ah1, checkingAcc1.getId(), ac2.getId(), new BigDecimal("100"), null));
    }

    @Test
    void prepareTransference_StuCheckFROZENCheck_Exception(){
        StudentCheckingAcc ac3 = new StudentCheckingAcc(ah1,ah3,new Money(new BigDecimal("10000")), Status.FROZEN);
        studentCheckingAccRepository.save(ac3);
        assertThrows(StatusException.class, ()->accountHolderService.prepareTransference(ah1, ac3.getId(), ac1.getId(), new BigDecimal("100"), null));
    }

    @Test
    void prepareTransference_SavCheckFROZENCheck_Exception(){
        SavingsAcc ac3 = new SavingsAcc(ah1,ah3,new Money(new BigDecimal("10000")),Status.FROZEN);
        savingsAccRepository.save(ac3);
        assertThrows(StatusException.class, ()->accountHolderService.prepareTransference(ah1, ac3.getId(), ac1.getId(), new BigDecimal("100"), null));
    }

    @Test
    void prepareTransference_CheckCheckFROZEN_Exception(){
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac2.getId()).get();
        checkingAcc.setStatus(Status.FROZEN);
        checkingAccRepository.save(checkingAcc);
        CheckingAcc checkingAcc2 = checkingAccRepository.findById(checkingAcc.getId()).get();
        assertThrows(StatusException.class, ()->accountHolderService.prepareTransference(ah1, ac1.getId(), checkingAcc2.getId(), new BigDecimal("100"), null));
    }
    @Test
    void prepareTransference_CheckSavCheckFROZEN_Exception(){
        SavingsAcc ac3 = new SavingsAcc(ah1,ah3,new Money(new BigDecimal("10000")),Status.FROZEN);
        savingsAccRepository.save(ac3);
        assertThrows(StatusException.class, ()->accountHolderService.prepareTransference(ah1, ac1.getId(), ac3.getId(), new BigDecimal("100"), null));
    }

    @Test
    void prepareTransference_CheckStuCheckFROZEN_Exception(){
        StudentCheckingAcc ac3 = new StudentCheckingAcc(ah1,ah3,new Money(new BigDecimal("10000")), Status.FROZEN);
        studentCheckingAccRepository.save(ac3);
        assertThrows(StatusException.class, ()->accountHolderService.prepareTransference(ah1, ac1.getId(), ac3.getId(), new BigDecimal("100"), null));
    }
}