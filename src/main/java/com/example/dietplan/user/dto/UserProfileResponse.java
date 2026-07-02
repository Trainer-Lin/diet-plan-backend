package com.example.dietplan.user.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private String gender;
    private Integer age;
    private BigDecimal height;
    private BigDecimal weight;
    private String activity;
    private Integer tdee;
    private BigDecimal targetWeight;
    private Integer targetCalories;
}
