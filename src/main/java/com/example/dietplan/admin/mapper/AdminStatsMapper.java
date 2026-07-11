package com.example.dietplan.admin.mapper;

import com.example.dietplan.admin.dto.SystemStatsResponse;
import java.util.List;
import org.apache.ibatis.annotations.Select;

public interface AdminStatsMapper {

    @Select("SELECT COUNT(*) FROM sys_user")
    Long countUsers();

    @Select("SELECT COUNT(*) FROM sys_user WHERE role = 'ADMIN'")
    Long countAdmins();

    @Select("SELECT COUNT(*) FROM food")
    Long countFoods();

    @Select("SELECT COUNT(*) FROM diet_record")
    Long countRecords();

    @Select("SELECT COUNT(*) FROM weight_record")
    Long countWeightRecords();

    @Select("SELECT r.user_id AS userId, u.username AS username, u.nickname AS nickname, " +
            "COUNT(*) AS recordCount FROM diet_record r " +
            "LEFT JOIN sys_user u ON r.user_id = u.id " +
            "GROUP BY r.user_id, u.username, u.nickname " +
            "ORDER BY recordCount DESC LIMIT 10")
    List<SystemStatsResponse.ActiveUserItem> selectTopActiveUsers();
}
