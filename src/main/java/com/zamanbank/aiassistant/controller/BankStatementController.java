package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.dto.bank.BankStatement;
import com.zamanbank.aiassistant.mapper.BankStatementMapper;
import com.zamanbank.aiassistant.model.Transaction;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.model.enums.TransactionType;
import com.zamanbank.aiassistant.service.TransactionService;
import com.zamanbank.aiassistant.service.UserService;
import com.zamanbank.aiassistant.service.parser.BankStatementParserService;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/bank-statements")
@RequiredArgsConstructor
@Slf4j
public class BankStatementController {

  private final BankStatementParserService parserService;
  private final BankStatementMapper mapper;
  private final TransactionService transactionService;
  private final UserService userService;

  @PostMapping("/upload")
  @Operation(summary = "Загрузить банковскую выписку")
  public ResponseEntity<Map<String, Object>> uploadStatement(
    @RequestParam("file") MultipartFile file,
    Authentication authentication) {

    try {
      // Проверяем формат файла
      if (!parserService.isSupportedFormat(file.getOriginalFilename())) {
        return ResponseEntity.badRequest()
          .body(Map.of("error", "Неподдерживаемый формат файла"));
      }

      // Сохраняем файл
      String filePath = saveUploadedFile(file);

      // Парсим выписку
      BankStatement statement = parserService.parseStatement(filePath);

      // Получаем пользователя
      User user = userService.getCurrentUser(authentication);

      // Конвертируем в транзакции
      List<Transaction> transactions = mapper.mapToTransactions(statement, user);

      // Сохраняем транзакции
      transactionService.saveAll(transactions);

      // Удаляем временный файл
      Files.deleteIfExists(Paths.get(filePath));

      Map<String, Object> response = new HashMap<>();
      response.put("message", "Выписка успешно обработана");
      response.put("transactionsCount", transactions.size());
      response.put("statementPeriod", statement.getPeriodFrom() + " - " + statement.getPeriodTo());
      response.put("totalIncome", transactions.stream()
        .filter(t -> t.getType() == TransactionType.INCOME)
        .mapToDouble(t -> t.getAmount().doubleValue())
        .sum());
      response.put("totalExpenses", transactions.stream()
        .filter(t -> t.getType() == TransactionType.EXPENSE)
        .mapToDouble(t -> t.getAmount().doubleValue())
        .sum());

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Ошибка при обработке выписки", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "Ошибка при обработке файла: " + e.getMessage()));
    }
  }

  private String saveUploadedFile(MultipartFile file) throws IOException {
    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
    Path filePath = Paths.get("temp", fileName);
    Files.createDirectories(filePath.getParent());
    Files.write(filePath, file.getBytes());
    return filePath.toString();
  }
}