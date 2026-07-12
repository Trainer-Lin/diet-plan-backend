package com.example.dietplan.ai;

import lombok.Data;

@Data
public class FoodNutritionResponse {
    private String name;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;
    private Double servingSize;
    private String servingUnit;
    private String note;
}