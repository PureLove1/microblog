package com.microblog.pojo;

import com.microblog.domain.User;
import lombok.Data;

/**
 * @author 贺畅
 * @date 2023/4/26
 */
@Data
public class UserHeaderAndFanNum {
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

	/**
	 * 粉丝数
	 */
	private Integer fanNum;

	/**
	 * 是否关注
	 */
	private boolean followed;
}
