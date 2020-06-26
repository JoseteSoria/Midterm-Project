package com.ironhack.MidtermProject.repository.user;

import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Integer> {
    @Query(value = "select a.id, a.balance_amount, a.balance_currency from account a where primary_owner_id =:id", nativeQuery = true)
    List<Object[]> findAccountByOwner(@Param("id") Integer id);

    @Query(value = "select * from user u join account_holder a on u.id = a.id where u.username= :username limit 1;", nativeQuery = true)
    Optional<AccountHolder> findByUsername(@Param("username") String username);

    @Query(value = "select from account a where id =:id", nativeQuery = true)
    Account findAccountsById(@Param("id") Integer id);

    @Query(value = "select primary_owner_id, secondary_owner_id from account where id = :accountId ; ", nativeQuery = true)
    Integer[] findOwnersByAccount(@Param(("accountId")) Integer accountId);
}
