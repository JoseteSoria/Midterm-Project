package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.dto.CheckingAccCreation;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
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
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest
class CheckingAccServiceTestUnit {
    @Autowired
    private CheckingAccService checkingAccService;

    @MockBean(name = "checkingAccRepository")
    private CheckingAccRepository checkingAccRepository;
    @MockBean(name = "accountHolderRepository")
    private AccountHolderRepository accountHolderRepository;
    @MockBean(name = "studentCheckingAccRepository")
    private StudentCheckingAccRepository studentCheckingAccRepository;
    @MockBean(name = "accountRepository")
    private AccountRepository accountRepository;
    @MockBean(name = "transactionRepository")
    private TransactionRepository transactionRepository;
    @MockBean(name = "thirdPartyRepository")
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2, ah3;
    Admin admin1;
    ThirdParty party1;
    CheckingAcc ac1, ac2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        add1 = new Address("Spain", "Madrid", "Canal");
        d1 = Date.valueOf("1980-10-12");
        d2 = Date.valueOf("1982-05-18");
        ah1 = new AccountHolder("Simba", "kinglyon", "kinglyon", d1, add1, null);
        ah1.setId(1);
        ah2 = new AccountHolder("Hercules", "strongman", "strongman", d2, add1, null);
        ah2.setId(2);
        ah3 = new AccountHolder("Pinocho", "woodman", "woodman", d2, add1, null);
        ah3.setId(3);
        admin1 = new Admin("Dreamworks", "dreamworks", "dreamworks");
        party1 = new ThirdParty("Third", "third", "third", "third-hashkey");
        party1.setId(5);
        ac1 = new CheckingAcc(ah1, ah2, new Money(new BigDecimal("5000")), Status.ACTIVE);
        ac1.setId(1);
        ac2 = new CheckingAcc(ah1, ah3, new Money(new BigDecimal("1000")), Status.ACTIVE);
        ac2.setId(2);
        when(checkingAccRepository.findAll()).thenReturn(Stream.of(ac1, ac2).collect(Collectors.toList()));
        when(checkingAccRepository.findById(ac1.getId())).thenReturn(Optional.of(ac1));
        when(checkingAccRepository.findById(ac2.getId())).thenReturn(Optional.of(ac2));
        when(accountRepository.findById(ac1.getId())).thenReturn(Optional.of(ac1));
        when(accountRepository.findById(ac2.getId())).thenReturn(Optional.of(ac2));
        doAnswer(i -> null).when(checkingAccRepository).save(ac1);
        doAnswer(i -> null).when(checkingAccRepository).save(ac2);
        CheckingAccCreation ac3 = new CheckingAccCreation(ah1, ah3, new Money(new BigDecimal("15000")));
        AccountHolder ah4 = new AccountHolder("Bambi", "littledeer", "littledeer", new Date(System.currentTimeMillis() - 31556926l * 16), add1, null);
        ah4.setId(4);
        when(accountHolderRepository.findById(ah1.getId())).thenReturn(Optional.of(ah1));
        when(accountHolderRepository.findById(ah2.getId())).thenReturn(Optional.of(ah2));
        when(accountHolderRepository.findById(ah3.getId())).thenReturn(Optional.of(ah3));
        doAnswer(i -> null).when(accountHolderRepository).save(ah4);
        when(accountHolderRepository.findById(ah4.getId())).thenReturn(Optional.of(ah4));
        when(thirdPartyRepository.findById(party1.getId())).thenReturn(Optional.of(party1));
    }

    @Test
    void findAll() {
        List<CheckingAcc> checkingAccServices = checkingAccService.findAll();
        assertEquals(2, checkingAccServices.size());
    }

    @Test
    void checkFindById_AdminAccess() {
        assertEquals("Simba", checkingAccService.checkFindById(ac2.getId(), admin1).getPrimaryOwner().getName());
    }

    @Test
    void checkFindById_OwnerLoggedIn() {
        ah1.setLoggedIn(true);
        assertEquals("Simba", checkingAccService.checkFindById(ac1.getId(), ah1).getPrimaryOwner().getName());
    }

    @Test
    void checkFindById_OwnerNotLoggedIn_Exception() {
        assertThrows(StatusException.class, () -> checkingAccService.checkFindById(ah1.getId(), ah1));
    }

    @Test
    void checkFindById_OwnerNotCorrect_Exception() {
        assertThrows(NoOwnerException.class, () -> checkingAccService.checkFindById(ac1.getId(), ah3));
    }

    @Test
    void create_OlderThan24() {
        CheckingAccCreation ac3 = new CheckingAccCreation(ah1, ah3, new Money(new BigDecimal("15000")));
        CheckingAccCreation creation = checkingAccService.create(ac3);
        assertEquals("Simba", creation.getPrimaryOwner().getName());
    }

    @Test
    void create_YoungerThan24() {
        Date d3 = new Date(System.currentTimeMillis() - 31556926l * 16);
        AccountHolder ah4 = new AccountHolder("Bambi", "littledeer", "littledeer", d3, add1, null);
        ah4.setId(4);
        CheckingAccCreation ac3 = new CheckingAccCreation(ah4, ah3, new Money(new BigDecimal("15000")));
        CheckingAccCreation creation = checkingAccService.create(ac3);
        assertEquals("Bambi", creation.getPrimaryOwner().getName());
    }

    @Test
    void reduceBalance() {
        ah1.setLoggedIn(true);
        checkingAccService.reduceBalance(ah1, ac1.getId(), new BigDecimal("1000"), null, "abrakadabra", null);
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("4000.00"), checkingAcc.getBalance().getAmount());
    }

    @Test
    void reduceBalance_AccountNotFound() {
        ah1.setLoggedIn(true);
        assertThrows(IdNotFoundException.class, () -> checkingAccService.reduceBalance(ah1, ac2.getId() + 100, new BigDecimal("1000"), null, "abrakadabra", null));
    }

    @Test
    void addBalance() {
        ah1.setLoggedIn(true);
        checkingAccService.addBalance(ah1, ac1.getId(), new BigDecimal("1000"), null, "abrakadabra", null);
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("6000.00"), checkingAcc.getBalance().getAmount());
    }

    @Test
    void addBalance_AccountNotFound() {
        ah1.setLoggedIn(true);
        assertThrows(IdNotFoundException.class, () -> checkingAccService.addBalance(ah1, ac2.getId() + 100, new BigDecimal("1000"), null, "abrakadabra", null));
    }

    @Test
    void checkAllowance_CorrectOwner() {
        ah1.setLoggedIn(true);
        checkingAccService.checkAllowance(ah1, ac1.getId(), "abrakadabra", "ashd");
    }

    @Test
    void checkAllowance_Admin() {
        checkingAccService.checkAllowance(admin1, ac1.getId(), "aabra", "ashd");
    }

    @Test
    void checkAllowance_FrozenAccount() {
        ah1.setLoggedIn(true);
        ac1.setStatus(Status.FROZEN);
        assertThrows(StatusException.class, () -> checkingAccService.checkAllowance(ah1, ac1.getId(), "abrakadabra", "ashd"));
    }

    @Test
    void checkAllowance_HashKeyWrong_Exception() {
        ac1.setSecretKey("abrakadabra");
        assertThrows(NoOwnerException.class, () -> checkingAccService.checkAllowance(party1, ac1.getId(), "askd", "third-hashkey"));
    }

    @Test
    void changeStatus_Correct() {
        checkingAccService.changeStatus(ac1.getId(), "FROZEN");
        assertEquals(Status.FROZEN, ac1.getStatus());
    }

    @Test
    void changeStatus_Already() {
        assertThrows(StatusException.class, () -> checkingAccService.changeStatus(ac1.getId(), "ACTIVE"));
    }

    @Test
    void changeStatus_NoStatus() {
        assertThrows(StatusException.class, () -> checkingAccService.changeStatus(ac1.getId(), "SDFIS"));
    }

    @Test
    void changeStatus_AccountNotFound() {
        assertThrows(IdNotFoundException.class, () -> checkingAccService.changeStatus(ac1.getId() + 100, "FROZEN"));
    }

}