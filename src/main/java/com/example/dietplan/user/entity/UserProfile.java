package com.example.dietplan.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("user_profile")
public class UserProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String gender;
    private Integer age;
    private BigDecimal height;
    private BigDecimal weight;
    private String activityLevel;
    private BigDecimal targetWeight;
    private Integer targetCalories;
    private LocalDateTime updatedAt;
}
