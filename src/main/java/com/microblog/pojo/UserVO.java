package com.microblog.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 贺畅
 * @date 2023/4/11
 */
@Data
public class UserVO implements Serializable {
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
	 * 粉丝数量
	 */
	private Integer fanNum;

	/**
	 * 关注数量
	 */
	private Integer followNum;

	/**
	 * 自我介绍
	 */
	private String introduce;

	/**
	 * 是否关注
	 */
	private boolean followed;
}
