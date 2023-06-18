package com.microblog.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author 贺畅
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    /**
     * 密码，加密存储
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 生日
     */
    private LocalDateTime birth;

    /**
     * 输入的验证码
     */
    private int verificationCode;

    /**
     * 登录方式
     */
    private String loginWay; //pwd：账户密码方式，code：验证码登录


}
