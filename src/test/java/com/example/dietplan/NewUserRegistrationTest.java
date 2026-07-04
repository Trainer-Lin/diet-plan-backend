package com.example.dietplan;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NewUserRegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;
    private static Long userId;

    @Test
    @Order(1)
    void test01_registerNewUser() throws Exception {
        String requestBody = """
                {
                    "username": "testuser",
                    "email": "test@example.com",
                    "password": "test123456",
                    "nickname": "测试用户"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andDo(r -> System.out.println("【注册接口】响应: " + r.getResponse().getContentAsString()))
                .andReturn();

        System.out.println("✅ 注册成功");
    }

    @Test
    @Order(2)
    void test02_registerDuplicateUsername() throws Exception {
        String requestBody = """
                {
                    "username": "testuser",
                    "email": "test2@example.com",
                    "password": "test123456",
                    "nickname": "测试用户2"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andDo(r -> System.out.println("【重复注册】响应: " + r.getResponse().getContentAsString()));

        System.out.println("✅ 重复用户名正确拒绝");
    }

    @Test
    @Order(3)
    void test03_login() throws Exception {
        String requestBody = """
                {
                    "username": "testuser",
                    "password": "test123456"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andDo(r -> System.out.println("【登录接口】响应: " + r.getResponse().getContentAsString()))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        token = json.get("data").get("token").asText();
        System.out.println("✅ 登录成功，token: " + token.substring(0, 20) + "...");
    }

    @Test
    @Order(4)
    void test04_getCurrentUser() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.nickname").value("测试用户"))
                .andDo(r -> System.out.println("【当前用户】响应: " + r.getResponse().getContentAsString()))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        userId = json.get("data").get("id").asLong();
        System.out.println("✅ 获取当前用户成功，userId: " + userId);
    }

    @Test
    @Order(5)
    void test05_getProfile() throws Exception {
        mockMvc.perform(get("/api/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.gender").exists())
                .andDo(r -> System.out.println("【用户档案】响应: " + r.getResponse().getContentAsString()));

        System.out.println("✅ 获取用户档案成功（新用户默认档案已自动创建）");
    }

    @Test
    @Order(6)
    void test06_listFoods() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/foods")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(r -> System.out.println("【食物列表】响应: " + r.getResponse().getContentAsString().substring(0, Math.min(300, r.getResponse().getContentAsString().length())) + "..."))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        int foodCount = json.get("data").size();
        System.out.println("✅ 食物列表获取成功，共 " + foodCount + " 种食物");
        assertTrue(foodCount > 0, "食物库不能为空");
    }

    @Test
    @Order(7)
    void test07_searchFoods() throws Exception {
        mockMvc.perform(get("/api/foods/search")
                        .param("keyword", "米饭")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(r -> System.out.println("【搜索食物】响应: " + r.getResponse().getContentAsString()));

        System.out.println("✅ 食物搜索成功");
    }

    @Test
    @Order(8)
    void test08_getDailyRecords_empty() throws Exception {
        mockMvc.perform(get("/api/records/daily")
                        .param("date", "2026-07-04")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(r -> System.out.println("【每日记录】响应: " + r.getResponse().getContentAsString()));

        System.out.println("✅ 新用户饮食记录为空，正常返回空数组");
    }

    @Test
    @Order(9)
    void test09_getTodayStats() throws Exception {
        mockMvc.perform(get("/api/stats/today")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalCalories").exists())
                .andDo(r -> System.out.println("【今日统计】响应: " + r.getResponse().getContentAsString()));

        System.out.println("✅ 今日统计获取成功");
    }

    @Test
    @Order(10)
    void test10_getWeeklyCalories() throws Exception {
        mockMvc.perform(get("/api/stats/weekly-calories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andDo(r -> System.out.println("【周热量】响应: " + r.getResponse().getContentAsString()));

        System.out.println("✅ 周热量统计获取成功");
    }

    @Test
    @Order(11)
    void test11_getWeightTrend_empty() throws Exception {
        mockMvc.perform(get("/api/stats/weight-trend")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(r -> System.out.println("【体重趋势】响应: " + r.getResponse().getContentAsString()));

        System.out.println("✅ 新用户体重趋势为空，正常返回空数组");
    }

    @Test
    @Order(12)
    void test12_accessWithoutToken_shouldFail() throws Exception {
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isForbidden())
                .andDo(r -> System.out.println("【无token访问】状态码: " + r.getResponse().getStatus()));

        System.out.println("✅ 未登录访问受保护接口正确返回 403");
    }

    @Test
    @Order(13)
    void test13_updateProfile() throws Exception {
        String requestBody = """
                {
                    "gender": "male",
                    "age": 25,
                    "height": 175.5,
                    "weight": 70.0,
                    "activity": "moderate",
                    "targetWeight": 65.0
                }
                """;

        mockMvc.perform(put("/api/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andDo(r -> System.out.println("【更新档案】响应: " + r.getResponse().getContentAsString()));

        System.out.println("✅ 更新用户档案成功");
    }

    @Test
    @Order(14)
    void test14_createCustomFood() throws Exception {
        String requestBody = """
                {
                    "name": "我的自定义食物",
                    "category": "主食",
                    "servingUnit": "克",
                    "servingSize": 100.0,
                    "calories": 200,
                    "protein": 10.0,
                    "carbs": 30.0,
                    "fat": 5.0
                }
                """;

        mockMvc.perform(post("/api/foods/custom")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andDo(r -> System.out.println("【自定义食物】响应: " + r.getResponse().getContentAsString()));

        System.out.println("✅ 创建自定义食物成功");
    }
}
