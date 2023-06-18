package com.microblog.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 贺畅
 * @date 2023/4/27
 */
@TableName("channel")
@Data
public class Channel {
	/**
	 * 编号 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/**
	 * 内容
	 */
	@TableField(value = "content")
	private String content;

	/**
	 * 插入时间
	 */
	@TableField(value = "create_time",fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;
}
