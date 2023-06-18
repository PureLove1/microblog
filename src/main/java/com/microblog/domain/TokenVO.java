package com.microblog.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * token信息封装
 *
 * @author 贺畅
 * @date 2022/12/21
 */
@Data
@AllArgsConstructor
public class TokenVO {
	private String userId;
	private String userIconUrl;
	private String username;
	private String token;
	private String refreshToken;

	public TokenVO(User user, String token, String refreshToken) {
		this.userId = user.getId().toString();
		this.userIconUrl = user.getImage();
		this.username = user.getName();
		this.token = token;
		this.refreshToken = refreshToken;
	}
}
