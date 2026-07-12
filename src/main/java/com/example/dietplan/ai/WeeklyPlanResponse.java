package com.example.dietplan.ai;

import lombok.Data;
import java.util.List;

@Data
public class WeeklyPlanResponse {
    private String summary;
    private List<DayPlan> days;

    @Data
    public static class DayPlan {
        private String dayOfWeek;
        private String date;
        private DietPlan dietPlan;
        private FitnessPlan fitnessPlan;
    }

    @Data
    public static class DietPlan {
        private Integer totalCalories;
        private Meal breakfast;
        private Meal lunch;
        private Meal dinner;
        private List<Meal> snacks;
    }

    @Data
    public static class FitnessPlan {
        private Integer estimatedCaloriesBurned;
        private String warmUp;
        private String mainWorkout;
        private String coolDown;
        private String notes;
    }

    @Data
    public static class Meal {
        private String name;
        private String description;
        private Integer calories;
    }
}