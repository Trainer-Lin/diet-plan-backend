package com.example.dietplan.admin.controller;

import com.example.dietplan.admin.dto.FoodReviewActionRequest;
import com.example.dietplan.admin.dto.FoodReviewTicketResponse;
import com.example.dietplan.admin.service.AdminService;
import com.example.dietplan.common.result.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/food-reviews")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminFoodReviewController {

    private final AdminService adminService;

    @GetMapping
    public ApiResponse<List<FoodReviewTicketResponse>> listTickets() {
        return ApiResponse.success(adminService.listFoodReviewTickets());
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<FoodReviewTicketResponse> approve(@PathVariable Long id) {
        return ApiResponse.success("审核通过", adminService.approveFoodReview(id));
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<FoodReviewTicketResponse> reject(@PathVariable Long id,
                                                        @Valid @RequestBody FoodReviewActionRequest request) {
        return ApiResponse.success("已拒绝", adminService.rejectFoodReview(id, request));
    }
}
