package com.example.dietplan.food.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum FoodReviewStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    @EnumValue
    private final String value;

    FoodReviewStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
