package com.example.dietplan.admin.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserProfileResponse {
    private Long userId;
    private String username;
    private String nickname;
    private String role;
    private String gender;
    private Integer age;
    private BigDecimal height;
    private BigDecimal weight;
    private String activity;
    private Integer tdee;
    private BigDecimal targetWeight;
    private Integer targetCalories;
}
