package com.ironhack.MidtermProject.repository.user;

import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Integer> {
    @Query(value = "select a.id, a.balance_amount, a.balance_currency from account a where primary_owner_id =:id", nativeQuery = true)
    List<Object[]> findAccountByOwner(@Param("id") Integer id);
}
