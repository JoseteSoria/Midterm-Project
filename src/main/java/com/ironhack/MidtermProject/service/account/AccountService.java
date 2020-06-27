package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.account.AccountRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
import com.ironhack.MidtermProject.util.PasswordUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    public List<Account> findAllAccount(){ return accountRepository.findAll(); }

    public Account findById(Integer id) {
        return accountRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Account not found with that id"));
    }

    public boolean checkLoggedIn(User user, Account account){
        if((account.getPrimaryOwner()!=null && (account.getPrimaryOwner().getId().equals(user.getId())) && account.getPrimaryOwner().isLoggedIn()) ||
                (account.getSecondaryOwner()!=null && (account.getSecondaryOwner().getId().equals(user.getId())) && account.getSecondaryOwner().isLoggedIn()))
        {
            return true;
        }else
            return false;
    }

    public void checkAllowance(User user, Integer id, String secretKey, String header) {
        Account account = accountRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Account not found with that id"));;
        switch(user.getRole()){
            case ADMIN:
                break;
            case ACCOUNT_HOLDER:
                if((account.getPrimaryOwner()!=null && account.getPrimaryOwner().getId().equals(user.getId())) || (account.getSecondaryOwner()!=null && account.getSecondaryOwner().getId().equals(user.getId()))){
                    if(checkLoggedIn(user, account)) {
                        break;
                    }
                    else {
                        throw new StatusException("You are not logged in");
                    }
                }
                else throw new NoOwnerException("You are not the owner of this account");
            case THIRD_PARTY:
                ThirdParty thirdParty = thirdPartyRepository.findById(user.getId())
                        .orElseThrow(()-> new IdNotFoundException("No third party found"));
                if(header == null || secretKey == null)
                    throw new NoOwnerException("You are a third party. You have to provide more info.");
                else if(!PasswordUtility.passwordEncoder.matches(header, thirdParty.getHashKey()))
                    throw new NoOwnerException("Your hash-key is wrong");
                else
                    break;
        }
    }

    public AccountHolder[] checkOwner(Account account){
        AccountHolder[] accountHolders = new AccountHolder[2];
        AccountHolder primOwner;
        if(account.getPrimaryOwner()!=null) {
            if (account.getPrimaryOwner().getId() != null) {
                primOwner = accountHolderRepository.findById(account.getPrimaryOwner().getId())
                        .orElseThrow(() -> new IdNotFoundException("Not primary Owner found with that id"));
            } else if (account.getPrimaryOwner().getUsername() != null) {
                primOwner = accountHolderRepository.findByUsername(account.getPrimaryOwner().getUsername()).orElse(
                        new AccountHolder(account.getPrimaryOwner().getName(), account.getPrimaryOwner().getUsername(),
                                account.getPrimaryOwner().getPassword(), account.getPrimaryOwner().getDateOfBirthday(),
                                account.getPrimaryOwner().getPrimaryAddress(), account.getPrimaryOwner().getMailingAddress()));
            }
            else{
                // Theoretically not reachable thankfully the constructor of tha accountHolder
                throw new NoOwnerException("You have to provide at least id or username for primary owner");
            }
        }
        else{
            // Theoretically not reachable thankfully the constructor of tha account
            throw new NoOwnerException("You have to provide a primary owner");
        }
        accountHolders[0] = primOwner;
        if(account.getSecondaryOwner()!=null) {
            AccountHolder secOwner;
            if (account.getSecondaryOwner().getId() != null) {
                secOwner = accountHolderRepository.findById(account.getSecondaryOwner().getId())
                        .orElseThrow(() -> new IdNotFoundException("Not secondary Owner found with that id"));
            } else if (account.getSecondaryOwner().getUsername() != null) {
                secOwner = accountHolderRepository.findByUsername(account.getSecondaryOwner().getUsername()).orElse(
                        new AccountHolder(account.getSecondaryOwner().getName(), account.getSecondaryOwner().getUsername(),
                                account.getSecondaryOwner().getPassword(), account.getSecondaryOwner().getDateOfBirthday(),
                                account.getSecondaryOwner().getPrimaryAddress(), account.getSecondaryOwner().getMailingAddress()));
            }
            else {
                // Theoretically not reachable thankfully the constructor of tha accountHolder
                throw new NoOwnerException("You have to provide at least id or username for secondary owner");
            }
            accountHolders[1] = secOwner;
        }
        return accountHolders;
    }
}
