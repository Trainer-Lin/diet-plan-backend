package com.example.dietplan.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * AI 服务层
 * 负责调用 Kimi API 并处理响应
 */
@Slf4j
@Service // 标记为服务层组件，会被 Spring 容器管理
@RequiredArgsConstructor // Lombok 注解，自动生成构造函数（用于依赖注入）
public class AiService {

    private final WebClient webClient; // 注入配置好的 WebClient
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        try {
            return callKimi(body); // 调用 Kimi API 并返回回复内容
        } catch (Exception e) {
            log.error("调用 Kimi 聊天接口失败", e);
            return "AI 服务暂时不可用，请稍后重试。";
        }
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
                "model", "kimi-k2.5", // 指定使用的模型
                "messages", List.of(
                        // system 角色：设定 AI 的行为和人设，并规定输出格式
                        Map.of("role", "system", "content", "你是一个专业的营养师。请根据用户提供的健康指标，生成饮食建议。必须严格按 JSON 格式输出，不要包含任何其他说明文字。JSON 格式为：{\"brief\": \"精简建议，10字以内\", \"detailed\": \"详细建议，200-300字\"}"),
                        // user 角色：把健康指标作为用户消息发送
                        Map.of("role", "user", "content", prompt)
                ),
                "response_format", Map.of("type", "json_object") // 强制 AI 返回 JSON 格式
        );

        try {
            // 调用 Kimi API，返回的是 JSON 字符串
            String json = callKimi(body);
            // 解析 JSON 字符串为 AiAdviceResponse 对象
            return parseAdviceResponse(json);
        } catch (Exception e) {
            log.error("调用 Kimi 建议接口失败", e);
            return defaultAdviceResponse();
        }
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
                .uri("/chat/completions") // Kimi API 的聊天接口路径
                .bodyValue(body) // 设置请求体
                .retrieve() // 执行请求
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException(
                                "Kimi API 错误: " + clientResponse.statusCode())))
                .bodyToMono(Map.class) // 将响应体解析为 Map
                .block(); // 同步等待结果

        // 解析响应结构
        // Kimi API 响应格式：{"choices": [{"message": {"content": "AI的回复"}}]}

        // 1. 获取 choices 数组
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");

        // 2. 获取第一个 choice 的 message 对象
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        // 3. 返回 AI 的回复内容，并清理 markdown 代码块标记
        return cleanJsonContent((String) message.get("content"));
    }

    /**
     * 清理 Kimi 返回内容中可能包含的 markdown 代码块标记
     */
    private String cleanJsonContent(String content) {
        if (content == null) {
            return null;
        }
        String cleaned = content.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring("```json".length());
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring("```".length());
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - "```".length());
        }
        return cleaned.trim();
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
            return objectMapper.readValue(json, AiAdviceResponse.class);
        } catch (Exception e) {
            log.warn("解析 AI 建议响应失败，返回默认建议：{}", json, e);
            // 如果解析失败，返回默认建议
            return defaultAdviceResponse();
        }
    }

    /**
     * 根据用户身体数据生成一周饮食与健身计划
     */
    public WeeklyPlanResponse generateWeeklyPlan(WeeklyPlanRequest request) {
        String prompt = buildWeeklyPlanPrompt(request);

        Map<String, Object> body = Map.of(
                "model", "kimi-k2.5",
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "你是一个专业的营养师和健身教练。请根据用户提供的身体数据，生成一周（7天）的个性化饮食计划与健身计划。" +
                                "必须严格按 JSON 格式输出，不要包含任何其他说明文字。JSON 格式为：" +
                                "{\"summary\": \"总体概述50字以内\"," +
                                "\"days\": [" +
                                "  {\"dayOfWeek\": \"周一\", \"date\": \"07/14\"," +
                                "   \"dietPlan\": {\"totalCalories\": 1800," +
                                "     \"breakfast\": {\"name\": \"早餐名称\", \"description\": \"具体食物描述\", \"calories\": 400}," +
                                "     \"lunch\": {\"name\": \"午餐名称\", \"description\": \"具体食物描述\", \"calories\": 600}," +
                                "     \"dinner\": {\"name\": \"晚餐名称\", \"description\": \"具体食物描述\", \"calories\": 500}," +
                                "     \"snacks\": [{\"name\": \"加餐名称\", \"description\": \"具体食物描述\", \"calories\": 300}]" +
                                "   }," +
                                "   \"fitnessPlan\": {\"estimatedCaloriesBurned\": 300," +
                                "     \"warmUp\": \"热身动作描述\", \"mainWorkout\": \"主训练描述\", \"coolDown\": \"放松拉伸描述\", \"notes\": \"注意事项\"}" +
                                "  }" +
                                "]}"),
                        Map.of("role", "user", "content", prompt)
                ),
                "response_format", Map.of("type", "json_object")
        );

        try {
            String json = callKimi(body);
            return objectMapper.readValue(json, WeeklyPlanResponse.class);
        } catch (Exception e) {
            log.error("调用 Kimi 周计划接口失败", e);
            WeeklyPlanResponse fallback = defaultWeeklyPlanResponse();
            fallback.setSummary("生成失败: " + e.getMessage());
            return fallback;
        }
    }

    /**
     * 查询食物营养信息
     */
    public FoodNutritionResponse queryFoodNutrition(FoodNutritionRequest request) {
        String prompt = String.format(
                "请查询以下食物的营养信息（每100g或每份标准份量）：\n" +
                        "食物：%s\n\n" +
                        "请以 JSON 格式返回，字段如下：\n" +
                        "name: 食物名称\n" +
                        "calories: 热量(kcal)\n" +
                        "protein: 蛋白质(g)\n" +
                        "carbs: 碳水化合物(g)\n" +
                        "fat: 脂肪(g)\n" +
                        "servingSize: 参考份量数值\n" +
                        "servingUnit: 参考份量单位（如：克、个、碗、杯）\n" +
                        "note: 补充说明（如\"按每100g计算\"或\"按1个中等大小计算\"），不超过30字\n\n" +
                        "如果食物名称不明确，请给出最常见的品类。所有数值保留一位小数。",
                request.getFoodName()
        );

        Map<String, Object> body = Map.of(
                "model", "kimi-k2.5",
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "你是一个专业的营养数据库。根据用户输入的食物名称，返回该食物的营养成分信息。" +
                                "必须严格按 JSON 格式输出，不要包含任何其他说明文字。"),
                        Map.of("role", "user", "content", prompt)
                ),
                "response_format", Map.of("type", "json_object")
        );

        try {
            String json = callKimi(body);
            return objectMapper.readValue(json, FoodNutritionResponse.class);
        } catch (Exception e) {
            log.error("查询食物营养信息失败", e);
            FoodNutritionResponse fallback = new FoodNutritionResponse();
            fallback.setName(request.getFoodName());
            fallback.setNote("查询失败: " + e.getMessage());
            return fallback;
        }
    }

    private String buildWeeklyPlanPrompt(WeeklyPlanRequest request) {
        // 计算下周一，生成 7 天精确日期
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate nextMonday = today.with(java.time.DayOfWeek.MONDAY);
        if (!nextMonday.isAfter(today)) {
            nextMonday = nextMonday.plusWeeks(1);
        }
        StringBuilder datesBuilder = new StringBuilder();
        String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        for (int i = 0; i < 7; i++) {
            java.time.LocalDate d = nextMonday.plusDays(i);
            String dateStr = String.format("%02d/%02d", d.getMonthValue(), d.getDayOfMonth());
            datesBuilder.append(weekDays[i]).append(": ").append(dateStr);
            if (i < 6) datesBuilder.append(", ");
        }

        return String.format(
                "请根据以下用户身体数据，生成一周（7天，从周一到周日）的个性化饮食计划与健身计划：\n" +
                        "当前体重：%s kg\n" +
                        "目标体重：%s kg\n" +
                        "每日目标热量：%s kcal/天\n" +
                        "基础代谢(TDEE)：%s kcal\n" +
                        "身高：%s cm\n" +
                        "年龄：%s\n" +
                        "性别：%s\n" +
                        "活动水平：%s\n\n" +
                        "本周日期对应关系（必须严格使用这些日期）：%s\n\n" +
                        "要求：\n" +
                        "1. 饮食计划要具体到每餐的食物名称和描述，热量合理分配\n" +
                        "2. 健身计划要包含热身、主训练、放松拉伸的具体动作\n" +
                        "3. 每天的热量缺口控制在300-500 kcal之间\n" +
                        "4. 根据性别和活动水平调整运动强度\n" +
                        "5. date 字段必须使用上面给出的日期，格式为 MM/DD",
                request.getWeight() != null ? request.getWeight() : "未记录",
                request.getTargetWeight() != null ? request.getTargetWeight() : "未设置",
                request.getTargetCalories() != null ? request.getTargetCalories() : "未设置",
                request.getTdee() != null ? request.getTdee() : "未记录",
                request.getHeight() != null ? request.getHeight() : "未记录",
                request.getAge() != null ? request.getAge() : "未记录",
                request.getGender() != null ? request.getGender() : "未记录",
                request.getActivityLevel() != null ? request.getActivityLevel() : "未记录",
                datesBuilder.toString()
        );
    }

    private WeeklyPlanResponse defaultWeeklyPlanResponse() {
        WeeklyPlanResponse response = new WeeklyPlanResponse();
        response.setSummary("AI 服务暂时不可用，请稍后重试。");

        String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        java.util.List<WeeklyPlanResponse.DayPlan> days = new java.util.ArrayList<>();

        for (String day : weekDays) {
            WeeklyPlanResponse.DayPlan dp = new WeeklyPlanResponse.DayPlan();
            dp.setDayOfWeek(day);
            dp.setDate("--");

            WeeklyPlanResponse.DietPlan diet = new WeeklyPlanResponse.DietPlan();
            diet.setTotalCalories(0);
            WeeklyPlanResponse.Meal emptyMeal = new WeeklyPlanResponse.Meal();
            emptyMeal.setName("暂无数据");
            emptyMeal.setDescription("AI 服务暂时不可用");
            emptyMeal.setCalories(0);
            diet.setBreakfast(emptyMeal);
            diet.setLunch(emptyMeal);
            diet.setDinner(emptyMeal);
            dp.setDietPlan(diet);

            WeeklyPlanResponse.FitnessPlan fitness = new WeeklyPlanResponse.FitnessPlan();
            fitness.setEstimatedCaloriesBurned(0);
            fitness.setWarmUp("暂无数据");
            fitness.setMainWorkout("暂无数据");
            fitness.setCoolDown("暂无数据");
            fitness.setNotes("AI 服务暂时不可用");
            dp.setFitnessPlan(fitness);

            days.add(dp);
        }
        response.setDays(days);
        return response;
    }

    /**
     * 返回默认的 AI 建议兜底内容
     */
    private AiAdviceResponse defaultAdviceResponse() {
        AiAdviceResponse response = new AiAdviceResponse();
        response.setBrief("保持规律饮食");
        response.setDetailed("目前无法生成个性化建议，请确保各项健康指标已正确记录。建议保持规律饮食、均衡摄入蛋白质/碳水/脂肪，并持续记录每日摄入。");
        return response;
    }
}
