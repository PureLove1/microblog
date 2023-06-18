package com.microblog.service;

import com.microblog.common.Result;
import com.microblog.domain.BlogComment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @date 2022/12/18
 * @author 贺畅
 */
public interface BlogCommentService extends IService<BlogComment> {

	/**
	 * 根据博客id查询评论
	 * @param blogId 博客id
	 * @return 评论集合
	 */
	List<BlogComment> queryBlogComments(Long blogId,Integer way);

	/**
	 * 添加评论
	 * @param blogComment 评论
	 * @return
	 */
	Result addComment(BlogComment blogComment);
}
