package com.zamanbank.aiassistant.repository;

import com.zamanbank.aiassistant.model.Transaction;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.TransactionType;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    List<Transaction> findByUserAndType(User user, TransactionType type);
    List<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
}
