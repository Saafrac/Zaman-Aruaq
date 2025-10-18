package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.dto.bank.BankStatement;
import com.zamanbank.aiassistant.model.Transaction;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.repository.TransactionRepository;
import com.zamanbank.aiassistant.service.parser.BankStatementParserService;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final BankStatementParserService bankStatementParserService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить транзакции пользователя")
    public ResponseEntity<List<Transaction>> getUserTransactions(@PathVariable UUID userId) {
        try {
            List<Transaction> transactions = transactionRepository.findByUserId(userId);
            log.info("Найдено {} транзакций для пользователя {}", transactions.size(), userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Ошибка при получении транзакций пользователя", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Получить общее количество транзакций")
    public ResponseEntity<Long> getTransactionCount() {
        try {
            long count = transactionRepository.count();
            log.info("Общее количество транзакций в БД: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Ошибка при получении количества транзакций", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/parse")
    public ResponseEntity<?> parseStatement(@RequestParam("file") MultipartFile file,
      @RequestParam("userId") UUID userId) throws IOException {
        Path temp = Files.createTempFile("statement-", file.getOriginalFilename());
        file.transferTo(temp.toFile());

        // Получаем пользователя из БД
        User user = new User();
        user.setId(userId);

        BankStatement statement = bankStatementParserService.parseAndSaveStatement(temp.toString(), user);

        return ResponseEntity.ok(statement);
    }
}
