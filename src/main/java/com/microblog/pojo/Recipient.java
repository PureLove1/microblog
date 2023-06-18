package com.microblog.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮件接收人信息
 * @author 贺畅
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipient {
    /**
     * 收信人邮件地址
     */
    String to;

    /**
     * 收信人名称
     */
    String username;

    /**
     * 发信主题
     */
    String subject;
}
