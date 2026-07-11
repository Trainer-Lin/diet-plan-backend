package com.example.dietplan.stats.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckinStatsResponse {
    private Integer completedDays;
    private Integer totalDays;
    private List<String> statuses;
    private List<String> days;
}
