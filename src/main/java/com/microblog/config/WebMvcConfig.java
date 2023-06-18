package com.microblog.config;

import com.microblog.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * springMVC配置
 * @author 贺畅
 * @date 2022/11/28
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

	/**
	 * 静态资源处理
	 * @param registry
	 */
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //  授权拦截
        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/login/**","/logout","/chat");
    }

    /**
     * 跨域配置（用于前后端分离）
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                // 允许路径
                .addMapping("/**")
                // 允许源
//                .allowedOrigins("http://localhost:8081")
                .allowedOriginPatterns("*")
                // 允许凭证
                .allowCredentials(true)
                // 允许的请求方式
                .allowedMethods("GET","HEAD","PUT","POST","DELETE","OPTIONS");
    }
}
