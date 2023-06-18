package com.microblog.common;

import com.microblog.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户Jwt信息封装
 * @author 贺畅
 * @date 2022/11/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtClaims {
	/**
	 * 用户id 由于jwt是无状态的，所以只能存id
	 */
	private long id;

	public JwtClaims(User user){
		this.id=user.getId();
	}
}
