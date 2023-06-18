package com.microblog.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.microblog.annotation.HasAnyRole;
import com.microblog.common.Result;
import com.microblog.common.UserHolder;
import com.microblog.domain.Blog;
import com.microblog.domain.BlogComments;
import com.microblog.domain.User;
import com.microblog.service.BlogCommentsService;
import com.microblog.service.BlogService;
import com.microblog.util.RedisIdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.microblog.constant.RedisKeyPrefix.BLOG_COMMENT_ID;
import static com.microblog.constant.RedisKeyPrefix.BLOG_COMMENT_LIKE;

import static com.microblog.constant.UserRole.*;

/**
 * @author 贺畅
 * @date 2022/11/28
 */
@RequestMapping("/blogComments")
@RestController
public class BlogCommentsController {
	private final Logger logger = LoggerFactory.getLogger(BlogCommentsController.class);

	@Autowired
	private BlogCommentsService blogCommentsService;


	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 获取博文评论
	 *
	 * @param blogId 博文id
	 * @return 博文评论
	 */
	@GetMapping("/{blogId}")
    @HasAnyRole({ROLE_TOURIST,ROLE_USER})
	public Result getBlogComments(@PathVariable Long blogId,@Nullable Integer way) {
		List<BlogComments> blogCommentsList = blogCommentsService.queryBlogComments(blogId,way);
		User currentUser = UserHolder.getCurrentUser();
		if (currentUser != null) {
			Long id = currentUser.getId();
			//设置是否评论
			setLiked(blogCommentsList, id);
		}

		return Result.ok(blogCommentsList);
	}

	/**
	 * 添加评论
	 *
	 * @param blogComments 评论
	 * @return 添加结果
	 */
	@HasAnyRole({ROLE_USER, ROLE_VIP, ROLE_ADMIN})
	@PostMapping
	public Result addComment(@RequestBody BlogComments blogComments) {
		return blogCommentsService.addComment(blogComments);
	}

	@HasAnyRole({ROLE_USER})
	@PostMapping("/like/{blogCommentsId}/{liked}")
	public Result likeBlogComment(@PathVariable Long blogCommentsId, @PathVariable boolean liked) {
		Long id = UserHolder.getCurrentUser().getId();
		String key = BLOG_COMMENT_LIKE + ":" + blogCommentsId;
        UpdateWrapper<BlogComments> updateWrapper = new UpdateWrapper<BlogComments>().eq("id", blogCommentsId);
        if (liked) {
			Long add = redisTemplate.opsForSet().add(key, id);
			if (add > 0) {
                updateWrapper.setSql("like_num = like_num + 1");
                if (blogCommentsService.update(updateWrapper)) {
                    return Result.ok();
                }
			}
			return Result.error("点赞失败");
		} else {
			Long remove = redisTemplate.opsForSet().remove(key, id);
			if (remove > 0) {
                updateWrapper.setSql("like_num = like_num - 1");
                if (blogCommentsService.update(updateWrapper)) {
                    return Result.ok();
                }
			}
			return Result.error("取消赞失败");
		}
	}

	/**
	 * 判断评论是否被点赞过
	 *
	 * @param blogCommentsList 评论列表
	 * @param id               用户id
	 * @return
	 */
	private List<BlogComments> setLiked(List<BlogComments> blogCommentsList, Long id) {
		for (BlogComments blogComments : blogCommentsList) {
			StringBuilder key = new StringBuilder(BLOG_COMMENT_LIKE);
			key.append(":" + blogComments.getId());
			String keyStr = key.toString();
			Boolean liked = redisTemplate.opsForSet().isMember(keyStr, id);
			blogComments.setLiked(liked);
			if (blogComments.getChildren() != null) {
				List<BlogComments> children = blogComments.getChildren();
				setLiked(children, id);
			}
		}
		return blogCommentsList;
	}
}
