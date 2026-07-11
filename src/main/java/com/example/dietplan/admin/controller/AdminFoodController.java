package com.example.dietplan.admin.controller;

import com.example.dietplan.admin.dto.AdminFoodRequest;
import com.example.dietplan.admin.dto.CustomFoodListResponse;
import com.example.dietplan.admin.service.AdminService;
import com.example.dietplan.common.result.ApiResponse;
import com.example.dietplan.food.dto.FoodResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/foods")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminFoodController {

    private final AdminService adminService;

    @GetMapping
    public ApiResponse<List<FoodResponse>> listOfficialFoods() {
        return ApiResponse.success(adminService.listOfficialFoods());
    }

    @GetMapping("/custom")
    public ApiResponse<List<CustomFoodListResponse>> listCustomFoods() {
        return ApiResponse.success(adminService.listCustomFoods());
    }

    @PostMapping
    public ApiResponse<FoodResponse> createFood(@Valid @RequestBody AdminFoodRequest request) {
        return ApiResponse.success("新增成功", adminService.createFood(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<FoodResponse> updateFood(@PathVariable Long id,
                                                @Valid @RequestBody AdminFoodRequest request) {
        return ApiResponse.success("修改成功", adminService.updateFood(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFood(@PathVariable Long id) {
        adminService.deleteFood(id);
        return ApiResponse.success("删除成功", null);
    }
}
