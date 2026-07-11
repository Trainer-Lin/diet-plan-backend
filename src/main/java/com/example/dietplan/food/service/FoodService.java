package com.example.dietplan.food.service;

import com.example.dietplan.food.dto.CustomFoodCreateRequest;
import com.example.dietplan.food.dto.FoodResponse;
import java.util.List;

public interface FoodService {
    List<FoodResponse> listFoods(Long userId);

    List<FoodResponse> searchFoods(Long userId, String keyword);

    FoodResponse createCustomFood(Long userId, CustomFoodCreateRequest request);
}
