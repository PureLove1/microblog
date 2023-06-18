package com.microblog.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @ClassName KaptchaConfig
 * @Author 贺畅
 * @Date 2022/12/13
 * @Description 谷歌验证码工具kaptcha配置类（根据文字创建图片）
 **/
@Configuration
@Slf4j
public class KaptchaConfig {

    /**
     * 将 Producer - kaptcha核心的类（接口），它有默认的实现类 DefaultKaptcha（接口实例化为Bean）
     */
    @Bean
    public Producer kaptchaProducer() throws IOException {
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        //传入参数配置 properties 配置文件写入
        try(InputStream in = this.getClass().getResourceAsStream("/kaptcha.properties")) {
            InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
            Properties properties = new Properties();
            properties.load(inputStreamReader);Config config = new Config(properties);
            kaptcha.setConfig(config);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        //传入配置
        return kaptcha;
    }

}
