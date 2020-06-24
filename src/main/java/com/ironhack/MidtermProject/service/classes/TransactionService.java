package com.ironhack.MidtermProject.service.classes;

import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> findAll(){ return transactionRepository.findAll(); }

    public void create(Transaction transaction){ transactionRepository.save(transaction); }


    public boolean checkTransaction(Transaction transaction) {
        // All credit are allowed. Not doing this, rich person could freeze whoever they want sending them a lot of money
        List<Transaction> transactions = transactionRepository.findByTransactionTypeNotLike("DEBIT");
        Date lastTransaction = transactionRepository.findLastTransactionDate(transaction.getOrderingAccountId());
        // if 2 transactions is done without 1 second of difference
        if(lastTransaction!=null && transaction.getDate().before(new Date(lastTransaction.getTime()-1000l))){
            return false;
        }
        Date transDate = transaction.getDate();
        Date trans24hBefore = new Date(transaction.getDate().getTime() - 86400000l);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String transDateString = simpleDateFormat.format(transaction.getDate());
        String trans24hBeforeString = simpleDateFormat.format((new Date(transaction.getDate().getTime() - 86400000l)));
        BigDecimal last24Amount = transactionRepository.findMyTotalLast24hTransactions(transDate,
                trans24hBefore,transaction.getOrderingAccountId()).orElse(new BigDecimal("0"));
        BigDecimal totalWithThisTrans = last24Amount.add(transaction.getQuantity().getAmount());

        // Up to 300 every quantity is allowed. (For initialization)
        BigDecimal maximum = new BigDecimal("300");

        if (transactions!=null){
            for (Transaction trans : transactions) {
                String date1 = trans.getDate().toString();
                String date2 = (new Date(trans.getDate().getTime() - 86400000l)).toString();
                BigDecimal max = transactionRepository.findMaxIn24HPeriodsForAnyOne(date1, date2);
                if (max.compareTo(maximum) > 0) {
                    maximum = max;
                }
            }
        }
        // bal + bal*1.5 = bal*2.5
        if (totalWithThisTrans.compareTo(maximum.multiply(new BigDecimal("2.5"))) > 0)
        {
            return false;
        }
        else {
            return true;
        }
    }
}
