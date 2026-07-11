package com.example.dietplan.food.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.dietplan.food.enums.FoodReviewStatus;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("food_review_ticket")
public class FoodReviewTicket {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long foodId;
    private Long submitterId;
    private FoodReviewStatus status;
    private Long reviewerId;
    private String remark;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
