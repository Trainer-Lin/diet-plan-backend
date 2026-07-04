package com.example.dietplan.common.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.dietplan.food.entity.Food;
import com.example.dietplan.food.mapper.FoodMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final FoodMapper foodMapper;

    @Override
    public void run(String... args) {
        initDefaultFoods();
    }

    private void initDefaultFoods() {
        Long count = foodMapper.selectCount(new LambdaQueryWrapper<Food>()
                .eq(Food::getIsCustom, false));
        if (count != null && count > 0) {
            log.info("默认食物数据已存在，跳过初始化");
            return;
        }

        log.info("开始初始化默认食物数据...");
        List<Food> defaultFoods = buildDefaultFoods();
        for (Food food : defaultFoods) {
            foodMapper.insert(food);
        }
        log.info("默认食物数据初始化完成，共 {} 条", defaultFoods.size());
    }

    private List<Food> buildDefaultFoods() {
        LocalDateTime now = LocalDateTime.now();
        return List.of(
                createFood("白米饭", "主食", "碗", new BigDecimal("150.00"), 174, new BigDecimal("4.00"), new BigDecimal("38.00"), new BigDecimal("0.50"), now),
                createFood("糙米饭", "主食", "碗", new BigDecimal("150.00"), 168, new BigDecimal("3.80"), new BigDecimal("36.00"), new BigDecimal("1.20"), now),
                createFood("面条（煮）", "主食", "碗", new BigDecimal("200.00"), 220, new BigDecimal("7.00"), new BigDecimal("45.00"), new BigDecimal("1.50"), now),
                createFood("馒头", "主食", "个", new BigDecimal("100.00"), 221, new BigDecimal("7.00"), new BigDecimal("47.00"), new BigDecimal("1.10"), now),
                createFood("全麦面包", "主食", "片", new BigDecimal("30.00"), 75, new BigDecimal("3.00"), new BigDecimal("14.00"), new BigDecimal("1.20"), now),
                createFood("红薯", "主食", "个", new BigDecimal("150.00"), 120, new BigDecimal("2.00"), new BigDecimal("28.00"), new BigDecimal("0.30"), now),
                createFood("燕麦片", "主食", "碗", new BigDecimal("50.00"), 190, new BigDecimal("7.00"), new BigDecimal("32.00"), new BigDecimal("3.50"), now),
                createFood("玉米", "主食", "根", new BigDecimal("200.00"), 112, new BigDecimal("4.00"), new BigDecimal("24.00"), new BigDecimal("1.20"), now),

                createFood("鸡胸肉", "肉蛋", "克", new BigDecimal("100.00"), 165, new BigDecimal("31.00"), BigDecimal.ZERO, new BigDecimal("3.60"), now),
                createFood("鸡腿（带皮）", "肉蛋", "克", new BigDecimal("100.00"), 184, new BigDecimal("24.00"), BigDecimal.ZERO, new BigDecimal("9.50"), now),
                createFood("牛肉（瘦）", "肉蛋", "克", new BigDecimal("100.00"), 125, new BigDecimal("22.00"), BigDecimal.ZERO, new BigDecimal("3.50"), now),
                createFood("猪肉（瘦）", "肉蛋", "克", new BigDecimal("100.00"), 143, new BigDecimal("20.30"), BigDecimal.ZERO, new BigDecimal("6.20"), now),
                createFood("鸡蛋", "肉蛋", "个", new BigDecimal("50.00"), 78, new BigDecimal("6.50"), new BigDecimal("0.60"), new BigDecimal("5.50"), now),
                createFood("鸭蛋", "肉蛋", "个", new BigDecimal("70.00"), 130, new BigDecimal("12.00"), new BigDecimal("1.00"), new BigDecimal("8.50"), now),
                createFood("三文鱼", "肉蛋", "克", new BigDecimal("100.00"), 208, new BigDecimal("20.00"), BigDecimal.ZERO, new BigDecimal("13.00"), now),
                createFood("虾", "肉蛋", "克", new BigDecimal("100.00"), 99, new BigDecimal("20.90"), new BigDecimal("0.20"), new BigDecimal("1.70"), now),
                createFood("草鱼", "肉蛋", "克", new BigDecimal("100.00"), 113, new BigDecimal("16.60"), BigDecimal.ZERO, new BigDecimal("5.20"), now),

                createFood("牛奶", "奶豆", "毫升", new BigDecimal("250.00"), 135, new BigDecimal("7.50"), new BigDecimal("12.00"), new BigDecimal("8.00"), now),
                createFood("酸奶（原味）", "奶豆", "克", new BigDecimal("100.00"), 72, new BigDecimal("2.50"), new BigDecimal("9.30"), new BigDecimal("2.70"), now),
                createFood("豆浆", "奶豆", "毫升", new BigDecimal("250.00"), 85, new BigDecimal("6.00"), new BigDecimal("10.00"), new BigDecimal("2.50"), now),
                createFood("豆腐", "奶豆", "克", new BigDecimal("100.00"), 76, new BigDecimal("8.00"), new BigDecimal("1.90"), new BigDecimal("4.80"), now),
                createFood("豆腐脑", "奶豆", "碗", new BigDecimal("200.00"), 100, new BigDecimal("8.00"), new BigDecimal("3.00"), new BigDecimal("5.00"), now),
                createFood("黄豆", "奶豆", "克", new BigDecimal("100.00"), 359, new BigDecimal("35.00"), new BigDecimal("34.20"), new BigDecimal("16.00"), now),

                createFood("青菜", "蔬菜", "克", new BigDecimal("100.00"), 15, new BigDecimal("1.50"), new BigDecimal("2.70"), new BigDecimal("0.30"), now),
                createFood("菠菜", "蔬菜", "克", new BigDecimal("100.00"), 28, new BigDecimal("2.60"), new BigDecimal("4.50"), new BigDecimal("0.30"), now),
                createFood("西兰花", "蔬菜", "克", new BigDecimal("100.00"), 34, new BigDecimal("2.80"), new BigDecimal("6.60"), new BigDecimal("0.40"), now),
                createFood("西红柿", "蔬菜", "克", new BigDecimal("100.00"), 19, new BigDecimal("0.90"), new BigDecimal("4.00"), new BigDecimal("0.20"), now),
                createFood("黄瓜", "蔬菜", "克", new BigDecimal("100.00"), 16, new BigDecimal("0.80"), new BigDecimal("2.90"), new BigDecimal("0.20"), now),
                createFood("胡萝卜", "蔬菜", "克", new BigDecimal("100.00"), 41, new BigDecimal("0.90"), new BigDecimal("9.60"), new BigDecimal("0.20"), now),
                createFood("土豆", "蔬菜", "克", new BigDecimal("100.00"), 77, new BigDecimal("2.00"), new BigDecimal("17.00"), new BigDecimal("0.10"), now),
                createFood("茄子", "蔬菜", "克", new BigDecimal("100.00"), 25, new BigDecimal("1.10"), new BigDecimal("5.90"), new BigDecimal("0.20"), now),
                createFood("白菜", "蔬菜", "克", new BigDecimal("100.00"), 17, new BigDecimal("1.50"), new BigDecimal("3.20"), new BigDecimal("0.10"), now),
                createFood("生菜", "蔬菜", "克", new BigDecimal("100.00"), 13, new BigDecimal("1.30"), new BigDecimal("2.00"), new BigDecimal("0.30"), now),

                createFood("苹果", "水果", "个", new BigDecimal("150.00"), 78, new BigDecimal("0.30"), new BigDecimal("20.00"), new BigDecimal("0.20"), now),
                createFood("香蕉", "水果", "根", new BigDecimal("120.00"), 107, new BigDecimal("1.30"), new BigDecimal("27.00"), new BigDecimal("0.40"), now),
                createFood("橙子", "水果", "个", new BigDecimal("150.00"), 72, new BigDecimal("1.10"), new BigDecimal("18.00"), new BigDecimal("0.20"), now),
                createFood("葡萄", "水果", "克", new BigDecimal("100.00"), 69, new BigDecimal("0.60"), new BigDecimal("17.00"), new BigDecimal("0.40"), now),
                createFood("西瓜", "水果", "克", new BigDecimal("100.00"), 30, new BigDecimal("0.60"), new BigDecimal("7.60"), new BigDecimal("0.20"), now),
                createFood("草莓", "水果", "克", new BigDecimal("100.00"), 32, new BigDecimal("0.70"), new BigDecimal("7.70"), new BigDecimal("0.30"), now),
                createFood("蓝莓", "水果", "克", new BigDecimal("100.00"), 57, new BigDecimal("0.70"), new BigDecimal("14.00"), new BigDecimal("0.30"), now),
                createFood("芒果", "水果", "克", new BigDecimal("100.00"), 60, new BigDecimal("0.80"), new BigDecimal("15.00"), new BigDecimal("0.40"), now),

                createFood("花生", "坚果", "克", new BigDecimal("100.00"), 567, new BigDecimal("25.80"), new BigDecimal("16.10"), new BigDecimal("49.20"), now),
                createFood("核桃", "坚果", "克", new BigDecimal("100.00"), 646, new BigDecimal("14.90"), new BigDecimal("9.60"), new BigDecimal("58.80"), now),
                createFood("杏仁", "坚果", "克", new BigDecimal("100.00"), 579, new BigDecimal("22.50"), new BigDecimal("22.10"), new BigDecimal("50.60"), now),
                createFood("腰果", "坚果", "克", new BigDecimal("100.00"), 553, new BigDecimal("17.30"), new BigDecimal("24.00"), new BigDecimal("36.70"), now),

                createFood("花生油", "油脂", "克", new BigDecimal("100.00"), 899, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("99.90"), now),
                createFood("橄榄油", "油脂", "克", new BigDecimal("100.00"), 899, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("99.90"), now),
                createFood("黄油", "油脂", "克", new BigDecimal("100.00"), 717, new BigDecimal("0.90"), new BigDecimal("0.10"), new BigDecimal("81.10"), now),

                createFood("可乐", "饮料", "毫升", new BigDecimal("330.00"), 140, BigDecimal.ZERO, new BigDecimal("35.00"), BigDecimal.ZERO, now),
                createFood("果汁", "饮料", "毫升", new BigDecimal("250.00"), 105, new BigDecimal("0.50"), new BigDecimal("26.00"), new BigDecimal("0.30"), now),
                createFood("咖啡（黑）", "饮料", "毫升", new BigDecimal("250.00"), 5, new BigDecimal("0.30"), BigDecimal.ZERO, BigDecimal.ZERO, now),
                createFood("茶（无糖）", "饮料", "毫升", new BigDecimal("250.00"), 2, BigDecimal.ZERO, new BigDecimal("0.50"), BigDecimal.ZERO, now),
                createFood("啤酒", "饮料", "毫升", new BigDecimal("330.00"), 143, new BigDecimal("1.20"), new BigDecimal("10.60"), BigDecimal.ZERO, now),

                createFood("巧克力", "零食", "克", new BigDecimal("100.00"), 546, new BigDecimal("4.90"), new BigDecimal("54.00"), new BigDecimal("31.00"), now),
                createFood("薯片", "零食", "克", new BigDecimal("100.00"), 536, new BigDecimal("6.60"), new BigDecimal("52.00"), new BigDecimal("33.00"), now),
                createFood("饼干", "零食", "克", new BigDecimal("100.00"), 478, new BigDecimal("7.00"), new BigDecimal("65.00"), new BigDecimal("21.00"), now),
                createFood("蛋糕", "零食", "克", new BigDecimal("100.00"), 347, new BigDecimal("5.60"), new BigDecimal("51.00"), new BigDecimal("13.90"), now),
                createFood("冰淇淋", "零食", "克", new BigDecimal("100.00"), 207, new BigDecimal("3.50"), new BigDecimal("24.00"), new BigDecimal("11.00"), now)
        );
    }

    private Food createFood(String name, String category, String servingUnit,
                            BigDecimal servingSize, int calories,
                            BigDecimal protein, BigDecimal carbs, BigDecimal fat,
                            LocalDateTime createdAt) {
        Food food = new Food();
        food.setName(name);
        food.setCategory(category);
        food.setServingUnit(servingUnit);
        food.setServingSize(servingSize);
        food.setCalories(calories);
        food.setProtein(protein);
        food.setCarbs(carbs);
        food.setFat(fat);
        food.setIsCustom(false);
        food.setCreatedBy(null);
        food.setCreatedAt(createdAt);
        return food;
    }
}
