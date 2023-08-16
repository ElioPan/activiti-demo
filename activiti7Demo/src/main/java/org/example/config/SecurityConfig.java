package org.example.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @ClassName SecurityConfig
 * @Description
 * @Date 2023/08/14 11:20:00 星期一
 * @Version 1.0
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().permitAll()  // 允许所有请求
                .and()
                .csrf().disable()          // 禁用 CSRF 保护
                .formLogin().disable()     // 禁用默认的登录页面
                .httpBasic().disable();    // 禁用 HTTP 基本认证
    }
}

