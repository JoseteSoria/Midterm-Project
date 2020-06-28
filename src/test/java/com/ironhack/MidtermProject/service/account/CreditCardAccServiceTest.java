package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.repository.account.AccountRepository;
import com.ironhack.MidtermProject.repository.account.CreditCardAccRepository;
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
class CreditCardAccServiceTest {

    @Autowired
    private CreditCardAccService creditCardAccService;
    @Autowired
    private CreditCardAccRepository creditCardAccRepository;
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
    CreditCardAcc ac1, ac2;

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
        ac1 = new CreditCardAcc(ah1, ah2, new Money(new BigDecimal("5000")), new BigDecimal("0.2"));
        ac2 = new CreditCardAcc(ah1, ah3, new Money(new BigDecimal("1000")), new BigDecimal("0.2"));
        creditCardAccRepository.saveAll(Stream.of(ac1, ac2).collect(Collectors.toList()));
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        creditCardAccRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    void findAll() {
        List<CreditCardAcc> creditCardAccs = creditCardAccService.findAll();
        assertEquals(2, creditCardAccs.size());
    }

    @Test
    void checkFindById_AdminAcces() {
        CreditCardAcc ac3 = new CreditCardAcc(ah1, ah3, new Money(new BigDecimal("10000")), new BigDecimal("0.2"));
        ac3.setDateInterestRate(new Date(System.currentTimeMillis()));
        creditCardAccRepository.save(ac3);
        assertEquals("Simba", creditCardAccService.checkFindById(ac3.getId(), admin1).getPrimaryOwner().getName());
    }

    @Test
    void checkFindById_OwnerLoggedIn() {
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        accountHolderRepository.save(accountHolder);
        CreditCardAcc ac3 = new CreditCardAcc(accountHolder, ah3, new Money(new BigDecimal("10000")), new BigDecimal("0.2"));
        ac3.setDateInterestRate(new Date(System.currentTimeMillis()));
        creditCardAccRepository.save(ac3);
        assertEquals("Simba", creditCardAccService.checkFindById(ac3.getId(), ah1).getPrimaryOwner().getName());
    }

    @Test
    void checkFindById_OwnerNotLoggedIn_Exception() {
        CreditCardAcc ac3 = new CreditCardAcc(ah1, ah3, new Money(new BigDecimal("10000")), new BigDecimal("0.2"));
        ac3.setDateInterestRate(new Date(System.currentTimeMillis()));
        creditCardAccRepository.save(ac3);
        assertThrows(StatusException.class, () -> creditCardAccService.checkFindById(ac3.getId(), ah1));
    }

    @Test
    void checkFindById_OwnerNotCorrect_Exception() {
        CreditCardAcc ac3 = new CreditCardAcc(ah1, ah3, new Money(new BigDecimal("10000")), new BigDecimal("0.2"));
        ac3.setDateInterestRate(new Date(System.currentTimeMillis()));
        creditCardAccRepository.save(ac3);
        assertThrows(NoOwnerException.class, () -> creditCardAccService.checkFindById(ac3.getId(), ah2));
    }

    @Test
    void create_Correct() {
        CreditCardAcc ac3 = new CreditCardAcc(ah1, ah3, new Money(new BigDecimal("10000")), new BigDecimal("0.2"));
        CreditCardAcc creation = creditCardAccService.create(ac3);
        assertEquals(3, creditCardAccService.findAll().size());
        assertEquals("Simba", creditCardAccRepository.findById(ac2.getId() + 1).get().getPrimaryOwner().getName());
    }

    @Test
    void creditBalance() {
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        creditCardAcc.setDateInterestRate(new Date(System.currentTimeMillis() - 86400000));
        accountHolderRepository.save(accountHolder);
        creditCardAccRepository.save(creditCardAcc);
        creditCardAcc = creditCardAccRepository.findById(ac1.getId()).get();
        creditCardAccService.creditBalance(ah1, creditCardAcc.getId(), new BigDecimal("1000"), null, "abrakadabra", null);
        creditCardAcc = creditCardAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("-1000.00"), creditCardAcc.getBalance().getAmount());
    }

    @Test
    void creditalance_AccountNotFound() {
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        creditCardAcc.setDateInterestRate(new Date(System.currentTimeMillis() - 86400000));
        accountHolderRepository.save(accountHolder);
        creditCardAccRepository.save(creditCardAcc);
        CreditCardAcc creditCardAcc1 = creditCardAccRepository.findById(ac1.getId()).get();
        assertThrows(IdNotFoundException.class, () -> creditCardAccService.creditBalance(ah1, creditCardAcc1.getId() + 100, new BigDecimal("1000"), null, "abrakadabra", null));
    }

    @Test
    void debitBalance() {
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        creditCardAcc.setDateInterestRate(new Date(System.currentTimeMillis() - 86400000));
        creditCardAcc.setCreditLimit(new Money(new BigDecimal("100000")));
        accountHolderRepository.save(accountHolder);
        creditCardAccRepository.save(creditCardAcc);
        creditCardAcc = creditCardAccRepository.findById(ac1.getId()).get();
        creditCardAccService.debitBalance(ah1, creditCardAcc.getId(), new BigDecimal("1000"), null, "abrakadabra", null);
        creditCardAcc = creditCardAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("1000.00"), creditCardAcc.getBalance().getAmount());
    }

    @Test
    void debitBalance_AccountNotFound() {
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        creditCardAcc.setDateInterestRate(new Date(System.currentTimeMillis() - 86400000));
        accountHolderRepository.save(accountHolder);
        creditCardAccRepository.save(creditCardAcc);
        CreditCardAcc creditCardAcc1 = creditCardAccRepository.findById(ac1.getId()).get();
        assertThrows(IdNotFoundException.class, () -> creditCardAccService.debitBalance(ah1, creditCardAcc1.getId() + 100, new BigDecimal("1000"), null, "abrakadabra", null));
    }

    @Test
    void checkAllowance_Admin() {
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(ac1.getId()).get();
        creditCardAcc.setDateInterestRate(new Date(System.currentTimeMillis() - 86400000));
        creditCardAccRepository.save(creditCardAcc);
        creditCardAccService.checkAllowance(admin1, ac1.getId(), "aabra", "ashd");
    }

    @Test
    void checkAllowance_CorrectOwner() {
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(ac1.getId()).get();
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        accountHolder.setLoggedIn(true);
        creditCardAcc.setDateInterestRate(new Date(System.currentTimeMillis() - 86400000));
        accountHolderRepository.save(accountHolder);
        creditCardAccRepository.save(creditCardAcc);
        creditCardAcc = creditCardAccRepository.findById(ac1.getId()).get();
        creditCardAccService.checkAllowance(creditCardAcc.getPrimaryOwner(), creditCardAcc.getId(), "aabra", "ashd");
    }

    @Test
    void checkAllowance_ThirdParty_Exception() {
        assertThrows(NoOwnerException.class, () -> creditCardAccService.checkAllowance(party1, ac1.getId(), "askd", "third-hashkey"));
    }
}