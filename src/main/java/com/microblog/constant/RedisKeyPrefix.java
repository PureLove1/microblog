package com.microblog.constant;

/**
 * 统一Redis Key的前缀
 * @author 贺畅
 * @date 2022/11/28
 */
public class RedisKeyPrefix {
    /**
     * 注册激活码
     */
    public static final String REGISTRATION_VERIFICATION_CODE = "REGISTRATION_VERIFICATION_CODE";

    /**
     * 登录验证码
     */
    public static final String LOGIN_VERIFICATION_CODE = "LOGIN_VERIFICATION_CODE";

    /**
     * 用户自增ID
     */
    public static final String USER_ID = "USER_ID";

    /**
     * token
     */
    public static final String USER_TOKEN = "USER_TOKEN";

    /**
     * 博文自增ID
     */
    public static final String BLOG_ID = "BLOG_ID";

    /**
     * 博文评论自增ID
     */
    public static final String BLOG_COMMENT_ID = "BLOG_COMMENT_ID";

    /**
     * 博文和话题自增ID
     */
    public static final String BLOG_TOPIC_ID = "BLOG_TOPIC_ID";

    /**
     * 博文点赞集合
     */
    public static final String BLOG_LIKE = "BLOG_LIKE";

    /**
     * 博文点赞集合
     */
    public static final String BLOG_COMMENT_LIKE = "BLOG_COMMENT_LIKE";

    /**
     * 图片验证码
     */
    public static final String IMAGE_CODE = "IMAGE_CODE:";

    /**
     * 关注集合
     */
    public static final String FOLLOW_SET = "FOLLOW_SET:";

    /**
     * 小时热搜
     */
    public static final String HOUR_HOT_SEARCH="HOUR_HOT_SEARCH";

    /**
     * 24小时热搜
     */
    public static final String DAY_HOT_SEARCH="DAY_HOT_SEARCH";

    /**
     * 7天热搜
     */
    public static final String WEEK_HOT_SEARCH="WEEK_HOT_SEARCH";


}
