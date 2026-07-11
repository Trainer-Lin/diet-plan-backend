package com.example.dietplan.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FoodReviewTicketResponse {
    private Long id;
    private Long foodId;
    private String name;
    private String category;
    private String serving;
    private Integer calories;
    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fat;
    private Long submitterId;
    private String submitterUsername;
    private String submitterNickname;
    private String status;
    private Long reviewerId;
    private String reviewerUsername;
    private String remark;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
}
