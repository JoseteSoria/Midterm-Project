package com.ironhack.MidtermProject.repository.account;

import com.ironhack.MidtermProject.model.account.SavingsAcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsAccRepository extends JpaRepository<SavingsAcc, Integer> {
}
