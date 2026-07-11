package com.example.dietplan.admin.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserListResponse {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String role;
    private LocalDateTime createdAt;
}
