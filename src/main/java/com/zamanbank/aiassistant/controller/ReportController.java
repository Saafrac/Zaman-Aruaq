package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.dto.ReportDto;
import com.zamanbank.aiassistant.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {
    
    private final ReportService reportService;
    
    @GetMapping("/monthly")
    public ResponseEntity<ReportDto> getMonthlyReport(Authentication authentication) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(1);
            
            ReportDto report = reportService.generateReport(authentication.getName(), startDate, endDate, "MONTHLY");
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Ошибка при генерации месячного отчета", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/quarterly")
    public ResponseEntity<ReportDto> getQuarterlyReport(Authentication authentication) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(3);
            
            ReportDto report = reportService.generateReport(authentication.getName(), startDate, endDate, "QUARTERLY");
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Ошибка при генерации квартального отчета", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/half-yearly")
    public ResponseEntity<ReportDto> getHalfYearlyReport(Authentication authentication) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(6);
            
            ReportDto report = reportService.generateReport(authentication.getName(), startDate, endDate, "HALF_YEARLY");
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Ошибка при генерации полугодового отчета", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/yearly")
    public ResponseEntity<ReportDto> getYearlyReport(Authentication authentication) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusYears(1);
            
            ReportDto report = reportService.generateReport(authentication.getName(), startDate, endDate, "YEARLY");
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Ошибка при генерации годового отчета", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/custom")
    public ResponseEntity<ReportDto> getCustomReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            Authentication authentication) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            ReportDto report = reportService.generateReport(authentication.getName(), start, end, "CUSTOM");
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Ошибка при генерации пользовательского отчета", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
