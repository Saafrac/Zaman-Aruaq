package com.zamanbank.aiassistant.service;

import com.zamanbank.aiassistant.dto.DashboardDto;

public interface DashboardService {
    DashboardDto getDashboard(String userPhone);
    DashboardDto getAnalytics(String userPhone, int months);
    DashboardDto getSummary(String userPhone);
}
