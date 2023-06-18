package com.microblog.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.microblog.domain.User;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 贺畅
 * @date 2023/4/27
 */
@Data
public class UserPublicInfo implements Serializable {
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
	@TableField(value = "sex")
	private Byte sex;

	/**
	 * 邮箱
	 */
	@TableField(value = "email")
	private String email;

	/**
	 * 用户创建时间
	 */
	@TableField(value = "create_time")
	private LocalDateTime createTime;

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
	private LocalDateTime birth;

	/**
	 * 串行化ID
	 */
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;

	public UserPublicInfo(User user){
		this.id=user.getId();
		this.name=user.getName();
		this.image=user.getImage();
		this.birth=user.getBirth();
		this.introduce=user.getIntroduce();
		this.phone=user.getPhone();
		this.realName=user.getRealName();
		this.sex=user.getSex();
		this.email=user.getEmail();
		this.createTime=user.getCreateTime();
	}
}
