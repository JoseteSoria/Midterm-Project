package com.ironhack.MidtermProject.service.classes;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.repository.account.AccountRepository;
import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.repository.user.AdminRepository;
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
import com.ironhack.MidtermProject.service.account.AccountService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private AdminRepository adminRepository;

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2, ah3;
    Admin admin1;
    ThirdParty party1;
    StudentCheckingAcc ac1, ac2;
    Transaction t1, t2;

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
        ac1 = new StudentCheckingAcc(ah1, ah2, new Money(new BigDecimal("1000")), Status.ACTIVE);
        ac2 = new StudentCheckingAcc(ah3, null, new Money(new BigDecimal("3000")), Status.ACTIVE);
        accountRepository.saveAll(Stream.of(ac1, ac2).collect(Collectors.toList()));
        t2 = new Transaction();
        t1 = new Transaction(ah1.getId(), ac2, ac1, new Money(new BigDecimal("100")), TransactionType.TRANSFERENCE);
        transactionService.create(t1);
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    void findAll() {
        List<Transaction> transactions = transactionService.findAll();
        assertEquals(1, transactions.size());
    }

    @Test
    void checkTransaction_FirstOne() {
        Transaction t3 = new Transaction(ah2.getId(), ac2, ac1, new Money(new BigDecimal("100")), TransactionType.TRANSFERENCE);
        t3.setDate(new Date(System.currentTimeMillis() + 86400000l));
        assertTrue(transactionService.checkTransaction(t3));
    }

    @Test
    void checkTransaction_LessThanOneSecond() {
        Transaction t3 = new Transaction(ah1.getId(), ac2, ac1, new Money(new BigDecimal("100")), TransactionType.TRANSFERENCE);
        t3.setDate(new Date(t1.getDate().getTime() + 500l));
        assertTrue(!transactionService.checkTransaction(t3));
    }

    @Test
    void checkTransaction_ToMuchTransactions() {
        Transaction t3 = new Transaction(ah2.getId(), ac2, ac1, new Money(new BigDecimal("100")), TransactionType.TRANSFERENCE);
        t3.setDate(new Date(t1.getDate().getTime() + 50000l));
        Transaction t4 = new Transaction(ah2.getId(), ac2, ac1, new Money(new BigDecimal("100")), TransactionType.TRANSFERENCE);
        t4.setDate(new Date(t1.getDate().getTime() + 100000l));
        Transaction t5 = new Transaction(ah2.getId(), ac2, ac1, new Money(new BigDecimal("100")), TransactionType.TRANSFERENCE);
        t5.setDate(new Date(t1.getDate().getTime() + 150000l));
        Transaction t6 = new Transaction(ah2.getId(), ac2, ac1, new Money(new BigDecimal("100")), TransactionType.TRANSFERENCE);
        t6.setDate(new Date(t1.getDate().getTime() + 200000l));
        transactionRepository.saveAll(Stream.of(t3, t4, t5).collect(Collectors.toList()));
        Transaction t7 = new Transaction(ah2.getId(), ac2, ac1, new Money(new BigDecimal("100")), TransactionType.TRANSFERENCE);
        t7.setDate(new Date(t1.getDate().getTime() + 250000l));
        assertTrue(!transactionService.checkTransaction(t7));
    }

}