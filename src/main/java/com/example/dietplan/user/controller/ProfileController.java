package com.example.dietplan.user.controller;

import com.example.dietplan.common.context.CurrentUserContext;
import com.example.dietplan.common.result.ApiResponse;
import com.example.dietplan.user.dto.UserProfileRequest;
import com.example.dietplan.user.dto.UserProfileResponse;
import com.example.dietplan.user.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ApiResponse<UserProfileResponse> getProfile() {
        return ApiResponse.success(profileService.getProfile(CurrentUserContext.getUserId()));
    }

    @PutMapping
    public ApiResponse<Void> updateProfile(@Valid @RequestBody UserProfileRequest request) {
        profileService.updateProfile(CurrentUserContext.getUserId(), request);
        return ApiResponse.success("更新成功", null);
    }
}
