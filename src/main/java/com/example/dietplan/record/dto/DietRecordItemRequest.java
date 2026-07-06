package com.example.dietplan.record.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DietRecordItemRequest {
    private Long foodId;

    @NotBlank(message = "食物名称不能为空")
    private String name;

    @NotBlank(message = "食物份量不能为空")
    private String amount;

    @NotNull(message = "热量不能为空")
    private Integer calories;

    @NotNull(message = "蛋白质不能为空")
    private BigDecimal protein;

    @NotNull(message = "碳水不能为空")
    private BigDecimal carbs;

    @NotNull(message = "脂肪不能为空")
    private BigDecimal fat;
}
