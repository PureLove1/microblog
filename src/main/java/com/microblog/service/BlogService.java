package com.microblog.service;

import com.microblog.common.Result;
import com.microblog.domain.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 * @author 贺畅
 */
public interface BlogService extends IService<Blog> {
	List<Blog> selectAllBlog(Long userId);

	List<Blog> selectAllBlogByMyself(Long userId);

	Result likeBlog(Long id);

	List<Blog> listFollowUserBlog(Long userId);

	List<Blog> selectUserBlogById(Long id);

	Blog getBlogAndUserByBlogId(Long id);

	List<Blog> selectBlogByType(String type);

	List<Blog> selectBlogByFollow(Long id, String type);

	List<Blog> listHotBlog(Integer currentPage, Integer pageSize);
}
