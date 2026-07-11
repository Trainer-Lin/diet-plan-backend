package com.example.dietplan.record.service;

import com.example.dietplan.record.dto.DailyDietRecordResponse;
import com.example.dietplan.record.dto.DietRecordCreateRequest;
import java.time.LocalDate;
import java.util.List;

public interface RecordService {
    List<DailyDietRecordResponse> getDailyRecords(Long userId, LocalDate date);

    void createRecord(Long userId, DietRecordCreateRequest request);

    void deleteRecordItem(Long userId, Long itemId);
}
