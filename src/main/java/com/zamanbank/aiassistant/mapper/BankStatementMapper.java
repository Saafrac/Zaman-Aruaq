package com.zamanbank.aiassistant.mapper;

import com.zamanbank.aiassistant.dto.bank.BankStatement;
import com.zamanbank.aiassistant.dto.bank.BankTransaction;
import com.zamanbank.aiassistant.model.Transaction;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.service.TransactionCategoryService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankStatementMapper {
  private final TransactionCategoryService categoryService;

  public List<Transaction> mapToTransactions(BankStatement statement, User user) {
    log.info("Начинаем конвертацию {} транзакций из BankStatement", statement.getTransactions().size());
    
    List<Transaction> transactions = statement.getTransactions().stream()
      .map(bankTx -> mapToTransaction(bankTx, user))
      .collect(Collectors.toList());
      
    log.info("Успешно конвертировано {} транзакций", transactions.size());
    return transactions;
  }

  private Transaction mapToTransaction(BankTransaction bankTx, User user) {
    Transaction transaction = Transaction.builder()
      .user(user)
      .amount(bankTx.getAmount())
      .type(bankTx.getType())
      .category(categoryService.categorizeTransaction(bankTx.getDescription()))
      .description(bankTx.getDescription())
      .date(bankTx.getOperationDate())
      .createdAt(LocalDateTime.now())
      .build();
      
    log.debug("Создана транзакция: {} - {} {} ({})", 
      bankTx.getOperationDate(), 
      bankTx.getAmount(), 
      bankTx.getType(), 
      bankTx.getDescription());
      
    return transaction;
  }
}