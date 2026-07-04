package com.example.dietplan.common.context;

import com.example.dietplan.common.exception.BusinessException;
import com.example.dietplan.common.result.ResultCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUserContext {

    private CurrentUserContext() {
    }

    public static Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录或登录已过期");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        if (principal instanceof String) {
            try {
                return Long.valueOf((String) principal);
            } catch (NumberFormatException e) {
                throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户信息无效");
            }
        }
        throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户信息无效");
    }

    public static Long getUserIdOrDefault() {
        try {
            return getUserId();
        } catch (Exception e) {
            return null;
        }
    }
}
