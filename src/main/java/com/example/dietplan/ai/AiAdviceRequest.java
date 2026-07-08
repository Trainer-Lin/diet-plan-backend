package com.example.dietplan.ai;

import lombok.Data;

/**
 * AI 健康建议请求的数据传输对象（DTO）
 * 用于接收前端传来的各项健康指标
 */
@Data // Lombok 注解，自动生成 getter/setter/toString 等方法
public class AiAdviceRequest {

    // 用户当前体重（单位：kg）
    private Double weight;

    // 用户目标体重（单位：kg），可能未设置
    private Double targetWeight;

    // 每日目标热量（单位：kcal）
    private Integer targetCalories;

    // 用户今日已摄入热量（单位：kcal）
    private Integer todayCalories;

    // 用户今日已摄入蛋白质（单位：g）
    private Integer todayProtein;

    // 用户今日已摄入碳水（单位：g）
    private Integer todayCarbs;

    // 用户今日已摄入脂肪（单位：g）
    private Integer todayFat;

    // 用户身高（单位：cm）
    private Double height;

    // 用户年龄
    private Integer age;

    // 用户性别
    private String gender;

    // 本周平均每日热量差（实际摄入 - 目标热量）
    private Integer averageDailyDiff;

    // 本周打卡完成天数
    private Integer completedDays;

    // 本周总天数
    private Integer totalDays;
}
