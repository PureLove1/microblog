package com.microblog.pojo;

import lombok.Data;
import lombok.Setter;

/**
 * @author 贺畅
 * @date 2023/4/26
 */
@Data
public class UserBaseInfo {
	/**
	 * id
	 */
	private Long id;
	/**
	 * 头像
	 */
	private String image;

	/**
	 * 名字
	 */
	private String name;

	/**
	 * 是否关注
	 */
	private boolean followed;

}
