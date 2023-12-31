package com.microblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.microblog.common.Result;
import com.microblog.common.UserHolder;
import com.microblog.dao.elasticsearch.BlogRepository;
import com.microblog.dao.mapper.BlogCommentMapper;
import com.microblog.domain.Blog;
import com.microblog.domain.BlogComment;
import com.microblog.service.BlogCommentService;
import com.microblog.service.BlogService;
import com.microblog.util.RedisIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.microblog.constant.RedisKeyPrefix.BLOG_COMMENT_ID;

/**
 * @author 贺畅
 * @date 2022/12/18
 */
@Service
public class BlogCommentServiceImpl extends ServiceImpl<BlogCommentMapper, BlogComment>
		implements BlogCommentService {
	@Autowired
	private BlogCommentMapper blogCommentMapper;

	@Autowired
	private BlogService blogService;

	@Autowired
	private BlogRepository blogRepository;

	@Autowired
	private RedisIdWorker redisIdWorker;

	@Override
	public List<BlogComment> queryBlogComments(Long blogId, Integer way) {
		List<BlogComment> blogComments = blogCommentMapper.queryCommentsAndUser(blogId, way);
		return blogComments;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result addComment(BlogComment blogComments) {
		//使用redis生成的自增Id
		long blogCommentId = redisIdWorker.nextId(BLOG_COMMENT_ID);
		//获取并设置发布此评论的用户的id
		Long id = UserHolder.getCurrentUser().getId();
		blogComments.setUserId(id);
		blogComments.setId(blogCommentId);
		//保存评论
		save(blogComments);

		//文章评论数+1
		UpdateWrapper<Blog> wrapper = new UpdateWrapper<>();
		wrapper.eq("id", blogComments.getBlogId());
		wrapper.setSql("comments_num = comments_num + 1");
		blogService.update(wrapper);
		blogRepository.save(blogService.getById(blogComments.getBlogId()));
		return Result.ok("成功");
	}

}




