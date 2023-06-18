package com.microblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.microblog.annotation.HasAnyRole;
import com.microblog.common.UserHolder;
import com.microblog.common.Result;
import com.microblog.dao.elasticsearch.BlogRepository;
import com.microblog.domain.Blog;
import com.microblog.domain.User;
import com.microblog.service.BlogService;
import com.microblog.dao.mapper.BlogMapper;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.microblog.constant.RedisKeyPrefix.BLOG_LIKE;
import static com.microblog.constant.UserRole.ROLE_USER;

/**
 * @author 贺畅
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
		implements BlogService {
	@Autowired
	private BlogMapper blogMapper;

	@Autowired
	private BlogRepository blogRepository;

	@Autowired
	private RedisTemplate redisTemplate;


	/**
	 * 查询所有博客包括用户和主提信息，按时间排序，也可以按用户id查询
	 *
	 * @return 所有博客包括用户和主题信息，按时间排序
	 */
	@Override
	public List<Blog> selectAllBlog(Long userId) {
		return blogMapper.selectAllBlog(userId);
	}

	/**
	 * 用户查询自己的博客
	 *
	 * @param userId 用户id
	 * @return 自己的博客内容和公开信息
	 */
	@Override
	public List<Blog> selectAllBlogByMyself(Long userId) {
		return null;
	}

	/**
	 * 博文点赞
	 *
	 * @param id 博文id
	 * @return 点赞结果
	 */
	@Override
	@HasAnyRole(ROLE_USER)
	public Result likeBlog(Long id) {
		//获取登录用户
		User currentUser = UserHolder.getCurrentUser();
		Long userId = currentUser.getId();
		//判断是否点赞
		String key = BLOG_LIKE + ":" + id;
		Boolean isMember = redisTemplate.opsForSet().isMember(key, userId);
		if (Boolean.FALSE.equals(isMember)) {
			//未点赞 数据库点赞数+1
			boolean isSuccess = update().setSql("like_num = like_num + 1").eq("id", id).update();

			if (isSuccess) {

				//修改成功redis添加用户
				redisTemplate.opsForSet().add(key, userId);
				Blog byId = getById(id);
				blogRepository.save(byId);
			}
		} else {
			//已点赞数据库点赞数-1
			boolean isSuccess = update().setSql("like_num = like_num - 1").eq("id", id).update();

			if (isSuccess) {
				//修改成功redis移出用户
				redisTemplate.opsForSet().remove(key, userId);
				Blog byId = getById(id);
				blogRepository.save(byId);
			}
		}

		return Result.ok();
	}

	@Override
	public List<Blog> listFollowUserBlog(Long userId) {
		return blogMapper.listFollowUserBlog(userId);
	}

	@Override
	public List<Blog> selectUserBlogById(Long id) {
		return blogMapper.selectUserBlogById(id);
	}

	@Override
	public Blog getBlogAndUserByBlogId(Long id) {
		return blogMapper.getBlogAndUserByBlogId(id);
	}

	@Override
	public List<Blog> selectBlogByType(String type) {
		return blogMapper.selectBlogByTypeAndFollow(null, type);
	}

	@Override
	public List<Blog> selectBlogByFollow(Long id, String type) {
		return blogMapper.selectBlogByTypeAndFollow(id, type);
	}

	@Override
	public List<Blog> listHotBlog(Integer currentPage, Integer pageSize) {
		if (currentPage != null && pageSize != null) {
			currentPage = (currentPage - 1) * pageSize;
		}
		return blogMapper.listHotBlog(currentPage, pageSize);
	}

}




