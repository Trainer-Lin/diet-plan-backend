package com.example.dietplan.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * AI 服务层
 * 负责调用 Kimi API 并处理响应
 */
@Service // 标记为服务层组件，会被 Spring 容器管理
@RequiredArgsConstructor // Lombok 注解，自动生成构造函数（用于依赖注入）
public class AiService {

    private final WebClient webClient; // 注入配置好的 WebClient

    /**
     * 发送消息给 Kimi AI 并获取回复
     *
     * @param userMessage 用户的消息内容
     * @return AI 的回复文本
     */
    public String chat(String userMessage) {

        // 构建请求体，Kimi API 使用 OpenAI 格式
        // 这是一个 Map 结构，会被自动序列化为 JSON
        Map<String, Object> body = Map.of(
                "model", "kimi-k2.5", // 指定使用的模型
                // messages 是对话历史数组，每条消息有 role 和 content
                "messages", List.of(
                        // system 角色：设定 AI 的行为和人设
                        Map.of("role", "system", "content", "你是一个专业的营养师，帮助用户分析饮食、提供建议。"),
                        // user 角色：用户的实际消息
                        Map.of("role", "user", "content", userMessage)
                )
        );

        return callKimi(body); // 调用 Kimi API 并返回回复内容
    }

    /**
     * 根据用户的健康指标生成个性化饮食建议
     *
     * @param request 包含体重、目标体重、摄入营养等健康指标
     * @return 包含精简建议和详细建议的响应对象
     */
    public AiAdviceResponse generateAdvice(AiAdviceRequest request) {

        // 构建给 AI 的提示词
        // 要求 AI 同时生成精简版（brief）和详细版（detailed）建议
        String prompt = buildAdvicePrompt(request);

        // 构建请求体，要求 AI 按 JSON 格式返回
        Map<String, Object> body = Map.of(
                "model", "kimi-k2-5", // 指定使用的模型
                "messages", List.of(
                        // system 角色：设定 AI 的行为和人设，并规定输出格式
                        Map.of("role", "system", "content", "你是一个专业的营养师。请根据用户提供的健康指标，生成饮食建议。必须严格按 JSON 格式输出，不要包含任何其他说明文字。JSON 格式为：{\"brief\": \"精简建议，10字以内\", \"detailed\": \"详细建议，200-300字\"}"),
                        // user 角色：把健康指标作为用户消息发送
                        Map.of("role", "user", "content", prompt)
                ),
                "response_format", Map.of("type", "json_object") // 强制 AI 返回 JSON 格式
        );

        // 调用 Kimi API，返回的是 JSON 字符串
        String json = callKimi(body);

        // 解析 JSON 字符串为 AiAdviceResponse 对象
        return parseAdviceResponse(json);
    }

    /**
     * 调用 Kimi API 并返回 AI 回复文本
     *
     * @param body 请求体
     * @return AI 的回复文本
     */
    private String callKimi(Map<String, Object> body) {
        // 发送 POST 请求并获取响应
        // block() 表示同步等待结果（阻塞当前线程直到响应返回）
        Map<String, Object> response = webClient.post()
                .uri("/v1/chat/completions") // Kimi API 的聊天接口路径
                .bodyValue(body) // 设置请求体
                .retrieve() // 执行请求
                .bodyToMono(Map.class) // 将响应体解析为 Map
                .block(); // 同步等待结果

        // 解析响应结构
        // Kimi API 响应格式：{"choices": [{"message": {"content": "AI的回复"}}]}

        // 1. 获取 choices 数组
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");

        // 2. 获取第一个 choice 的 message 对象
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        // 3. 返回 AI 的回复内容
        return (String) message.get("content");
    }

    /**
     * 根据健康指标构建给 AI 的提示词
     *
     * @param request 健康指标请求对象
     * @return 提示词字符串
     */
    private String buildAdvicePrompt(AiAdviceRequest request) {
        return String.format(
                "请根据以下健康指标生成饮食建议：\n" +
                        "当前体重：%s kg\n" +
                        "目标体重：%s kg\n" +
                        "目标热量：%s kcal/天\n" +
                        "今日摄入热量：%s kcal\n" +
                        "今日蛋白质：%s g\n" +
                        "今日碳水：%s g\n" +
                        "今日脂肪：%s g\n" +
                        "身高：%s cm\n" +
                        "年龄：%s\n" +
                        "性别：%s\n" +
                        "本周平均每日热量差：%s kcal\n" +
                        "本周打卡天数：%s/%s\n",
                request.getWeight() != null ? request.getWeight() : "未记录",
                request.getTargetWeight() != null ? request.getTargetWeight() : "未设置",
                request.getTargetCalories() != null ? request.getTargetCalories() : "未设置",
                request.getTodayCalories() != null ? request.getTodayCalories() : "未记录",
                request.getTodayProtein() != null ? request.getTodayProtein() : "未记录",
                request.getTodayCarbs() != null ? request.getTodayCarbs() : "未记录",
                request.getTodayFat() != null ? request.getTodayFat() : "未记录",
                request.getHeight() != null ? request.getHeight() : "未记录",
                request.getAge() != null ? request.getAge() : "未记录",
                request.getGender() != null ? request.getGender() : "未记录",
                request.getAverageDailyDiff() != null ? request.getAverageDailyDiff() : "未记录",
                request.getCompletedDays() != null ? request.getCompletedDays() : "未记录",
                request.getTotalDays() != null ? request.getTotalDays() : "未记录"
        );
    }

    /**
     * 解析 AI 返回的 JSON 字符串为 AiAdviceResponse 对象
     *
     * @param json AI 返回的 JSON 字符串
     * @return AiAdviceResponse 对象
     */
    private AiAdviceResponse parseAdviceResponse(String json) {
        try {
            // 使用 Jackson 的 ObjectMapper 解析 JSON
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, AiAdviceResponse.class);
        } catch (Exception e) {
            // 如果解析失败，返回默认建议
            AiAdviceResponse response = new AiAdviceResponse();
            response.setBrief("保持规律饮食");
            response.setDetailed("目前无法生成个性化建议，请确保各项健康指标已正确记录。建议保持规律饮食、均衡摄入蛋白质/碳水/脂肪，并持续记录每日摄入。");
            return response;
        }
    }
}
