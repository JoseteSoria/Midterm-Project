package com.ironhack.MidtermProject.repository.account;

import com.ironhack.MidtermProject.model.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
}
