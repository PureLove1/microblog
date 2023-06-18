package com.microblog.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 贺畅
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
	/**
	 * 主键
	 */
	@TableId(value = "id")
	private Long id;

	/**
	 * 用户昵称
	 */
	@TableField(value = "name")
	private String name;

	/**
	 * 1:男0:女2:保密
	 */
	@TableField(value = "sex",fill = FieldFill.INSERT)
	private Byte sex;

	/**
	 * 密码
	 */
	@TableField(value = "password")
	private String password;

	/**
	 * 邮箱
	 */
	@TableField(value = "email")
	private String email;

	/**
	 * 最后一次修改
	 */
	@TableField(value = "last_modify",fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime lastModify;

	/**
	 * 最后一次登录
	 */
	@TableField(value = "last_login")
	private LocalDateTime lastLogin;

	/**
	 * 0:正常 1:封号 2:注销 3：未激活
	 */
	@TableField(value = "status",fill = FieldFill.INSERT)
	private Byte status;

	/**
	 * 用户创建时间
	 */
	@TableField(value = "create_time",fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/**
	 * 被点赞数
	 */
	@TableField(value = "like_num",fill = FieldFill.INSERT)
	private Integer likeNum;

	/**
	 * 用户头像
	 */
	@TableField(value = "image")
	private String image;

	/**
	 * 用户介绍
	 */
	@TableField(value = "introduce")
	private String introduce;

	/**
	 * 电话号码
	 */
	@TableField(value = "phone")
	private String phone;

	/**
	 * 真实姓名
	 */
	@TableField(value = "real_name")
	private String realName;

	/**
	 * 生日
	 */
	@TableField(value = "birth")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime birth;

	/**
	 * 用户角色：0：普通用户 1：管理员 2：vip
	 */
	@TableField(value = "role")
	private Byte role;

	/**
	 * 密码加盐
	 */
	@TableField(value = "salt")
	private String salt;

	/**
	 * 粉丝数
	 */
	@TableField(value = "fan_num",fill = FieldFill.INSERT)
	private Integer fanNum;

	/**
	 * 关注数
	 */
	@TableField(value = "follow_num",fill = FieldFill.INSERT)
	private Integer followNum;

	/**
	 * 是否关注
	 */
	@TableField(exist = false)
	private boolean followed;

	/**
	 * 串行化ID
	 */
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;

}