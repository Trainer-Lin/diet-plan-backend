package com.example.dietplan.stats.controller;

import com.example.dietplan.common.context.CurrentUserContext;
import com.example.dietplan.common.result.ApiResponse;
import com.example.dietplan.stats.dto.CheckinStatsResponse;
import com.example.dietplan.stats.dto.TodayStatsResponse;
import com.example.dietplan.stats.dto.WeeklyCaloriesResponse;
import com.example.dietplan.stats.dto.WeeklyMacrosResponse;
import com.example.dietplan.stats.dto.WeightTrendPointResponse;
import com.example.dietplan.stats.service.StatsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/today")
    public ApiResponse<TodayStatsResponse> today() {
        return ApiResponse.success(statsService.getTodayStats(CurrentUserContext.getUserId()));
    }

    @GetMapping("/weekly-calories")
    public ApiResponse<WeeklyCaloriesResponse> weeklyCalories() {
        return ApiResponse.success(statsService.getWeeklyCalories(CurrentUserContext.getUserId()));
    }

    @GetMapping("/weekly-macros")
    public ApiResponse<WeeklyMacrosResponse> weeklyMacros() {
        return ApiResponse.success(statsService.getWeeklyMacros(CurrentUserContext.getUserId()));
    }

    @GetMapping("/checkin")
    public ApiResponse<CheckinStatsResponse> checkin() {
        return ApiResponse.success(statsService.getCheckinStats(CurrentUserContext.getUserId()));
    }

    @GetMapping("/weight-trend")
    public ApiResponse<List<WeightTrendPointResponse>> weightTrend() {
        return ApiResponse.success(statsService.getWeightTrend(CurrentUserContext.getUserId()));
    }
}
