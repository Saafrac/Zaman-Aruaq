package com.zamanbank.aiassistant.service;

import com.zamanbank.aiassistant.model.enums.TransactionCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionCategoryService {

  public TransactionCategory categorizeTransaction(String description) {
    // Простая логика категоризации (пока без AI)
    if (description.toLowerCase().contains("еда") ||
      description.toLowerCase().contains("магазин") ||
      description.toLowerCase().contains("супермаркет")) {
      return TransactionCategory.FOOD;
    }

    if (description.toLowerCase().contains("транспорт") ||
      description.toLowerCase().contains("автобус") ||
      description.toLowerCase().contains("такси")) {
      return TransactionCategory.TRANSPORT;
    }

    if (description.toLowerCase().contains("аптека") ||
      description.toLowerCase().contains("здоровье") ||
      description.toLowerCase().contains("медицина")) {
      return TransactionCategory.HEALTHCARE;
    }

    return TransactionCategory.OTHER_EXPENSE;
  }
}