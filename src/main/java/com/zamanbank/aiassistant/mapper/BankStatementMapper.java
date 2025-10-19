package com.zamanbank.aiassistant.mapper;

import com.zamanbank.aiassistant.dto.bank.BankStatement;
import com.zamanbank.aiassistant.dto.bank.BankTransaction;
import com.zamanbank.aiassistant.model.Transaction;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.service.TransactionCategoryService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BankStatementMapper {
  private final TransactionCategoryService categoryService;

  public List<Transaction> mapToTransactions(BankStatement statement, User user) {
    return statement.getTransactions().stream()
      .map(bankTx -> mapToTransaction(bankTx, user))
      .collect(Collectors.toList());
  }

  private Transaction mapToTransaction(BankTransaction bankTx, User user) {
    return Transaction.builder()
      .user(user)
      .amount(bankTx.getAmount())
      .type(bankTx.getType())
      .category(categoryService.categorizeTransaction(bankTx.getDescription()))
      .description(bankTx.getDescription())
      .date(bankTx.getOperationDate())
      .build();
  }
}