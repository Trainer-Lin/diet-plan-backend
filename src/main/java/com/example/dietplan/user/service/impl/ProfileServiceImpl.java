package com.example.dietplan.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.dietplan.common.result.ResultCode;
import com.example.dietplan.common.exception.BusinessException;
import com.example.dietplan.user.dto.UserProfileRequest;
import com.example.dietplan.user.dto.UserProfileResponse;
import com.example.dietplan.user.entity.UserProfile;
import com.example.dietplan.user.mapper.UserProfileMapper;
import com.example.dietplan.user.service.ProfileService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfileResponse getProfile(Long userId) {
        UserProfile profile = getByUserId(userId);
        int tdee = calculateTdee(profile);

        return UserProfileResponse.builder()
                .gender(profile.getGender())
                .age(profile.getAge())
                .height(profile.getHeight())
                .weight(profile.getWeight())
                .activity(profile.getActivityLevel())
                .tdee(tdee)
                .targetWeight(profile.getTargetWeight())
                .targetCalories(profile.getTargetCalories())
                .build();
    }

    @Override
    public void updateProfile(Long userId, UserProfileRequest request) {
        UserProfile profile = getByUserId(userId);
        profile.setGender(request.getGender());
        profile.setAge(request.getAge());
        profile.setHeight(request.getHeight());
        profile.setWeight(request.getWeight());
        profile.setActivityLevel(request.getActivity());
        profile.setTargetWeight(request.getTargetWeight());
        profile.setTargetCalories(calculateTdee(profile));
        profile.setUpdatedAt(LocalDateTime.now());
        userProfileMapper.updateById(profile);
    }

    @Override
    public void initEmptyProfile(Long userId) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setGender("male");
        profile.setAge(18);
        profile.setHeight(BigDecimal.ZERO);
        profile.setWeight(BigDecimal.ZERO);
        profile.setActivityLevel("moderate");
        profile.setTargetWeight(BigDecimal.ZERO);
        profile.setTargetCalories(0);
        profile.setUpdatedAt(LocalDateTime.now());
        userProfileMapper.insert(profile);
    }

    private UserProfile getByUserId(Long userId) {
        UserProfile profile = userProfileMapper.selectOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId));

        if (profile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户档案不存在");
        }
        return profile;
    }

    private int calculateTdee(UserProfile profile) {
        if (profile.getHeight() == null || profile.getWeight() == null || profile.getAge() == null) {
            return 0;
        }

        BigDecimal weightPart = profile.getWeight().multiply(BigDecimal.TEN);
        BigDecimal heightPart = profile.getHeight().multiply(new BigDecimal("6.25"));
        BigDecimal agePart = BigDecimal.valueOf(profile.getAge()).multiply(new BigDecimal("5"));
        BigDecimal base = weightPart.add(heightPart).subtract(agePart);
        base = "male".equalsIgnoreCase(profile.getGender()) ? base.add(new BigDecimal("5")) : base.subtract(new BigDecimal("161"));

        BigDecimal multiplier = switch (profile.getActivityLevel()) {
            case "sedentary" -> new BigDecimal("1.2");
            case "light" -> new BigDecimal("1.375");
            case "active" -> new BigDecimal("1.725");
            case "very_active" -> new BigDecimal("1.9");
            default -> new BigDecimal("1.55");
        };

        return base.multiply(multiplier).setScale(0, RoundingMode.HALF_UP).intValue();
    }
}
