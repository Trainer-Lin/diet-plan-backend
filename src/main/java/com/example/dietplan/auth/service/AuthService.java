package com.example.dietplan.auth.service;

import com.example.dietplan.auth.dto.AuthTokenResponse;
import com.example.dietplan.auth.dto.CurrentUserResponse;
import com.example.dietplan.auth.dto.LoginRequest;
import com.example.dietplan.auth.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);

    AuthTokenResponse login(LoginRequest request);

    CurrentUserResponse getCurrentUser();
}
