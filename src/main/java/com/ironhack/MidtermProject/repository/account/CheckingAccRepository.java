package com.ironhack.MidtermProject.repository.account;

import com.ironhack.MidtermProject.model.account.CheckingAcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckingAccRepository extends JpaRepository<CheckingAcc, Integer> {
}
