package com.example.dietplan.ai;

import lombok.Data;

@Data
public class WeeklyPlanRequest {
    private Double weight;
    private Double targetWeight;
    private Integer targetCalories;
    private Double height;
    private Integer age;
    private String gender;
    private String activityLevel;
    private Integer tdee;
}