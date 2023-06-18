package com.microblog.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 
 * @author 贺畅
 * @TableName blog_comments
 */
@TableName(value ="blog_comment")
@Data
public class BlogComment implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 评论人id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 父级评论id
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 评论内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 点赞数
     */
    @TableField(value = "like_num",fill = FieldFill.INSERT)
    private Integer likeNum;

    /**
     * 评论的博客id
     */
    @TableField(value = "blog_id")
    private Long blogId;

    /**
     * 评论状态：0-正常，1-被举报，2-被删除
     */
    @TableField(value = "status",fill = FieldFill.INSERT)
    private Byte status;

    /**
     * 评论时间
     */
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 回复的用户的名称
     */
    @TableField(value = "target")
    private String target;

    /**
     * 修改时间
     */
    @TableField(value = "last_modify",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastModify;

    /**
     * 是否删除 1代表删除，0代表未删除
     */
    @TableField(value = "is_deleted")
    private Boolean deleted;

    /**
     * 用户信息
     */
    @TableField(exist = false)
    private User user;

    /**
     * 点赞信息
     */
    @TableField(exist = false)
    private Boolean liked;

    /**
     * 子评论集合
     */
    @TableField(exist = false)
    private List<BlogComment> children;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}