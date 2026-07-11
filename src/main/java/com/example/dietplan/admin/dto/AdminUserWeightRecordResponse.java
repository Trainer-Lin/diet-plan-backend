package com.example.dietplan.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserWeightRecordResponse {
    private Long id;
    private LocalDate recordDate;
    private BigDecimal weight;
    private BigDecimal bmi;
}
