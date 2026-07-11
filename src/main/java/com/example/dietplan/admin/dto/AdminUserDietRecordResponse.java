package com.example.dietplan.admin.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserDietRecordResponse {
    private Long id;
    private LocalDate recordDate;
    private String mealType;
    private String note;
    private Integer totalCalories;
    private LocalDateTime createdAt;
    private List<DietRecordItem> items;

    @Data
    @Builder
    public static class DietRecordItem {
        private Long id;
        private String foodName;
        private String amount;
        private Integer calories;
        private java.math.BigDecimal protein;
        private java.math.BigDecimal carbs;
        private java.math.BigDecimal fat;
    }
}
