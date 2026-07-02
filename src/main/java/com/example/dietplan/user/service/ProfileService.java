package com.example.dietplan.user.service;

import com.example.dietplan.user.dto.UserProfileRequest;
import com.example.dietplan.user.dto.UserProfileResponse;

public interface ProfileService {
    UserProfileResponse getProfile(Long userId);

    void updateProfile(Long userId, UserProfileRequest request);

    void initEmptyProfile(Long userId);
}
