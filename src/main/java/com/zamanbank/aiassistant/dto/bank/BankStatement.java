package com.zamanbank.aiassistant.dto.bank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankStatement {
  private String clientName;
  private String iin;
  private String accountType;
  private String kztAccount;
  private String usdAccount;
  private String eurAccount;
  private LocalDate statementDate;
  private LocalDate periodFrom;
  private LocalDate periodTo;
  private BigDecimal incomingBalanceKzt;
  private BigDecimal outgoingBalanceKzt;
  private BigDecimal availableAmountKzt;
  private List<BankTransaction> transactions;
}