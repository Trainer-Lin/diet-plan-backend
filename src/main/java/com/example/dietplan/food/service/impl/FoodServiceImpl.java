package com.example.dietplan.food.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.dietplan.food.dto.CustomFoodCreateRequest;
import com.example.dietplan.food.dto.FoodResponse;
import com.example.dietplan.food.entity.Food;
import com.example.dietplan.food.mapper.FoodMapper;
import com.example.dietplan.food.service.FoodService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodMapper foodMapper;

    @Override
    public List<FoodResponse> listFoods() {
        return foodMapper.selectList(new LambdaQueryWrapper<Food>()
                        .orderByDesc(Food::getCreatedAt))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<FoodResponse> searchFoods(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return listFoods();
        }

        return foodMapper.selectList(new LambdaQueryWrapper<Food>()
                        .like(Food::getName, keyword)
                        .or()
                        .like(Food::getCategory, keyword)
                        .orderByDesc(Food::getCreatedAt))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public FoodResponse createCustomFood(Long userId, CustomFoodCreateRequest request) {
        Food food = new Food();
        food.setName(request.getName());
        food.setCategory(request.getCategory());
        food.setServingUnit(request.getServingUnit());
        food.setServingSize(request.getServingSize());
        food.setCalories(request.getCalories());
        food.setProtein(request.getProtein());
        food.setCarbs(request.getCarbs());
        food.setFat(request.getFat());
        food.setIsCustom(Boolean.TRUE);
        food.setCreatedBy(userId);
        food.setCreatedAt(LocalDateTime.now());
        foodMapper.insert(food);

        return toResponse(food);
    }

    private FoodResponse toResponse(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .category(food.getCategory())
                .serving(food.getServingSize() + " " + food.getServingUnit())
                .calories(food.getCalories())
                .protein(food.getProtein())
                .carbs(food.getCarbs())
                .fat(food.getFat())
                .tags(Collections.emptyList())
                .build();
    }
}
