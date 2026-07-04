package com.example.dietplan.record.controller;

import com.example.dietplan.common.context.CurrentUserContext;
import com.example.dietplan.common.result.ApiResponse;
import com.example.dietplan.record.dto.DailyDietRecordResponse;
import com.example.dietplan.record.dto.DietRecordCreateRequest;
import com.example.dietplan.record.service.RecordService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @GetMapping("/daily")
    public ApiResponse<List<DailyDietRecordResponse>> getDailyRecords(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.success(recordService.getDailyRecords(CurrentUserContext.getUserId(), date));
    }

    @PostMapping
    public ApiResponse<Void> createRecord(@Valid @RequestBody DietRecordCreateRequest request) {
        recordService.createRecord(CurrentUserContext.getUserId(), request);
        return ApiResponse.success("记录成功", null);
    }

    @DeleteMapping("/item/{id}")
    public ApiResponse<Void> deleteRecordItem(@PathVariable Long id) {
        recordService.deleteRecordItem(id);
        return ApiResponse.success("删除成功", null);
    }
}
