package com.example.dietplan.record.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DailyDietRecordResponse {
    private Long id;
    private String meal;
    private String time;
    private Integer totalCalories;
    private String note;
    private List<DietRecordItemResponse> foods;
}
