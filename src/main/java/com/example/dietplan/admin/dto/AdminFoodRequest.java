package com.example.dietplan.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class AdminFoodRequest {
    @NotBlank(message = "食物名称不能为空")
    private String name;
    @NotBlank(message = "食物分类不能为空")
    private String category;
    @NotBlank(message = "份量单位不能为空")
    private String servingUnit;
    @NotNull(message = "标准份量不能为空")
    private BigDecimal servingSize;
    @NotNull(message = "热量不能为空")
    private Integer calories;
    @NotNull(message = "蛋白质不能为空")
    private BigDecimal protein;
    @NotNull(message = "碳水不能为空")
    private BigDecimal carbs;
    @NotNull(message = "脂肪不能为空")
    private BigDecimal fat;
}
