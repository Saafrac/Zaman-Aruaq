package com.zamanbank.aiassistant.service.impl;

import com.zamanbank.aiassistant.dto.bank.BankStatement;
import com.zamanbank.aiassistant.dto.bank.BankTransaction;
import com.zamanbank.aiassistant.model.Transaction;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.TransactionCategory;
import com.zamanbank.aiassistant.model.enums.TransactionType;
import com.zamanbank.aiassistant.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionSaveService {

  private final TransactionRepository transactionRepository;

  public void saveParsedStatement(BankStatement statement, User user) {
    if (statement.getTransactions() == null || statement.getTransactions().isEmpty()) {
      log.warn("Нет транзакций для сохранения");
      return;
    }

    List<Transaction> transactions = statement.getTransactions().stream()
      .map(tx -> convertToEntity(tx, user))
      .collect(Collectors.toList());

    transactionRepository.saveAll(transactions);
    log.info("Сохранено {} транзакций для пользователя {}", transactions.size(), user.getId());
  }

  private Transaction convertToEntity(BankTransaction tx, User user) {
    return Transaction.builder()
      .user(user)
      .amount(tx.getAmount())
      .type(tx.getType() != null ? tx.getType() : TransactionType.EXPENSE)
      .category(tx.getCategory() != null ? tx.getCategory() : TransactionCategory.OTHER_EXPENSE)
      .description(tx.getDescription())
      .date(tx.getOperationDate())
      .createdAt(LocalDateTime.now())
      .build();
  }
}
