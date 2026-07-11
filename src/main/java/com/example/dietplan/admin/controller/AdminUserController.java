package com.example.dietplan.admin.controller;

import com.example.dietplan.admin.dto.AdminUserDietRecordResponse;
import com.example.dietplan.admin.dto.AdminUserListResponse;
import com.example.dietplan.admin.dto.AdminUserProfileResponse;
import com.example.dietplan.admin.dto.AdminUserUpdateRequest;
import com.example.dietplan.admin.dto.AdminUserWeightRecordResponse;
import com.example.dietplan.admin.service.AdminService;
import com.example.dietplan.common.result.ApiResponse;
import com.example.dietplan.user.dto.UserProfileRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminService adminService;

    @GetMapping
    public ApiResponse<List<AdminUserListResponse>> listUsers() {
        return ApiResponse.success(adminService.listUsers());
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminUserListResponse> updateUser(@PathVariable Long id,
                                                         @Valid @RequestBody AdminUserUpdateRequest request) {
        return ApiResponse.success("修改成功", adminService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/{id}/profile")
    public ApiResponse<AdminUserProfileResponse> getUserProfile(@PathVariable Long id) {
        return ApiResponse.success(adminService.getUserProfile(id));
    }

    @PutMapping("/{id}/profile")
    public ApiResponse<AdminUserProfileResponse> updateUserProfile(@PathVariable Long id,
                                                                   @Valid @RequestBody UserProfileRequest request) {
        return ApiResponse.success("修改成功", adminService.updateUserProfile(id, request));
    }

    @GetMapping("/{id}/diet-records")
    public ApiResponse<List<AdminUserDietRecordResponse>> getUserDietRecords(@PathVariable Long id) {
        return ApiResponse.success(adminService.getUserDietRecords(id));
    }

    @GetMapping("/{id}/weight-records")
    public ApiResponse<List<AdminUserWeightRecordResponse>> getUserWeightRecords(@PathVariable Long id) {
        return ApiResponse.success(adminService.getUserWeightRecords(id));
    }
}
