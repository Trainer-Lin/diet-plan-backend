package com.example.dietplan.stats.service.impl;

import com.example.dietplan.record.mapper.WeightRecordMapper;
import com.example.dietplan.stats.dto.CheckinStatsResponse;
import com.example.dietplan.stats.dto.TodayStatsResponse;
import com.example.dietplan.stats.dto.WeeklyCaloriesResponse;
import com.example.dietplan.stats.dto.WeeklyMacrosResponse;
import com.example.dietplan.stats.dto.WeightTrendPointResponse;
import com.example.dietplan.stats.service.StatsService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final WeightRecordMapper weightRecordMapper;

    @Override
    public TodayStatsResponse getTodayStats(Long userId) {
        return TodayStatsResponse.builder()
                .totalCalories(0)
                .protein(BigDecimal.ZERO)
                .carbs(BigDecimal.ZERO)
                .fat(BigDecimal.ZERO)
                .build();
    }

    @Override
    public WeeklyCaloriesResponse getWeeklyCalories(Long userId) {
        return WeeklyCaloriesResponse.builder()
                .days(List.of("周一", "周二", "周三", "周四", "周五", "周六", "周日"))
                .calories(List.of(0, 0, 0, 0, 0, 0, 0))
                .build();
    }

    @Override
    public WeeklyMacrosResponse getWeeklyMacros(Long userId) {
        List<String> days = List.of("周一", "周二", "周三", "周四", "周五", "周六", "周日");
        List<BigDecimal> zeroLine = List.of(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        return WeeklyMacrosResponse.builder()
                .days(days)
                .protein(zeroLine)
                .carbs(zeroLine)
                .fat(zeroLine)
                .build();
    }

    @Override
    public CheckinStatsResponse getCheckinStats(Long userId) {
        return CheckinStatsResponse.builder()
                .completedDays(0)
                .totalDays(7)
                .statuses(List.of("rest", "rest", "rest", "rest", "rest", "rest", "rest"))
                .build();
    }

    @Override
    public List<WeightTrendPointResponse> getWeightTrend(Long userId) {
        // 后续可改为按用户最近 7-30 次体重记录进行聚合。
        return weightRecordMapper.selectList(null).stream()
                .map(record -> WeightTrendPointResponse.builder()
                        .day(record.getRecordDate().toString())
                        .value(record.getWeight())
                        .goalReached(Boolean.FALSE)
                        .build())
                .toList();
    }
}
