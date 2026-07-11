package com.example.dietplan.admin.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemStatsResponse {
    private Long totalUsers;
    private Long totalFoods;
    private Long totalRecords;
    private Long totalWeightRecords;
    private Long adminCount;
    private List<ActiveUserItem> topActiveUsers;

    @Data
    @Builder
    public static class ActiveUserItem {
        private Long userId;
        private String username;
        private String nickname;
        private Long recordCount;
    }
}
