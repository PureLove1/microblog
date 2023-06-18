package com.microblog.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 贺畅
 * @date 2023/5/13
 */
@Data
public class Password implements  Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 新密码
	 */
	private String password;
	/**
	 * 旧密码
	 */
	private String oPassword;
}
