package com.microblog.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
}
