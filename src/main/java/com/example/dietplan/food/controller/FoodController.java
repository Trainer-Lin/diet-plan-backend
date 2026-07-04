package com.example.dietplan.food.controller;

import com.example.dietplan.common.context.CurrentUserContext;
import com.example.dietplan.common.result.ApiResponse;
import com.example.dietplan.food.dto.CustomFoodCreateRequest;
import com.example.dietplan.food.dto.FoodResponse;
import com.example.dietplan.food.service.FoodService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @GetMapping
    public ApiResponse<List<FoodResponse>> listFoods() {
        return ApiResponse.success(foodService.listFoods());
    }

    @GetMapping("/search")
    public ApiResponse<List<FoodResponse>> searchFoods(@RequestParam String keyword) {
        return ApiResponse.success(foodService.searchFoods(keyword));
    }

    @PostMapping("/custom")
    public ApiResponse<Void> createCustomFood(@Valid @RequestBody CustomFoodCreateRequest request) {
        foodService.createCustomFood(CurrentUserContext.getUserId(), request);
        return ApiResponse.success("新增成功", null);
    }
}
