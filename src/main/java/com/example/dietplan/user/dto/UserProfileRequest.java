package com.example.dietplan.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class UserProfileRequest {
    @NotBlank(message = "性别不能为空")
    private String gender;

    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "年龄不能小于 1")
    @Max(value = 120, message = "年龄不能大于 120")
    private Integer age;

    @NotNull(message = "身高不能为空")
    private BigDecimal height;

    @NotNull(message = "体重不能为空")
    private BigDecimal weight;

    @NotBlank(message = "活动等级不能为空")
    private String activity;

    private BigDecimal targetWeight;
}
