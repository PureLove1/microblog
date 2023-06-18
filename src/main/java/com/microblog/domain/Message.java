package com.microblog.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @author 贺畅
 * @TableName message
 */
@TableName(value ="message")
@Data
public class Message implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 发信人id
     */
    @TableField(value = "sender")
    private Long sender;

    /**
     * 收信人id
     */
    @TableField(value = "receiver")
    private Long receiver;

    /**
     * 内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 状态：0：已读 1：未读
     */
    @TableField(value="status")
    private Byte status;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}