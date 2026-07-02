package com.example.dietplan.stats.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeeklyCaloriesResponse {
    private List<String> days;
    private List<Integer> calories;
}
