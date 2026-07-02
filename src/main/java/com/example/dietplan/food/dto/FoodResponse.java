package com.example.dietplan.food.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FoodResponse {
    private Long id;
    private String name;
    private String category;
    private String serving;
    private Integer calories;
    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fat;
    private List<String> tags;
}
