package com.zamanbank.aiassistant.service;

import com.zamanbank.aiassistant.model.Transaction;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.TransactionType;
import com.zamanbank.aiassistant.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    
    @Transactional
    public void saveAll(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            log.warn("Попытка сохранить пустой список транзакций");
            return;
        }
        
        transactionRepository.saveAll(transactions);
        log.info("Сохранено {} транзакций", transactions.size());
    }
    
    @Transactional(readOnly = true)
    public List<Transaction> findByUser(User user) {
        return transactionRepository.findByUser(user);
    }
    
    @Transactional(readOnly = true)
    public List<Transaction> findByUserAndType(User user, TransactionType type) {
        return transactionRepository.findByUserAndType(user, type);
    }
    
    @Transactional(readOnly = true)
    public List<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end) {
        return transactionRepository.findByUserAndDateBetween(user, start, end);
    }
    
    @Transactional
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
    
    @Transactional
    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
        log.info("Удалена транзакция с ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public long countByUser(User user) {
        return transactionRepository.findByUser(user).size();
    }
}