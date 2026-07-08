package com.example.dietplan.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 聊天请求的数据传输对象（DTO）
 * 用于接收前端传来的用户消息
 */
@Data // Lombok 注解，自动生成 getter/setter/toString 等方法
public class AiChatRequest {

    @NotBlank(message = "内容不能为空") // 校验注解：不能为空字符串或 null
    private String content; // 用户发送的消息内容
}
