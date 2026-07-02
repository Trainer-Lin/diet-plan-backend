package com.example.dietplan.stats.service;

import com.example.dietplan.stats.dto.CheckinStatsResponse;
import com.example.dietplan.stats.dto.TodayStatsResponse;
import com.example.dietplan.stats.dto.WeeklyCaloriesResponse;
import com.example.dietplan.stats.dto.WeeklyMacrosResponse;
import com.example.dietplan.stats.dto.WeightTrendPointResponse;
import java.util.List;

public interface StatsService {
    TodayStatsResponse getTodayStats(Long userId);

    WeeklyCaloriesResponse getWeeklyCalories(Long userId);

    WeeklyMacrosResponse getWeeklyMacros(Long userId);

    CheckinStatsResponse getCheckinStats(Long userId);

    List<WeightTrendPointResponse> getWeightTrend(Long userId);
}
