package com.zamanbank.aiassistant.dto.bank;

import com.zamanbank.aiassistant.model.enums.TransactionCategory;
import com.zamanbank.aiassistant.model.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankTransaction {
  private LocalDate operationDate;
  private LocalDate processingDate;
  private String description;
  private BigDecimal amount;
  private String currency;
  private BigDecimal creditAmount;
  private BigDecimal debitAmount;
  private BigDecimal commission;
  private String cardAccountNumber;
  private TransactionType type; // INCOME, EXPENSE
  private TransactionCategory category; // FOOD, TRANSPORT, HEALTH, etc.
}