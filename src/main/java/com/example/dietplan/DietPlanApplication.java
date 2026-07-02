package com.example.dietplan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.example.dietplan")
@SpringBootApplication
public class DietPlanApplication {

    public static void main(String[] args) {
        SpringApplication.run(DietPlanApplication.class, args);
    }
}
