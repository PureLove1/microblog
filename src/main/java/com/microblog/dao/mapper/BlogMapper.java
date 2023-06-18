package com.microblog.dao.mapper;

import com.microblog.domain.Blog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 贺畅
 * @Entity com.microblog.domain.Blog
 */
@Repository
public interface BlogMapper extends BaseMapper<Blog> {
	/**
	 * id为空查询全部公开博客，userId不为空查询对应用户公开博客
	 * @param userId 可以为空
	 * @return 博客公开信息和对应用户及主题
	 */
	List<Blog> selectAllBlog(Long userId);


	/**
	 * 查询自己全部博客
	 * @param userId 用户id
	 * @return 用户公开博客
	 */
	List<Blog> selectAllBlogByMyself(Long userId);

	List<Blog> listFollowUserBlog(@Param("userId")Long userId);

	List<Blog> selectUserBlogById(Long id);

	Blog getBlogAndUserByBlogId(Long id);


	List<Blog> selectBlogByTypeAndFollow(Long id, String type);

	List<Blog> listHotBlog(Integer currentPage, Integer pageSize);
}




