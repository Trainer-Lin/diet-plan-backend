package com.example.dietplan.admin.controller;

import com.example.dietplan.admin.dto.SystemStatsResponse;
import com.example.dietplan.admin.service.AdminService;
import com.example.dietplan.common.result.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminService adminService;

    @GetMapping("/system")
    public ApiResponse<SystemStatsResponse> getSystemStats() {
        return ApiResponse.success(adminService.getSystemStats());
    }
}
