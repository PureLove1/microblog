package com.microblog.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

/**
 * @author 贺畅
 * @TableName topic
 */
@TableName(value = "topic")
@Data
public class Topic implements Serializable {
	/**
	 * 主键
	 */
	@TableId(value = "id")
	private Long id;

	/**
	 * 主题内容
	 */
	@TableField(value = "content")
	private String content;

	/**
	 * 最后一次修改
	 */
	@TableField(value = "last_modify", fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime lastModify;

	/**
	 * 创建时间
	 */
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/**
	 *
	 */
	@TableField(value = "degree")
	private Integer degree;

	/**
	 *
	 */
	@TableField(value = "owner_id")
	private Long ownerId;

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}