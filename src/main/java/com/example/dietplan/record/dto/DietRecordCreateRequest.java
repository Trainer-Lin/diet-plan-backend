package com.example.dietplan.record.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class DietRecordCreateRequest {
    @NotNull(message = "记录日期不能为空")
    private LocalDate recordDate;

    @NotBlank(message = "餐次不能为空")
    private String meal;

    @NotBlank(message = "时间不能为空")
    private String time;

    private String note;

    @Valid
    @NotEmpty(message = "食物明细不能为空")
    private List<DietRecordItemRequest> foods;
}
