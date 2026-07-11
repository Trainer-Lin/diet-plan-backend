package com.example.dietplan.common.config;

import com.example.dietplan.user.entity.SysUser;
import com.example.dietplan.user.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initAdmin();
    }

    private void initAdmin() {
        LambdaQueryWrapper<SysUser> query = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, "admin");
        if (sysUserMapper.selectCount(query) == 0) {
            SysUser admin = new SysUser();
            admin.setUsername("admin");
            admin.setEmail("admin@dietplan.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("系统管理员");
            admin.setRole("ADMIN");
            admin.setCreatedAt(java.time.LocalDateTime.now());
            admin.setUpdatedAt(java.time.LocalDateTime.now());
            sysUserMapper.insert(admin);
            log.info("管理员账号初始化成功: admin / admin123");
        }
    }
}
