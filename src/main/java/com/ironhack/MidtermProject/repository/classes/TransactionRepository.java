package com.ironhack.MidtermProject.repository.classes;

import com.ironhack.MidtermProject.model.classes.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    @Query(value = "select * from transaction t join user u on t.ordering_id = u.id where u.role != :role and t.ordering_id != :orderId ;", nativeQuery = true)
    public List<Transaction> findByRoleIdNotLikeAndNotOrderId(@Param("role") String role, @Param("orderId") Integer orderId);

    @Query(value = "select date from transaction where ordering_id =:orderId order by date desc limit 1;", nativeQuery = true)
    public Date findLastTransactionDate(@Param("orderId") Integer orderId);

    @Query(value = "select max(tot) from (select count(t.amount) as tot from transaction t join user u on t.ordering_id = u.id where" +
            " u.role !='ADMIN' and t.date <= :date1 and t.date >= :date2 and t.ordering_id != :orderId group by t.ordering_id) as a ;", nativeQuery = true)
    public BigDecimal findMaxIn24HPeriodsForAnyOne(@Param("date1") String date1, @Param("date2") String date2, @Param("orderId") Integer orderId);

    @Query(value = "select count(t.amount) from transaction t join user u on t.ordering_id = " +
            "u.id where t.date < :date1 and t.date > :date2 and u.role!='ADMIN' and t.ordering_id = :orderId ;", nativeQuery = true)
    public Optional<BigDecimal> findMyTotalLast24hTransactions(@Param("date1") String date1, @Param("date2") String date2, @Param("orderId") Integer orderId);

//    @Query(value = "select max(amount) from transaction where ordering_account_id =:orderId and where date < :date1 and date > :date2;", nativeQuery = true)
//    public BigDecimal findMaxIn24HPeriod(@Param("date1") String date1, @Param("date2") String date2, @Param("orderId") Integer orderId);
//@Query(value = "select * from transaction t where t.transaction_type != :transType ;", nativeQuery = true)
//public List<Transaction> findByTransactionTypeNotLike(@Param("transType") String transactionType);
}
