package com.example.dietplan.admin.service;

import com.example.dietplan.admin.dto.AdminFoodRequest;
import com.example.dietplan.admin.dto.AdminUserDietRecordResponse;
import com.example.dietplan.admin.dto.AdminUserListResponse;
import com.example.dietplan.admin.dto.AdminUserProfileResponse;
import com.example.dietplan.admin.dto.AdminUserUpdateRequest;
import com.example.dietplan.admin.dto.AdminUserWeightRecordResponse;
import com.example.dietplan.admin.dto.CustomFoodListResponse;
import com.example.dietplan.admin.dto.FoodReviewActionRequest;
import com.example.dietplan.admin.dto.FoodReviewTicketResponse;
import com.example.dietplan.admin.dto.SystemStatsResponse;
import com.example.dietplan.food.dto.FoodResponse;
import com.example.dietplan.user.dto.UserProfileRequest;
import java.util.List;

public interface AdminService {

    List<AdminUserListResponse> listUsers();

    AdminUserListResponse updateUser(Long userId, AdminUserUpdateRequest request);

    void deleteUser(Long userId);

    AdminUserProfileResponse getUserProfile(Long userId);

    AdminUserProfileResponse updateUserProfile(Long userId, UserProfileRequest request);

    List<AdminUserDietRecordResponse> getUserDietRecords(Long userId);

    List<AdminUserWeightRecordResponse> getUserWeightRecords(Long userId);

    List<FoodResponse> listOfficialFoods();

    FoodResponse createFood(AdminFoodRequest request);

    FoodResponse updateFood(Long foodId, AdminFoodRequest request);

    void deleteFood(Long foodId);

    List<CustomFoodListResponse> listCustomFoods();

    SystemStatsResponse getSystemStats();

    List<FoodReviewTicketResponse> listFoodReviewTickets();

    FoodReviewTicketResponse approveFoodReview(Long ticketId);

    FoodReviewTicketResponse rejectFoodReview(Long ticketId, FoodReviewActionRequest request);
}
