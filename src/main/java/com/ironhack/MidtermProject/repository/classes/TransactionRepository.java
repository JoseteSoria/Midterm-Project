package com.ironhack.MidtermProject.repository.classes;

import com.ironhack.MidtermProject.model.classes.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
}
