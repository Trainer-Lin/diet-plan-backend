package com.example.dietplan.stats.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.dietplan.record.entity.DietRecord;
import com.example.dietplan.record.entity.DietRecordItem;
import com.example.dietplan.record.mapper.DietRecordItemMapper;
import com.example.dietplan.record.mapper.DietRecordMapper;
import com.example.dietplan.record.entity.WeightRecord;
import com.example.dietplan.record.mapper.WeightRecordMapper;
import com.example.dietplan.stats.dto.CheckinStatsResponse;
import com.example.dietplan.stats.dto.TodayStatsResponse;
import com.example.dietplan.stats.dto.WeeklyCaloriesResponse;
import com.example.dietplan.stats.dto.WeeklyMacrosResponse;
import com.example.dietplan.stats.dto.WeightTrendPointResponse;
import com.example.dietplan.stats.service.StatsService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private static final DateTimeFormatter DAY_LABEL_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    private final DietRecordMapper dietRecordMapper;
    private final DietRecordItemMapper dietRecordItemMapper;
    private final WeightRecordMapper weightRecordMapper;

    @Override
    public TodayStatsResponse getTodayStats(Long userId) {
        List<DietRecord> todayRecords = loadRecordsBetween(userId, LocalDate.now(), LocalDate.now());
        List<DietRecordItem> items = loadItemsByRecordIds(todayRecords.stream().map(DietRecord::getId).toList());

        return TodayStatsResponse.builder()
                .totalCalories(todayRecords.stream().mapToInt(DietRecord::getTotalCalories).sum())
                .protein(sumMacro(items, MacroType.PROTEIN))
                .carbs(sumMacro(items, MacroType.CARBS))
                .fat(sumMacro(items, MacroType.FAT))
                .build();
    }

    @Override
    public WeeklyCaloriesResponse getWeeklyCalories(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        List<DietRecord> weeklyRecords = loadRecordsBetween(userId, startDate, endDate);
        Map<LocalDate, Integer> caloriesByDate = new LinkedHashMap<>();
        List<String> days = new ArrayList<>();
        List<Integer> calories = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            caloriesByDate.put(date, 0);
            days.add(date.format(DAY_LABEL_FORMATTER));
        }

        weeklyRecords.forEach(record ->
                caloriesByDate.computeIfPresent(
                        record.getRecordDate(),
                        (date, total) -> total + (record.getTotalCalories() == null ? 0 : record.getTotalCalories())
                )
        );
        calories.addAll(caloriesByDate.values());

        return WeeklyCaloriesResponse.builder()
                .days(days)
                .calories(calories)
                .build();
    }

    @Override
    public WeeklyMacrosResponse getWeeklyMacros(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        List<DietRecord> weeklyRecords = loadRecordsBetween(userId, startDate, endDate);
        List<Long> recordIds = weeklyRecords.stream().map(DietRecord::getId).toList();
        List<DietRecordItem> weeklyItems = loadItemsByRecordIds(recordIds);
        Map<Long, LocalDate> recordDateMap = weeklyRecords.stream()
                .collect(LinkedHashMap::new, (map, record) -> map.put(record.getId(), record.getRecordDate()), Map::putAll);
        Map<LocalDate, BigDecimal> proteinByDate = new LinkedHashMap<>();
        Map<LocalDate, BigDecimal> carbsByDate = new LinkedHashMap<>();
        Map<LocalDate, BigDecimal> fatByDate = new LinkedHashMap<>();
        List<String> days = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            proteinByDate.put(date, BigDecimal.ZERO);
            carbsByDate.put(date, BigDecimal.ZERO);
            fatByDate.put(date, BigDecimal.ZERO);
            days.add(date.format(DAY_LABEL_FORMATTER));
        }

        weeklyItems.forEach(item -> {
            LocalDate date = recordDateMap.get(item.getRecordId());
            if (date == null) {
                return;
            }
            proteinByDate.computeIfPresent(date, (key, value) -> value.add(defaultDecimal(item.getProtein())));
            carbsByDate.computeIfPresent(date, (key, value) -> value.add(defaultDecimal(item.getCarbs())));
            fatByDate.computeIfPresent(date, (key, value) -> value.add(defaultDecimal(item.getFat())));
        });

        return WeeklyMacrosResponse.builder()
                .days(days)
                .protein(new ArrayList<>(proteinByDate.values()))
                .carbs(new ArrayList<>(carbsByDate.values()))
                .fat(new ArrayList<>(fatByDate.values()))
                .build();
    }

    @Override
    public CheckinStatsResponse getCheckinStats(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        List<DietRecord> weeklyRecords = loadRecordsBetween(userId, startDate, endDate);
        Map<LocalDate, Integer> caloriesByDate = new LinkedHashMap<>();
        List<String> statuses = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            caloriesByDate.put(date, 0);
        }

        weeklyRecords.forEach(record ->
                caloriesByDate.computeIfPresent(
                        record.getRecordDate(),
                        (date, total) -> total + (record.getTotalCalories() == null ? 0 : record.getTotalCalories())
                )
        );

        caloriesByDate.values().forEach(total -> statuses.add(total > 0 ? "done" : "rest"));

        return CheckinStatsResponse.builder()
                .completedDays((int) caloriesByDate.values().stream().filter(total -> total > 0).count())
                .totalDays(7)
                .statuses(statuses)
                .build();
    }

    @Override
    public List<WeightTrendPointResponse> getWeightTrend(Long userId) {
        return weightRecordMapper.selectList(new LambdaQueryWrapper<WeightRecord>()
                        .eq(WeightRecord::getUserId, userId)
                        .orderByAsc(WeightRecord::getRecordDate))
                .stream()
                .map(record -> WeightTrendPointResponse.builder()
                        .day(record.getRecordDate().toString())
                        .value(record.getWeight())
                        .goalReached(Boolean.FALSE)
                        .build())
                .toList();
    }

    private List<DietRecord> loadRecordsBetween(Long userId, LocalDate startDate, LocalDate endDate) {
        return dietRecordMapper.selectList(new LambdaQueryWrapper<DietRecord>()
                .eq(DietRecord::getUserId, userId)
                .between(DietRecord::getRecordDate, startDate, endDate)
                .orderByAsc(DietRecord::getRecordDate)
                .orderByAsc(DietRecord::getCreatedAt));
    }

    private List<DietRecordItem> loadItemsByRecordIds(List<Long> recordIds) {
        if (recordIds.isEmpty()) {
            return List.of();
        }
        return dietRecordItemMapper.selectList(new LambdaQueryWrapper<DietRecordItem>()
                .in(DietRecordItem::getRecordId, recordIds));
    }

    private BigDecimal sumMacro(List<DietRecordItem> items, MacroType macroType) {
        return items.stream()
                .map(item -> switch (macroType) {
                    case PROTEIN -> defaultDecimal(item.getProtein());
                    case CARBS -> defaultDecimal(item.getCarbs());
                    case FAT -> defaultDecimal(item.getFat());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private enum MacroType {
        PROTEIN,
        CARBS,
        FAT
    }
}
