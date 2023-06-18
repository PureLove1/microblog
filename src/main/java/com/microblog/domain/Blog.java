package com.microblog.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.microblog.pojo.UserBaseInfo;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

/**
 * @author 贺畅
 * @TableName blog
 */
@TableName(value = "blog")
@Data
@Document(indexName = "blog")
@Setting(shards = 6, replicas = 3)
public class Blog implements Serializable {
	/**
	 * 主键
	 */
	@TableId(value = "id")
	@Id
	private Long id;

	/**
	 * 所属用户id
	 */
	@TableField(value = "user_id")
	@Field(type = FieldType.Double)
	private Long userId;

	/**
	 * 文本内容
	 */
	@TableField(value = "content")
	@Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
	private String content;

	/**
	 * 图片或视频的url链接
	 */
	@TableField(value = "urls")
	@Field(type = FieldType.Text)
	private String urls;

	/**
	 * 类型：视频、图片、纯文本
	 */
	@TableField(value = "type")
	@Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
	private String type;

	/**
	 * 频道id
	 */
	@TableField(value = "channel_id")
	@Field(type = FieldType.Long)
	private Long channelId;

	/**
	 * 创建时间
	 */
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	@Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	@Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
	private LocalDateTime updateTime;

	/**
	 * 0：正常，1：被举报，2：被用户隐藏
	 */
	@TableField(value = "status", fill = FieldFill.INSERT)
	@Field(type = FieldType.Byte)
	private Byte status;

	/**
	 * 标题
	 */
	@TableField(value = "title")
	@Field(type = FieldType.Text)
	private String title;

	/**
	 * 点赞数
	 */
	@TableField(value = "like_num", fill = FieldFill.INSERT)
	@Field(type = FieldType.Integer)
	private Integer likeNum;

	/**
	 * 评论数
	 */
	@TableField(value = "comments_num", fill = FieldFill.INSERT)
	@Field(type = FieldType.Integer)
	private Integer commentsNum;

	/**
	 * 分享数
	 */
	@TableField(value = "share_num", fill = FieldFill.INSERT)
	@Field(type = FieldType.Integer)
	private Integer shareNum;

	/**
	 * 分享数
	 */
	@TableField(value = "degree")
	@Field(type = FieldType.Double)
	private Double degree;

	@TableField("retweet_id")
	@Field(type = FieldType.Long)
	private Long retweetId;


	/**
	 * 是否删除 1代表删除，0代表未删除
	 */
	@TableField("is_deleted")
	@Field(type = FieldType.Boolean)
	private Boolean deleted;

	/**
	 * 是否点赞
	 */
	@TableField(exist = false)
	private Boolean liked;

	/**
	 * 关联用户
	 */
	@TableField(exist = false)
	private User user;

	/**
	 * 关联主题
	 */
	@TableField(exist = false)
	private Topic topic;

	/**
	 * 展示评论
	 */
	@TableField(exist = false)
	private Boolean showComment = false;

	/**
	 * 展示评论
	 */
	@TableField(exist = false)
	private List<BlogComment> blogComments = null;

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;

	public void setUserBaseInfo(UserBaseInfo userBaseInfo) {
		this.user = new User();
		this.user.setId(userBaseInfo.getId());
		this.user.setName(userBaseInfo.getName());
		this.user.setImage(userBaseInfo.getImage());
		this.user.setFollowed(userBaseInfo.isFollowed());
	}
}