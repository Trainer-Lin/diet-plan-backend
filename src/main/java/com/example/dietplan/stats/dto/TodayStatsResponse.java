package com.example.dietplan.stats.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TodayStatsResponse {
    private Integer totalCalories;
    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fat;
}
