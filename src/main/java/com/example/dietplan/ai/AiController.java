package com.example.dietplan.ai;

import com.example.dietplan.common.result.ApiResponse; // 项目统一的响应封装类
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 控制器
 * 暴露 HTTP 接口给前端调用
 */
@RestController // 标记为 REST 控制器，返回 JSON 格式数据
@RequestMapping("/api/ai") // 这个控制器下所有接口的 URL 前缀
@RequiredArgsConstructor // 自动生成构造函数，用于注入 AiService
public class AiController {

    private final AiService aiService; // 注入 AI 服务

    /**
     * AI 聊天接口
     * <p>
     * 前端 POST /api/ai/chat
     * 请求体: {"content": "我今天吃了..."}
     * 响应体: {"code": 200, "data": "根据你的饮食...", "message": "success"}
     *
     * @param request 前端传来的用户消息
     * @return AI 的回复
     */
    @PostMapping("/chat") // 处理 POST 请求，路径为 /api/ai/chat
    public ApiResponse<String> chat(@Valid @RequestBody AiChatRequest request) {
        // @Valid 触发参数校验（如 @NotBlank）
        // @RequestBody 将 JSON 请求体自动转换为 Java 对象

        // 调用服务层获取 AI 回复
        String reply = aiService.chat(request.getContent());

        // 用统一的响应格式返回
        return ApiResponse.success(reply);
    }

    /**
     * AI 健康建议接口
     * <p>
     * 前端 POST /api/ai/advice
     * 请求体: 包含用户体重、目标体重、今日摄入等健康指标
     * 响应体: {"code": 200, "data": {"brief": "精简建议", "detailed": "详细建议"}, "message": "success"}
     *
     * @param request 前端传来的健康指标
     * @return 包含精简和详细建议的响应对象
     */
    @PostMapping("/advice") // 处理 POST 请求，路径为 /api/ai/advice
    public ApiResponse<AiAdviceResponse> advice(@Valid @RequestBody AiAdviceRequest request) {
        // @RequestBody 将 JSON 请求体自动转换为 Java 对象

        // 调用服务层生成个性化健康建议
        AiAdviceResponse advice = aiService.generateAdvice(request);

        // 用统一的响应格式返回
        return ApiResponse.success(advice);
    }
}
