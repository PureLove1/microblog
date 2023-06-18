package com.microblog.util;

import com.microblog.domain.User;

/**
 * @author 贺畅
 * @date 2023/4/29
 * 用于去除敏感信息
 */
public class InfomationUtil {
	public static User blockSensitiveInformation(User user){
		user.setPassword(null);
		user.setLastLogin(null);
		user.setPhone(null);
		user.setSalt(null);
		user.setRole(null);
		user.setRealName(null);
		user.setStatus(null);
		return user;
	}
}
