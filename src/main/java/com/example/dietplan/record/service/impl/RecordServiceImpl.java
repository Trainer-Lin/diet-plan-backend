package com.example.dietplan.record.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.dietplan.record.dto.DailyDietRecordResponse;
import com.example.dietplan.record.dto.DietRecordCreateRequest;
import com.example.dietplan.record.dto.DietRecordItemResponse;
import com.example.dietplan.record.entity.DietRecord;
import com.example.dietplan.record.entity.DietRecordItem;
import com.example.dietplan.record.mapper.DietRecordItemMapper;
import com.example.dietplan.record.mapper.DietRecordMapper;
import java.util.ArrayList;
import com.example.dietplan.record.service.RecordService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final DietRecordMapper dietRecordMapper;
    private final DietRecordItemMapper dietRecordItemMapper;

    @Override
    public List<DailyDietRecordResponse> getDailyRecords(Long userId, java.time.LocalDate date) {
        return dietRecordMapper.selectList(new LambdaQueryWrapper<DietRecord>()
                        .eq(DietRecord::getUserId, userId)
                        .eq(DietRecord::getRecordDate, date)
                        .orderByAsc(DietRecord::getCreatedAt))
                .stream()
                .map(record -> DailyDietRecordResponse.builder()
                        .id(record.getId())
                        .meal(record.getMealType())
                        .time("")
                        .totalCalories(record.getTotalCalories())
                        .note(record.getNote())
                        .foods(loadItems(record.getId()))
                        .build())
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRecord(Long userId, DietRecordCreateRequest request) {
        DietRecord record = new DietRecord();
        record.setUserId(userId);
        record.setRecordDate(request.getRecordDate());
        record.setMealType(request.getMeal());
        record.setNote(request.getNote());
        record.setTotalCalories(request.getFoods().stream().mapToInt(item -> item.getCalories()).sum());
        record.setCreatedAt(LocalDateTime.now());
        dietRecordMapper.insert(record);

        request.getFoods().forEach(item -> {
            DietRecordItem entity = new DietRecordItem();
            entity.setRecordId(record.getId());
            entity.setFoodId(item.getFoodId());
            entity.setFoodNameSnapshot(item.getName());
            entity.setAmount(item.getAmount());
            entity.setCalories(item.getCalories());
            entity.setProtein(item.getProtein());
            entity.setCarbs(item.getCarbs());
            entity.setFat(item.getFat());
            dietRecordItemMapper.insert(entity);
        });
    }

    @Override
    public void deleteRecordItem(Long userId, Long itemId) {
        DietRecordItem item = dietRecordItemMapper.selectById(itemId);
        if (item == null) {
            return;
        }

        DietRecord record = dietRecordMapper.selectById(item.getRecordId());
        if (record == null || !record.getUserId().equals(userId)) {
            return;
        }

        Long recordId = item.getRecordId();
        dietRecordItemMapper.deleteById(itemId);
        refreshRecordCalories(recordId);
    }

    private List<DietRecordItemResponse> loadItems(Long recordId) {
        return dietRecordItemMapper.selectList(new LambdaQueryWrapper<DietRecordItem>()
                        .eq(DietRecordItem::getRecordId, recordId))
                .stream()
                .map(item -> DietRecordItemResponse.builder()
                        .id(item.getId())
                        .name(item.getFoodNameSnapshot())
                        .amount(item.getAmount())
                        .calories(item.getCalories())
                        .build())
                .toList();
    }

    private void refreshRecordCalories(Long recordId) {
        DietRecord record = dietRecordMapper.selectById(recordId);
        if (record == null) {
            return;
        }

        List<DietRecordItem> items = new ArrayList<>(dietRecordItemMapper.selectList(
                new LambdaQueryWrapper<DietRecordItem>().eq(DietRecordItem::getRecordId, recordId)
        ));

        if (items.isEmpty()) {
            dietRecordMapper.deleteById(recordId);
            return;
        }

        record.setTotalCalories(items.stream().mapToInt(DietRecordItem::getCalories).sum());
        dietRecordMapper.updateById(record);
    }
}
