package com.zamanbank.aiassistant.service;

import com.zamanbank.aiassistant.dto.ReportDto;

import java.time.LocalDate;

public interface ReportService {
    ReportDto generateReport(String userPhone, LocalDate startDate, LocalDate endDate, String reportType);
}
