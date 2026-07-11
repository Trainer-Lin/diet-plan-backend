package com.example.dietplan.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminUserUpdateRequest {
    private String nickname;
    private String email;
    @NotBlank(message = "角色不能为空")
    private String role;
}
