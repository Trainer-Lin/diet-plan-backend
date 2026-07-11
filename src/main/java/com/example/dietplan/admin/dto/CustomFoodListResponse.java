package com.example.dietplan.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomFoodListResponse {
    private Long id;
    private String name;
    private String category;
    private String serving;
    private Integer calories;
    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fat;
    private Long createdBy;
    private String creatorUsername;
    private String creatorNickname;
    private LocalDateTime createdAt;
}
