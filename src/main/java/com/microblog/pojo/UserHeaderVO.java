package com.microblog.pojo;

import lombok.Data;

/**
 * @author 贺畅
 * @date 2023/4/25
 */
@Data
public class UserHeaderVO {
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
	 * 自我介绍
	 */
	private String introduce;

}
