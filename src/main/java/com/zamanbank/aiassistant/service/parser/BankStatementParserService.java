package com.zamanbank.aiassistant.service.parser;

import com.zamanbank.aiassistant.dto.bank.BankStatement;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankStatementParserService {

  private final BankStatementPdfParserService pdfParser;

  public BankStatement parseStatement(String filePath) throws IOException {
    log.info("Начинаем парсинг банковской выписки: {}", filePath);

    if (filePath.endsWith(".pdf")) {
      return pdfParser.parsePdfStatement(filePath);
    } else if (filePath.endsWith(".csv")) {
      // TODO: Реализовать CSV парсер
      throw new UnsupportedOperationException("CSV парсер пока не реализован");
    } else {
      throw new UnsupportedOperationException("Неподдерживаемый формат файла: " + filePath);
    }
  }

  public boolean isSupportedFormat(String fileName) {
    return fileName.endsWith(".pdf") || fileName.endsWith(".csv");
  }
}