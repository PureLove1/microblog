package com.microblog.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName blog_topic
 */
@TableName(value ="blog_topic")
@Data
public class BlogTopic implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    @TableField(value = "blog_id")
    private Long blogId;

    /**
     * 
     */
    @TableField(value = "topic_id")
    private Long topicId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}