package com.microblog.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;

/**
 * @author 贺畅
 * @date 2022/12/20
 */
public class RememberMeUtil {
	/***
	 * 添加token到cookie
	 * @param response
	 * @param token
	 */
	public static void setTokenIntoCookie(HttpServletResponse response, String token) {
		Cookie cookie = new Cookie("token", token);
		// 七天过期
		cookie.setMaxAge(60*60*24*7);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/**
	 * 添加唯一标识到cookie
	 * @param response
	 */
	public static void setIdentityIntoCookie(HttpServletResponse response) {
		String uuid = UUID.randomUUID().toString();
		long timestamp = LocalDateTime.now().toEpochSecond(UTC);
		response.addCookie(new Cookie("identity", uuid + timestamp));
	}
}
