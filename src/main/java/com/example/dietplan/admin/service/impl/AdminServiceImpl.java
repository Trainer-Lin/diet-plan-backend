package com.example.dietplan.admin.service.impl;

import com.example.dietplan.admin.dto.AdminFoodRequest;
import com.example.dietplan.admin.dto.AdminUserDietRecordResponse;
import com.example.dietplan.admin.dto.AdminUserListResponse;
import com.example.dietplan.admin.dto.AdminUserProfileResponse;
import com.example.dietplan.admin.dto.AdminUserUpdateRequest;
import com.example.dietplan.admin.dto.AdminUserWeightRecordResponse;
import com.example.dietplan.admin.dto.CustomFoodListResponse;
import com.example.dietplan.admin.dto.FoodReviewActionRequest;
import com.example.dietplan.admin.dto.FoodReviewTicketResponse;
import com.example.dietplan.admin.dto.SystemStatsResponse;
import com.example.dietplan.admin.mapper.AdminStatsMapper;
import com.example.dietplan.admin.service.AdminService;
import com.example.dietplan.common.context.CurrentUserContext;
import com.example.dietplan.common.exception.BusinessException;
import com.example.dietplan.common.result.ResultCode;
import com.example.dietplan.food.dto.FoodResponse;
import com.example.dietplan.food.entity.Food;
import com.example.dietplan.food.entity.FoodReviewTicket;
import com.example.dietplan.food.enums.FoodReviewStatus;
import com.example.dietplan.food.mapper.FoodMapper;
import com.example.dietplan.food.mapper.FoodReviewTicketMapper;
import com.example.dietplan.record.entity.DietRecord;
import com.example.dietplan.record.entity.DietRecordItem;
import com.example.dietplan.record.entity.WeightRecord;
import com.example.dietplan.record.mapper.DietRecordItemMapper;
import com.example.dietplan.record.mapper.DietRecordMapper;
import com.example.dietplan.record.mapper.WeightRecordMapper;
import com.example.dietplan.user.dto.UserProfileRequest;
import com.example.dietplan.user.entity.SysUser;
import com.example.dietplan.user.entity.UserProfile;
import com.example.dietplan.user.mapper.SysUserMapper;
import com.example.dietplan.user.mapper.UserProfileMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final SysUserMapper sysUserMapper;
    private final FoodMapper foodMapper;
    private final FoodReviewTicketMapper foodReviewTicketMapper;
    private final AdminStatsMapper adminStatsMapper;
    private final UserProfileMapper userProfileMapper;
    private final DietRecordMapper dietRecordMapper;
    private final DietRecordItemMapper dietRecordItemMapper;
    private final WeightRecordMapper weightRecordMapper;

    @Override
    public List<AdminUserListResponse> listUsers() {
        return sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getRole, "USER"))
                .stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Override
    public AdminUserListResponse updateUser(Long userId, AdminUserUpdateRequest request) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }

        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getRole())) {
            if (!"USER".equals(request.getRole()) && !"ADMIN".equals(request.getRole())) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "角色只能是 USER 或 ADMIN");
            }
            user.setRole(request.getRole());
        }
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);

        return toUserResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "不允许删除管理员账号");
        }

        LambdaQueryWrapper<DietRecord> recordWrapper = new LambdaQueryWrapper<DietRecord>()
                .eq(DietRecord::getUserId, userId);
        List<DietRecord> records = dietRecordMapper.selectList(recordWrapper);
        if (!records.isEmpty()) {
            List<Long> recordIds = records.stream().map(DietRecord::getId).collect(Collectors.toList());
            dietRecordItemMapper.delete(new LambdaQueryWrapper<DietRecordItem>()
                    .in(DietRecordItem::getRecordId, recordIds));
            dietRecordMapper.delete(recordWrapper);
        }

        weightRecordMapper.delete(new LambdaQueryWrapper<WeightRecord>()
                .eq(WeightRecord::getUserId, userId));

        userProfileMapper.delete(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId));

        foodMapper.delete(new LambdaQueryWrapper<Food>()
                .eq(Food::getCreatedBy, userId)
                .eq(Food::getIsCustom, true));

        sysUserMapper.deleteById(userId);
    }

    @Override
    public AdminUserProfileResponse getUserProfile(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        UserProfile profile = userProfileMapper.selectOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId));
        if (profile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户档案不存在");
        }
        int tdee = calculateTdee(profile);
        return AdminUserProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
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
    public AdminUserProfileResponse updateUserProfile(Long userId, UserProfileRequest request) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        UserProfile profile = userProfileMapper.selectOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId));
        if (profile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户档案不存在");
        }
        profile.setGender(request.getGender());
        profile.setAge(request.getAge());
        profile.setHeight(request.getHeight());
        profile.setWeight(request.getWeight());
        profile.setActivityLevel(request.getActivity());
        profile.setTargetWeight(request.getTargetWeight());
        profile.setTargetCalories(calculateTdee(profile));
        profile.setUpdatedAt(LocalDateTime.now());
        userProfileMapper.updateById(profile);
        return getUserProfile(userId);
    }

    @Override
    public List<AdminUserDietRecordResponse> getUserDietRecords(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        List<DietRecord> records = dietRecordMapper.selectList(new LambdaQueryWrapper<DietRecord>()
                .eq(DietRecord::getUserId, userId)
                .orderByDesc(DietRecord::getRecordDate)
                .orderByDesc(DietRecord::getCreatedAt));
        if (records.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> recordIds = records.stream().map(DietRecord::getId).collect(Collectors.toSet());
        Map<Long, List<DietRecordItem>> itemMap = dietRecordItemMapper.selectList(new LambdaQueryWrapper<DietRecordItem>()
                .in(DietRecordItem::getRecordId, recordIds)).stream()
                .collect(Collectors.groupingBy(DietRecordItem::getRecordId));
        return records.stream().map(record -> {
            List<DietRecordItem> items = itemMap.getOrDefault(record.getId(), Collections.emptyList());
            List<AdminUserDietRecordResponse.DietRecordItem> responseItems = items.stream()
                    .map(item -> AdminUserDietRecordResponse.DietRecordItem.builder()
                            .id(item.getId())
                            .foodName(item.getFoodNameSnapshot())
                            .amount(item.getAmount())
                            .calories(item.getCalories())
                            .protein(item.getProtein())
                            .carbs(item.getCarbs())
                            .fat(item.getFat())
                            .build())
                    .toList();
            return AdminUserDietRecordResponse.builder()
                    .id(record.getId())
                    .recordDate(record.getRecordDate())
                    .mealType(record.getMealType())
                    .note(record.getNote())
                    .totalCalories(record.getTotalCalories())
                    .createdAt(record.getCreatedAt())
                    .items(responseItems)
                    .build();
        }).toList();
    }

    @Override
    public List<AdminUserWeightRecordResponse> getUserWeightRecords(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        UserProfile profile = userProfileMapper.selectOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId));
        List<WeightRecord> records = weightRecordMapper.selectList(new LambdaQueryWrapper<WeightRecord>()
                .eq(WeightRecord::getUserId, userId)
                .orderByAsc(WeightRecord::getRecordDate));
        return records.stream().map(record -> {
            BigDecimal bmi = BigDecimal.ZERO;
            if (profile != null && profile.getHeight() != null && profile.getHeight().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal heightInMeters = profile.getHeight().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                bmi = record.getWeight().divide(heightInMeters.multiply(heightInMeters), 2, RoundingMode.HALF_UP);
            }
            return AdminUserWeightRecordResponse.builder()
                    .id(record.getId())
                    .recordDate(record.getRecordDate())
                    .weight(record.getWeight())
                    .bmi(bmi)
                    .build();
        }).toList();
    }

    private int calculateTdee(UserProfile profile) {
        if (profile.getHeight() == null || profile.getWeight() == null || profile.getAge() == null) {
            return 0;
        }
        if (profile.getHeight().compareTo(BigDecimal.ZERO) <= 0
                || profile.getWeight().compareTo(BigDecimal.ZERO) <= 0
                || profile.getAge() <= 0) {
            return 0;
        }
        BigDecimal weightPart = profile.getWeight().multiply(BigDecimal.TEN);
        BigDecimal heightPart = profile.getHeight().multiply(new BigDecimal("6.25"));
        BigDecimal agePart = BigDecimal.valueOf(profile.getAge()).multiply(new BigDecimal("5"));
        BigDecimal base = weightPart.add(heightPart).subtract(agePart);
        base = "male".equalsIgnoreCase(profile.getGender()) ? base.add(new BigDecimal("5")) : base.subtract(new BigDecimal("161"));
        BigDecimal multiplier = switch (profile.getActivityLevel() == null ? "moderate" : profile.getActivityLevel()) {
            case "sedentary" -> new BigDecimal("1.2");
            case "light" -> new BigDecimal("1.375");
            case "active" -> new BigDecimal("1.725");
            case "very_active" -> new BigDecimal("1.9");
            default -> new BigDecimal("1.55");
        };
        int tdee = base.multiply(multiplier).setScale(0, RoundingMode.HALF_UP).intValue();
        return Math.max(tdee, 0);
    }

    @Override
    public FoodResponse createFood(AdminFoodRequest request) {
        Food food = new Food();
        applyFoodRequest(food, request);
        food.setIsCustom(false);
        food.setCreatedAt(LocalDateTime.now());
        foodMapper.insert(food);
        return toFoodResponse(food);
    }

    @Override
    public FoodResponse updateFood(Long foodId, AdminFoodRequest request) {
        Food food = foodMapper.selectById(foodId);
        if (food == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "食物不存在");
        }
        applyFoodRequest(food, request);
        foodMapper.updateById(food);
        return toFoodResponse(food);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFood(Long foodId) {
        Food food = foodMapper.selectById(foodId);
        if (food == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "食物不存在");
        }
        dietRecordItemMapper.update(null, new LambdaUpdateWrapper<DietRecordItem>()
                .set(DietRecordItem::getFoodId, null)
                .eq(DietRecordItem::getFoodId, foodId));
        foodReviewTicketMapper.delete(new LambdaQueryWrapper<FoodReviewTicket>()
                .eq(FoodReviewTicket::getFoodId, foodId));
        foodMapper.deleteById(foodId);
    }

    @Override
    public List<CustomFoodListResponse> listCustomFoods() {
        // 查询所有用户自定义食物
        LambdaQueryWrapper<Food> wrapper =
                new LambdaQueryWrapper<>();
        wrapper.eq(Food::getIsCustom, true).orderByDesc(Food::getCreatedAt);
        List<Food> customFoods = foodMapper.selectList(wrapper);

        if (customFoods.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询创建者信息
        Set<Long> creatorIds = customFoods.stream()
                .map(Food::getCreatedBy)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, SysUser> userMap = creatorIds.isEmpty()
                ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(creatorIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, u -> u));

        return customFoods.stream()
                .map(food -> {
                    SysUser creator = food.getCreatedBy() != null ? userMap.get(food.getCreatedBy()) : null;
                    return CustomFoodListResponse.builder()
                            .id(food.getId())
                            .name(food.getName())
                            .category(food.getCategory())
                            .serving(food.getServingSize() + " " + food.getServingUnit())
                            .calories(food.getCalories())
                            .protein(food.getProtein())
                            .carbs(food.getCarbs())
                            .fat(food.getFat())
                            .createdBy(food.getCreatedBy())
                            .creatorUsername(creator != null ? creator.getUsername() : "未知用户")
                            .creatorNickname(creator != null ? creator.getNickname() : "-")
                            .createdAt(food.getCreatedAt())
                            .build();
                })
                .toList();
    }

    @Override
    public List<FoodResponse> listOfficialFoods() {
        return foodMapper.selectList(new LambdaQueryWrapper<Food>()
                        .eq(Food::getIsCustom, false)
                        .orderByDesc(Food::getCreatedAt))
                .stream()
                .map(this::toFoodResponse)
                .toList();
    }

    @Override
    public SystemStatsResponse getSystemStats() {
        return SystemStatsResponse.builder()
                .totalUsers(adminStatsMapper.countUsers())
                .adminCount(adminStatsMapper.countAdmins())
                .totalFoods(adminStatsMapper.countFoods())
                .totalRecords(adminStatsMapper.countRecords())
                .totalWeightRecords(adminStatsMapper.countWeightRecords())
                .topActiveUsers(adminStatsMapper.selectTopActiveUsers())
                .build();
    }

    @Override
    public List<FoodReviewTicketResponse> listFoodReviewTickets() {
        List<FoodReviewTicket> tickets = foodReviewTicketMapper.selectList(
                new LambdaQueryWrapper<FoodReviewTicket>()
                        .orderByDesc(FoodReviewTicket::getCreatedAt));
        if (tickets.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> foodIds = tickets.stream()
                .map(FoodReviewTicket::getFoodId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> submitterIds = tickets.stream()
                .map(FoodReviewTicket::getSubmitterId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> reviewerIds = tickets.stream()
                .map(FoodReviewTicket::getReviewerId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Food> foodMap = foodIds.isEmpty()
                ? Collections.emptyMap()
                : foodMapper.selectBatchIds(foodIds).stream()
                        .collect(Collectors.toMap(Food::getId, f -> f));
        Set<Long> allUserIds = new java.util.HashSet<>();
        allUserIds.addAll(submitterIds);
        allUserIds.addAll(reviewerIds);
        Map<Long, SysUser> userMap = allUserIds.isEmpty()
                ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(allUserIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, u -> u));

        return tickets.stream()
                .map(ticket -> {
                    Food food = foodMap.get(ticket.getFoodId());
                    SysUser submitter = ticket.getSubmitterId() != null ? userMap.get(ticket.getSubmitterId()) : null;
                    SysUser reviewer = ticket.getReviewerId() != null ? userMap.get(ticket.getReviewerId()) : null;
                    return FoodReviewTicketResponse.builder()
                            .id(ticket.getId())
                            .foodId(ticket.getFoodId())
                            .name(food != null ? food.getName() : "-")
                            .category(food != null ? food.getCategory() : "-")
                            .serving(food != null ? food.getServingSize() + " " + food.getServingUnit() : "-")
                            .calories(food != null ? food.getCalories() : null)
                            .protein(food != null ? food.getProtein() : null)
                            .carbs(food != null ? food.getCarbs() : null)
                            .fat(food != null ? food.getFat() : null)
                            .submitterId(ticket.getSubmitterId())
                            .submitterUsername(submitter != null ? submitter.getUsername() : "未知用户")
                            .submitterNickname(submitter != null ? submitter.getNickname() : "-")
                            .status(ticket.getStatus() != null ? ticket.getStatus().name() : null)
                            .reviewerId(ticket.getReviewerId())
                            .reviewerUsername(reviewer != null ? reviewer.getUsername() : null)
                            .remark(ticket.getRemark())
                            .reviewedAt(ticket.getReviewedAt())
                            .createdAt(ticket.getCreatedAt())
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FoodReviewTicketResponse approveFoodReview(Long ticketId) {
        FoodReviewTicket ticket = foodReviewTicketMapper.selectById(ticketId);
        if (ticket == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "审核工单不存在");
        }
        if (ticket.getStatus() != FoodReviewStatus.PENDING) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "工单不是待审核状态");
        }

        Food originalFood = foodMapper.selectById(ticket.getFoodId());
        if (originalFood == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "关联食物不存在");
        }

        Food officialFood = new Food();
        officialFood.setName(originalFood.getName());
        officialFood.setCategory(originalFood.getCategory());
        officialFood.setServingSize(originalFood.getServingSize());
        officialFood.setServingUnit(originalFood.getServingUnit());
        officialFood.setCalories(originalFood.getCalories());
        officialFood.setProtein(originalFood.getProtein());
        officialFood.setCarbs(originalFood.getCarbs());
        officialFood.setFat(originalFood.getFat());
        officialFood.setIsCustom(false);
        officialFood.setCreatedBy(null);
        officialFood.setCreatedAt(LocalDateTime.now());
        foodMapper.insert(officialFood);

        ticket.setStatus(FoodReviewStatus.APPROVED);
        ticket.setReviewerId(CurrentUserContext.getUserId());
        ticket.setReviewedAt(LocalDateTime.now());
        foodReviewTicketMapper.updateById(ticket);

        return toFoodReviewTicketResponse(ticket, officialFood);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FoodReviewTicketResponse rejectFoodReview(Long ticketId, FoodReviewActionRequest request) {
        FoodReviewTicket ticket = foodReviewTicketMapper.selectById(ticketId);
        if (ticket == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "审核工单不存在");
        }
        if (ticket.getStatus() != FoodReviewStatus.PENDING) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "工单不是待审核状态");
        }

        ticket.setStatus(FoodReviewStatus.REJECTED);
        ticket.setReviewerId(CurrentUserContext.getUserId());
        ticket.setReviewedAt(LocalDateTime.now());
        ticket.setRemark(request != null ? request.getRemark() : null);
        foodReviewTicketMapper.updateById(ticket);

        Food food = foodMapper.selectById(ticket.getFoodId());
        return toFoodReviewTicketResponse(ticket, food);
    }

    private FoodReviewTicketResponse toFoodReviewTicketResponse(FoodReviewTicket ticket, Food food) {
        SysUser submitter = ticket.getSubmitterId() != null
                ? sysUserMapper.selectById(ticket.getSubmitterId())
                : null;
        SysUser reviewer = ticket.getReviewerId() != null
                ? sysUserMapper.selectById(ticket.getReviewerId())
                : null;
        return FoodReviewTicketResponse.builder()
                .id(ticket.getId())
                .foodId(ticket.getFoodId())
                .name(food != null ? food.getName() : "-")
                .category(food != null ? food.getCategory() : "-")
                .serving(food != null ? food.getServingSize() + " " + food.getServingUnit() : "-")
                .calories(food != null ? food.getCalories() : null)
                .protein(food != null ? food.getProtein() : null)
                .carbs(food != null ? food.getCarbs() : null)
                .fat(food != null ? food.getFat() : null)
                .submitterId(ticket.getSubmitterId())
                .submitterUsername(submitter != null ? submitter.getUsername() : "未知用户")
                .submitterNickname(submitter != null ? submitter.getNickname() : "-")
                .status(ticket.getStatus() != null ? ticket.getStatus().name() : null)
                .reviewerId(ticket.getReviewerId())
                .reviewerUsername(reviewer != null ? reviewer.getUsername() : null)
                .remark(ticket.getRemark())
                .reviewedAt(ticket.getReviewedAt())
                .createdAt(ticket.getCreatedAt())
                .build();
    }

    private void applyFoodRequest(Food food, AdminFoodRequest request) {
        food.setName(request.getName());
        food.setCategory(request.getCategory());
        food.setServingUnit(request.getServingUnit());
        food.setServingSize(request.getServingSize());
        food.setCalories(request.getCalories());
        food.setProtein(request.getProtein());
        food.setCarbs(request.getCarbs());
        food.setFat(request.getFat());
    }

    private AdminUserListResponse toUserResponse(SysUser user) {
        return AdminUserListResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private FoodResponse toFoodResponse(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .category(food.getCategory())
                .serving(food.getServingSize() + " " + food.getServingUnit())
                .calories(food.getCalories())
                .protein(food.getProtein())
                .carbs(food.getCarbs())
                .fat(food.getFat())
                .tags(Collections.emptyList())
                .build();
    }
}
