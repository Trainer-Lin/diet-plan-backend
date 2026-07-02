package com.example.dietplan.record.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

@Data
@TableName("diet_record_item")
public class DietRecordItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recordId;
    private Long foodId;
    private String foodNameSnapshot;
    private String amount;
    private Integer calories;
    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fat;
}
