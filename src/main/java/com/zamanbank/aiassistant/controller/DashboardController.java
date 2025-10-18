package com.zamanbank.aiassistant.controller;

import com.zamanbank.aiassistant.dto.DashboardDto;
import com.zamanbank.aiassistant.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping
    public ResponseEntity<DashboardDto> getDashboard(Authentication authentication) {
        try {
            DashboardDto dashboard = dashboardService.getDashboard(authentication.getName());
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Ошибка при получении дашборда", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/analytics")
    public ResponseEntity<DashboardDto> getAnalytics(
            @RequestParam(defaultValue = "6") int months,
            Authentication authentication) {
        try {
            DashboardDto analytics = dashboardService.getAnalytics(authentication.getName(), months);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Ошибка при получении аналитики", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/summary")
    public ResponseEntity<DashboardDto> getSummary(Authentication authentication) {
        try {
            DashboardDto summary = dashboardService.getSummary(authentication.getName());
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Ошибка при получении сводки", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
