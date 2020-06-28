package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.dto.AccountMainFields;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.repository.account.*;
import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.repository.user.AdminRepository;
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
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest
class AccountHolderServiceTestUnit {

    @Autowired
    private AccountHolderService accountHolderService;

    @Autowired
    private AdminRepository adminRepository;

    @MockBean(name = "accountHolderRepository")
    private AccountHolderRepository accountHolderRepository;
    @MockBean(name = "thirdPartyRepository")
    private ThirdPartyRepository thirdPartyRepository;
    @MockBean(name = "accountRepository")
    private AccountRepository accountRepository;
    @MockBean(name = "checkingAccRepository")
    private CheckingAccRepository checkingAccRepository;
    @MockBean(name = "studentCheckingAccRepository")
    private StudentCheckingAccRepository studentCheckingAccRepository;
    @MockBean(name = "creditCardAccRepository")
    private CreditCardAccRepository creditCardAccRepository;
    @MockBean(name = "savingsAccRepository")
    private SavingsAccRepository savingsAccRepository;
    @MockBean(name = "transactionRepository")
    private TransactionRepository transactionRepository;


    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2, ah3, ah4;
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
        ah1.setLoggedIn(true);
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
        when(accountHolderRepository.findAll()).thenReturn(Stream.of(ah1, ah2, ah3).collect(Collectors.toList()));
        when(accountHolderRepository.findById(ah1.getId())).thenReturn(Optional.of(ah1));
        when(accountHolderRepository.findById(ah2.getId())).thenReturn(Optional.of(ah2));
        when(accountHolderRepository.findById(ah3.getId())).thenReturn(Optional.of(ah3));
        Object[] ob1 = {1, ac1.getBalance().getAmount(), "USD"};
        Object[] ob2 = {2, ac2.getBalance().getAmount(), "USD"};
        when(accountHolderRepository.findAccountByOwner(ah1.getId())).thenReturn(Stream.of(ob1, ob2).collect(Collectors.toList()));
        when(accountRepository.findById(ac1.getId())).thenReturn(Optional.of(ac1));
        when(accountRepository.findById(ac2.getId())).thenReturn(Optional.of(ac2));
        ah4 = new AccountHolder("Balu", "mowglifriend", "mowglifriend", d2, add1, null);
        doAnswer(i -> null).when(accountHolderRepository).save(ah4);
        doAnswer(i -> null).when(checkingAccRepository).save(ac1);
        doAnswer(i -> null).when(checkingAccRepository).save(ac2);

        when(accountHolderRepository.findById(ah4.getId())).thenReturn(Optional.of(ah4));
        when(thirdPartyRepository.findById(party1.getId())).thenReturn(Optional.of(party1));

        when(checkingAccRepository.findById(ac1.getId())).thenReturn(Optional.of(ac1));
        when(checkingAccRepository.findById(ac2.getId())).thenReturn(Optional.of(ac2));
    }

    @Test
    void findAll() {
        List<AccountHolder> accountHolders = accountHolderService.findAll();
        assertEquals(3, accountHolders.size());
    }

    @Test
    void checkFindById_AdminAccess() {
        assertEquals("Simba", accountHolderService.checkFindById(ah1.getId(), admin1).getName());
    }

    @Test
    void checkFindById_OwnerAccess() {
        assertEquals("Simba", accountHolderService.checkFindById(ah1.getId(), ah1).getName());
    }

    @Test
    void checkFindById_NotOwnerAccess_Exception() {
        assertThrows(NoOwnerException.class, () -> accountHolderService.checkFindById(ah1.getId(), ah2).getName());
    }

    @Test
    void checkFindById_ThirdPartyAccess_Exception() {
        assertThrows(NoOwnerException.class, () -> accountHolderService.checkFindById(ah1.getId(), party1).getName());
    }

    @Test
    void create() {
        assertEquals(null, accountHolderService.store(ah4));
    }

    @Test
    void findAllAccountAsPrimaryOwnerById() {
        List<AccountMainFields> accountMainFieldsList = accountHolderService.findAllAccountAsPrimaryOwnerById(ah1.getId(), ah1);
        assertEquals(2, accountMainFieldsList.size());
    }

    @Test
    void findAllAccountAsPrimaryOwnerById_NotOwner() {
        assertThrows(NoOwnerException.class, () -> accountHolderService.findAllAccountAsPrimaryOwnerById(ah1.getId(), ah2));
    }

    @Test
    void setLogged_Correct() {
        accountHolderService.setLogged(ah1, false);
        AccountHolder accountHolder = accountHolderRepository.findById(ah1.getId()).get();
        assertEquals(false, accountHolder.isLoggedIn());
    }

    @Test
    void setLogged_Already_Exception() {
        assertThrows(StatusException.class, () -> accountHolderService.setLogged(ah1, true));
    }

    @Test
    void prepareTransference_CheckCheck_EverythingCorrect() {
        accountHolderService.prepareTransference(ah1, ac1.getId(), ac2.getId(), new BigDecimal("100"), null);
        CheckingAcc checkingAcc = checkingAccRepository.findById(ac1.getId()).get();
        assertEquals(new BigDecimal("4900.00"), checkingAcc.getBalance().getAmount());
    }

    @Test
    void prepareTransference_CheckCheckNotLoggedIn_Exception() {
        ah1.setLoggedIn(false);
        assertThrows(StatusException.class, () -> accountHolderService.prepareTransference(ah1, ac1.getId(), ac2.getId(), new BigDecimal("100"), null));
    }

    @Test
    void prepareTransference_EUR_Correct() {
        accountHolderService.prepareTransference(ah1, ac1.getId(), ac2.getId(), new BigDecimal("100"), Currency.getInstance("EUR"));
        assertEquals(new BigDecimal("4888.00"), ac1.getBalance().getAmount());
    }

    @Test
    void prepareTransference_NotOwner_Exception() {
        assertThrows(NoOwnerException.class, () -> accountHolderService.prepareTransference(ah3, ac1.getId(), ac2.getId(), new BigDecimal("100"), Currency.getInstance("EUR")));
    }

    @Test
    void prepareTransference_CheckFROZENCheck_Exception() {
        ac1.setStatus(Status.FROZEN);
        assertThrows(StatusException.class, () -> accountHolderService.prepareTransference(ah1, ac1.getId(), ac2.getId(), new BigDecimal("100"), null));
    }

    @Test
    void prepareTransference_CheckCheckFROZEN_Exception() {
        ac2.setStatus(Status.FROZEN);
        assertThrows(StatusException.class, () -> accountHolderService.prepareTransference(ah1, ac1.getId(), ac2.getId(), new BigDecimal("100"), null));
    }

}