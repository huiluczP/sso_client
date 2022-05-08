package com.huiluczp.config;

import com.huiluczp.interceptor.CookieInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    @Autowired
    CookieInterceptor cookieInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 因为需要获取之前的url并跳转，所以login和validate需要进行验证
        String[] addPath = {"/**"};
        String[] excludePath = {"/test"};
        registry.addInterceptor(cookieInterceptor)
                .addPathPatterns(addPath)
                .excludePathPatterns(excludePath);
    }
}
