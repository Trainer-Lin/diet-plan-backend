package com.example.dietplan.record.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DietRecordItemResponse {
    private Long id;
    private String name;
    private String amount;
    private Integer calories;
}
