package com.example.dietplan.stats.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeeklyMacrosResponse {
    private List<String> days;
    private List<BigDecimal> protein;
    private List<BigDecimal> carbs;
    private List<BigDecimal> fat;
}
