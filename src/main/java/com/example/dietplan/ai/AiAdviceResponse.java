package com.example.dietplan.ai;

import lombok.Data;

/**
 * AI 健康建议响应的数据传输对象（DTO）
 * 包含精简版建议和详细版建议
 */
@Data // Lombok 注解，自动生成 getter/setter/toString 等方法
public class AiAdviceResponse {

    // 精简版建议，用于总览看板展示
    private String brief;

    // 详细版建议，用于统计分析页面展示
    private String detailed;
}
