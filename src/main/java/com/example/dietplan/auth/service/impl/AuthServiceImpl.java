package com.example.dietplan.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.dietplan.auth.dto.AuthTokenResponse;
import com.example.dietplan.auth.dto.CurrentUserResponse;
import com.example.dietplan.auth.dto.LoginRequest;
import com.example.dietplan.auth.dto.RegisterRequest;
import com.example.dietplan.auth.service.AuthService;
import com.example.dietplan.common.context.CurrentUserContext;
import com.example.dietplan.common.exception.BusinessException;
import com.example.dietplan.common.result.ResultCode;
import com.example.dietplan.common.utils.JwtTokenUtil;
import com.example.dietplan.user.entity.SysUser;
import com.example.dietplan.user.mapper.SysUserMapper;
import com.example.dietplan.user.service.ProfileService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public void register(RegisterRequest request) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername());
        if (sysUserMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.insert(user);

        profileService.initEmptyProfile(user.getId());
    }

    @Override
    public AuthTokenResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername()));

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }

        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getRole() != null ? user.getRole() : "USER");
        return new AuthTokenResponse(token, "Bearer");
    }

    @Override
    public CurrentUserResponse getCurrentUser() {
        Long userId = CurrentUserContext.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        return CurrentUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole() != null ? user.getRole() : "USER")
                .build();
    }
}
