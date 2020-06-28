package com.ironhack.MidtermProject.repository.account;

import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardAccRepository extends JpaRepository<CreditCardAcc, Integer> {
}
