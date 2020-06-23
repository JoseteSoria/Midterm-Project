package com.ironhack.MidtermProject.repository.account;

import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentCheckingAccRepository extends JpaRepository<StudentCheckingAcc, Integer> {
}
