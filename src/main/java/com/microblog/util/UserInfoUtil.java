package com.microblog.util;

import com.microblog.domain.User;

import java.time.LocalDateTime;


/**
 * 用户信息工具类
 * @author 贺畅
 */
public class UserInfoUtil {

    /**
     * 用户信息填充
     *
     * @param userId
     * @param password
     * @param email
     * @param birth
     * @return
     */
    public static User fillUserInfo(Long userId, String password, String email, LocalDateTime birth) {

        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        //属性设置
        user.setId(userId);
        user.setPassword(password);
        user.setEmail(email);
        user.setBirth(birth);
        user.setCreateTime(now);
        user.setLastModify(now);
        user.setName("用户" + userId);
        return user;
    }
}
