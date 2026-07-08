package com.example.dietplan.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * AI 模块的配置类
 * 负责创建用于调用 Kimi API 的 WebClient 客户端
 */
@Configuration // 标记这是一个配置类，Spring 启动时会执行
public class AiConfig {

    /**
     * 创建一个配置好的 WebClient Bean
     * WebClient 是 Spring 提供的 HTTP 客户端，用于发送网络请求
     *
     * @param baseUrl 从 application.yml 读取的 API 地址
     * @param apiKey  从 application.yml 读取的密钥
     * @return 配置好的 WebClient 实例
     */
    @Bean // @Bean 表示这个方法返回的对象会被 Spring 容器管理，可以注入到其他类中
    public WebClient moonshotWebClient(
            @Value("${moonshot.base-url}") String baseUrl, // @Value 从配置文件读取值
            @Value("${moonshot.api-key}") String apiKey) {

        return WebClient.builder()
                .baseUrl(baseUrl) // 设置基础 URL，后续请求只需要写路径
                // 添加 Authorization 请求头，Kimi API 用 Bearer 认证
                .defaultHeader("Authorization", "Bearer " + apiKey)
                // 设置 Content-Type 为 JSON
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
