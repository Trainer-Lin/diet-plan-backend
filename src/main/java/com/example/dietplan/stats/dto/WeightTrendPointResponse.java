package com.example.dietplan.stats.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeightTrendPointResponse {
    private String day;
    private BigDecimal value;
    private Boolean goalReached;
}
