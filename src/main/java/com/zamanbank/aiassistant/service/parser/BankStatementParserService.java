package com.zamanbank.aiassistant.service.parser;

import com.zamanbank.aiassistant.dto.bank.BankStatement;
import com.zamanbank.aiassistant.model.User;
import com.zamanbank.aiassistant.service.impl.TransactionSaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankStatementParserService {

  private final BankStatementPdfParserService pdfParser;
  private final KaspiBankStatementParserService kaspiParser;
  private final TransactionSaveService transactionSaveService;

  public BankStatement parseAndSaveStatement(String filePath, User user) throws IOException {
    log.info("Начинаем парсинг и сохранение выписки: {}", filePath);

    BankStatement statement = parseStatement(filePath);
    transactionSaveService.saveParsedStatement(statement, user);

    return statement;
  }

  public BankStatement parseStatement(String filePath) throws IOException {
    if (filePath.endsWith(".pdf")) {
      return pdfParser.parsePdfStatement(filePath);
    } else if (filePath.endsWith(".txt")) {
      String content = new String(Files.readAllBytes(Paths.get(filePath)));

      if (content.contains("Kaspi Bank") || content.contains("Kaspi Gold")) {
        return kaspiParser.parseKaspiStatement(content);
      } else {
        return pdfParser.parsePdfStatement(filePath);
      }
    } else {
      throw new UnsupportedOperationException("Неподдерживаемый формат файла: " + filePath);
    }
  }

  public boolean isSupportedFormat(String fileName) {
    return fileName.endsWith(".pdf") || fileName.endsWith(".txt");
  }
}