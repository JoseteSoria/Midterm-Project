package com.ironhack.MidtermProject.service.classes;

import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> findAll(){ return transactionRepository.findAll(); }

    public void create(Transaction transaction){ transactionRepository.save(transaction); }


    public boolean checkTransaction(Transaction transaction) {
        //Admin movements are not taking into account
        List<Transaction> transactions = transactionRepository.findByRoleIdNotLike("ADMIN");
        Date lastTransaction = transactionRepository.findLastTransactionDate(transaction.getOrderingId());
        // if 2 transactions is done without 1 second of difference
        if(lastTransaction!=null && transaction.getDate().before(new Date(lastTransaction.getTime()-1000l))){
            return false;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Everything in database is in UTC
        String transDate = simpleDateFormat.format(transaction.getDate());
        String trans24hBefore = simpleDateFormat.format((new Date(transaction.getDate().getTime() - 86400000l)));
        BigDecimal last24Amount = transactionRepository.findMyTotalLast24hTransactions(transDate,
                trans24hBefore, transaction.getOrderingId()).orElse(new BigDecimal("0"));
        BigDecimal totalWithThisTrans = last24Amount.add(transaction.getQuantity().getAmount());

        // Up to 300 every quantity is allowed. (For initialization)
        BigDecimal maximum = new BigDecimal("300");

        if (transactions!=null){
            for (Transaction trans : transactions) {
                String date1 = simpleDateFormat.format(trans.getDate());
                String date2 = simpleDateFormat.format(new Date(trans.getDate().getTime() - 86400000l));
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
