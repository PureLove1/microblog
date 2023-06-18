package com.microblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.microblog.annotation.HasAnyRole;
import com.microblog.common.UserHolder;
import com.microblog.common.Result;
import com.microblog.domain.Blog;
import com.microblog.domain.BlogComment;
import com.microblog.domain.Follow;
import com.microblog.domain.User;
import com.microblog.pojo.BlogAndTopic;
import com.microblog.pojo.UserHeaderAndFanNum;
import com.microblog.service.*;
import com.microblog.util.InfomationUtil;
import com.microblog.util.RedisIdWorker;
import com.microblog.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.microblog.constant.RedisKeyPrefix.*;
import static com.microblog.constant.StatusCode.*;
import static com.microblog.constant.UserRole.*;
import static com.microblog.util.StringUtil.isNotBlank;

/**
 * @author 贺畅
 * @date 2022/11/28
 */
@RestController
@RequestMapping("/blog")
public class BlogController {

	private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

	@Autowired
	private BlogService blogService;

	@Autowired
	private BlogCommentService blogCommentService;

	@Autowired
	private RedisIdWorker redisIdWorker;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private UserService userService;

	@Resource
	private FollowService followService;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private ElasticSearchService elasticSearchService;

	public static String addTopicQueue = "addTopic.queue";


	/**
	 * 用户发布博客
	 *
	 * @param blog 博文
	 * @return 处理结果
	 */
	@PostMapping
	public Result addBlog(@RequestBody Blog blog) {
		//使用redis生成的自增Id
		long blogId = redisIdWorker.nextId(BLOG_ID);
		//从ThreadLocal获取当前用户信息
		User currentUser = UserHolder.getCurrentUser();
		Long userId = currentUser.getId();
		//获取文本内容
		String content = blog.getContent();
		//判断博客格式
		String urls = blog.getUrls();

		if (isNotBlank(urls)) {
			blog.setType("纯文本");
		} else {
			blog.setType("图片");
		}
		//设置博客id
		blog.setId(blogId);
		//设置所属用户id
		blog.setUserId(userId);
		//设置文本内容
		content = HtmlUtils.htmlEscape(content);
		blog.setContent(content);
		//执行保存
		boolean isSaved = blogService.save(blog);
		if (isSaved) {
			elasticSearchService.saveBlog(blog);
			return Result.ok("发布成功！");
		}
		return Result.error("发布失败！", SYSTEM_EXECUTION_ERROR, null);
	}

	/**
	 * 用户发布博客,存储话题到数据库
	 *
	 * @param blogAndTopic 博文和话题
	 * @return 处理结果
	 */
	@PostMapping("/topic")
	@HasAnyRole(ROLE_USER)
	public Result addBlogAndTopic(@RequestBody BlogAndTopic blogAndTopic) {
		//从ThreadLocal获取当前用户信息
		User currentUser = UserHolder.getCurrentUser();
		Long userId = currentUser.getId();

		Blog blog = blogAndTopic.getBlog();

		//使用redis生成的自增Id
		long blogId = redisIdWorker.nextId(BLOG_ID);
		//设置博客id
		blog.setId(blogId);

		//异步调用
		blogAndTopic.setBlog(blog);
		blogAndTopic.setUserId(userId);

		rabbitTemplate.convertAndSend(addTopicQueue, blogAndTopic);

		//获取文本内容
		String content = blog.getContent();
		//判断博客格式
		String urls = blog.getUrls();
		if (StringUtils.isEmpty(urls)) {
			blog.setType("纯文本");
		} else {
			blog.setType("图片");
		}

		//设置所属用户id
		blog.setUserId(userId);
		//设置文本内容
		content = HtmlUtils.htmlEscape(content);
		blog.setContent(content);
		//执行保存
		boolean isSaved = blogService.save(blog);
		if (isSaved) {
			elasticSearchService.saveBlog(blog);
			return Result.ok("发布成功！");
		}
		return Result.error("发布失败！", SYSTEM_EXECUTION_ERROR);
	}

	/**
	 * 视频微博
	 *
	 * @param blog 博文
	 * @return 处理结果
	 */
	@PostMapping("/video")
	@HasAnyRole(ROLE_USER)
	public Result addVideoBlog(@RequestBody Blog blog) {
		//从ThreadLocal获取当前用户信息
		User currentUser = UserHolder.getCurrentUser();
		Long userId = currentUser.getId();
		long id = redisIdWorker.nextId(BLOG_ID);
		blog.setUserId(userId);
		blog.setId(id);
		blog.setType("视频");
		boolean save = blogService.save(blog);
		if (save) {
			elasticSearchService.saveBlog(blog);
			return Result.ok("视频博客发布成功");
		}
		return Result.error();
	}


	/**
	 * 搜索视频微博
	 *
	 * @return 处理结果
	 */
	@GetMapping("/video")
	@HasAnyRole(ROLE_USER)
	public Result queryVideoBlog() {
		List<Blog> blogs = blogService.selectBlogByType("视频");
		//获取当前用户
		User currentUser = UserHolder.getCurrentUser();
		//判断登录状态，未登录无需进行遍历，因为不登录无法点赞
		if (currentUser != null) {
			for (int i = 0; i < blogs.size(); i++) {
				//遍历博文
				Blog blog = blogs.get(i);
				Long blogId = blog.getId();

				//判断博文是否被点赞
				String key = BLOG_LIKE + ":" + blogId;
				Boolean isLiked = redisTemplate.opsForSet().isMember(key, currentUser.getId());
				//设置isLike
				blog.setLiked(isLiked);
				//判断博文用户的关注信息
				User user = blog.getUser();
				Long userId = blog.getUserId();
				int count = followService.count(new LambdaQueryWrapper<Follow>()
						.eq(Follow::getFollowUserId, userId)
						.eq(Follow::getUserId, currentUser.getId()));
				user.setFollowed(count > 0);
				blogs.set(i, blog);
			}
		}
		return Result.ok("成功", blogs);
	}

	/**
	 * 搜索视频微博
	 *
	 * @return 处理结果
	 */
	@GetMapping("/video/follow")
	@HasAnyRole(ROLE_USER)
	public Result queryFollowVideoBlog() {
		User currentUser = UserHolder.getCurrentUser();
		List<Blog> blogs = blogService.selectBlogByFollow(currentUser.getId(), "视频");
		//获取当前用户

		//判断登录状态，未登录无需进行遍历，因为不登录无法点赞
		if (currentUser != null) {
			for (int i = 0; i < blogs.size(); i++) {
				//遍历博文
				Blog blog = blogs.get(i);
				Long blogId = blog.getId();

				//判断博文是否被点赞
				String key = BLOG_LIKE + ":" + blogId;
				Boolean isLiked = redisTemplate.opsForSet().isMember(key, currentUser.getId());
				//设置isLike
				blog.setLiked(isLiked);
				//判断博文用户的关注信息
				User user = blog.getUser();
				Long userId = blog.getUserId();
				int count = followService.count(new LambdaQueryWrapper<Follow>()
						.eq(Follow::getFollowUserId, userId)
						.eq(Follow::getUserId, currentUser.getId()));
				user.setFollowed(count > 0);
				blogs.set(i, blog);
			}
		}
		return Result.ok("成功", blogs);
	}

	/**
	 * 查询最新博文，添加点赞信息
	 *
	 * @return 博文
	 */
	@GetMapping
	@HasAnyRole({ROLE_USER, ROLE_TOURIST})
	public Result listAllBlog() throws InterruptedException {
		//查询全部博文
		List<Blog> blogs = blogService.selectAllBlog(null);
		//获取当前用户
		User currentUser = UserHolder.getCurrentUser();
		//判断登录状态，未登录无需进行遍历，因为不登录无法点赞
		if (currentUser != null) {
			for (int i = 0; i < blogs.size(); i++) {
				//遍历博文
				Blog blog = blogs.get(i);
				Long blogId = blog.getId();

				//判断博文是否被点赞
				String key = BLOG_LIKE + ":" + blogId;
				Boolean isLiked = redisTemplate.opsForSet().isMember(key, currentUser.getId());
				//设置isLike
				blog.setLiked(isLiked);
				//判断博文用户的关注信息
				User user = blog.getUser();
				Long userId = blog.getUserId();
				int count = followService.count(new LambdaQueryWrapper<Follow>()
						.eq(Follow::getFollowUserId, userId)
						.eq(Follow::getUserId, currentUser.getId()));
				user.setFollowed(count > 0);
				blogs.set(i, blog);
			}
		}
		return Result.ok("成功", blogs);
	}

	/**
	 * 查询关注的人发布的博文，添加点赞信息
	 *
	 * @return 博文
	 */
	@GetMapping("/follow")
	@HasAnyRole(ROLE_USER)
	public Result listFollowUserBlog(HttpServletRequest request) {
		//获取当前用户
		User currentUser = UserHolder.getCurrentUser();
		//查询全部博文
		List<Blog> blogs = blogService.listFollowUserBlog(currentUser.getId());

		for (int i = 0; i < blogs.size(); i++) {
			//遍历博文
			Blog blog = blogs.get(i);
			Long blogId = blog.getId();

			//判断博文是否被点赞
			String key = BLOG_LIKE + ":" + blogId;
			Boolean isLiked = redisTemplate.opsForSet().isMember(key, currentUser.getId());
			//设置isLike
			blog.setLiked(isLiked);

			//判断博文用户的关注信息
			User user = blog.getUser();
			Long userId = blog.getUserId();
			int count = followService.count(new LambdaQueryWrapper<Follow>()
					.eq(Follow::getFollowUserId, userId)
					.eq(Follow::getUserId, currentUser.getId()));
			user.setFollowed(count > 0);
			blogs.set(i, blog);
		}
		return Result.ok("成功", blogs);
	}

	/**
	 * 查询关注的人发布的博文，添加点赞信息
	 *
	 * @return 博文
	 */
	@GetMapping("/hot")
	@HasAnyRole(ROLE_USER)
	public Result listHotBlog(Integer currentPage, Integer pageSize) {
		//获取当前用户
		User currentUser = UserHolder.getCurrentUser();
		//查询全部博文
		List<Blog> blogs = blogService.listHotBlog(currentPage, pageSize);

		for (int i = 0; i < blogs.size(); i++) {
			//遍历博文
			Blog blog = blogs.get(i);
			Long blogId = blog.getId();

			//判断博文是否被点赞
			String key = BLOG_LIKE + ":" + blogId;
			Boolean isLiked = redisTemplate.opsForSet().isMember(key, currentUser.getId());
			//设置isLike
			blog.setLiked(isLiked);
			//判断博文用户的关注信息
			User user = blog.getUser();
			Long userId = blog.getUserId();
			int count = followService.count(new LambdaQueryWrapper<Follow>()
					.eq(Follow::getFollowUserId, userId)
					.eq(Follow::getUserId, currentUser.getId()));
			user.setFollowed(count > 0);
			blogs.set(i, blog);
		}
		return Result.ok("成功", blogs);
	}

	/**
	 * 根据id删除博文(仅自己)
	 *
	 * @param id
	 * @return
	 */
	@HasAnyRole({ROLE_USER, ROLE_ADMIN})
	@DeleteMapping("/{id}")
	public Result deleteBlogById(@PathVariable Long id) {
		//定义用于记录结果的变量
		boolean isDeleted;
		//获取当前用户
		User currentUser = UserHolder.getCurrentUser();

		//查询博客
		LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<Blog>();
		wrapper.eq(Blog::getId, id);
		Blog blog = blogService.getOne(wrapper);

		if (blog == null) {
			return Result.error("博文不存在！", SYSTEM_EXECUTION_ERROR);
		}
		//比对博客所属人和当前登录人是否同一人
		if (blog.getUserId().equals(currentUser.getId())) {
			isDeleted = blogService.removeById(id);
		} else {
			isDeleted = false;
		}
		//返回结果
		if (isDeleted) {
			elasticSearchService.deleteBlog(id);
			return Result.ok("删除成功！");
		} else {
			return Result.error("删除失败，你并非博文所有者！", USER_ACCESS_ERROR);
		}
	}

	/**
	 * 查看用户的博文
	 *
	 * @param id
	 * @return
	 */
	@HasAnyRole({ROLE_USER, ROLE_ADMIN})
	@GetMapping("/user/{id}")
	public Result getBlogByUserId(@PathVariable Long id) {
		List<Blog> blogs = blogService.selectUserBlogById(id);
		//获取当前用户
		User currentUser = UserHolder.getCurrentUser();
		//判断登录状态，未登录无需进行遍历，因为不登录无法点赞
		if (currentUser != null) {
			for (int i = 0; i < blogs.size(); i++) {
				//遍历博文
				Blog blog = blogs.get(i);
				Long blogId = blog.getId();
				//判断博文是否被点赞
				String key = BLOG_LIKE + ":" + blogId;
				Boolean isLiked = redisTemplate.opsForSet().isMember(key, currentUser.getId());
				//设置isLike
				blog.setLiked(isLiked);
				//判断博文用户的关注信息
				User user = blog.getUser();
				Long userId = blog.getUserId();
				int count = followService.count(new LambdaQueryWrapper<Follow>()
						.eq(Follow::getFollowUserId, userId)
						.eq(Follow::getUserId, currentUser.getId()));
				user.setFollowed(count > 0);
				blogs.set(i, blog);
			}
			return Result.ok(blogs);
		}
		return Result.ok(blogs);
	}

	@HasAnyRole(ROLE_USER)
	@GetMapping("/{id}")
	public Result getBlogById(@PathVariable Long id) {
		User currentUser = UserHolder.getCurrentUser();
		Blog blog = blogService.getById(id);
		Long userId = blog.getUserId();
		User user = userService.getById(userId);
		user = InfomationUtil.blockSensitiveInformation(user);
		int count = followService.count(new LambdaQueryWrapper<Follow>()
				.eq(Follow::getFollowUserId, userId)
				.eq(Follow::getUserId, currentUser.getId()));
		user.setFollowed(count > 0);
		blog.setUser(user);
		List<BlogComment> blogComments = blogCommentService.queryBlogComments(blog.getId(), 0);
		blog.setBlogComments(blogComments);
		return Result.ok(blog);
	}

	/**
	 * 博文点赞
	 *
	 * @param id
	 * @return
	 */
	@PutMapping("/like/{id}")
	@HasAnyRole(ROLE_USER)
	public Result likeBlog(@PathVariable Long id) {
		//首先判断博文是否存在
		LambdaQueryWrapper<Blog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(Blog::getId, id);
		Blog blog = blogService.getOne(lambdaQueryWrapper);

		//存在调用Service层
		if (blog != null) {
			return blogService.likeBlog(id);
		}

		//不存在直接返回
		return Result.error("博客不存在或已被删除！", SYSTEM_EXECUTION_ERROR);
	}


	@GetMapping("/{id}/comments/{way}")
	@HasAnyRole(ROLE_USER)
	public Result getBlogById(@PathVariable Long id, @PathVariable Integer way) {
		Blog blog = blogService.getBlogAndUserByBlogId(id);
		//获取当前用户
		User currentUser = UserHolder.getCurrentUser();
		//判断登录状态，未登录无需进行遍历，因为不登录无法点赞
		if (currentUser != null) {
			Long blogId = blog.getId();
			//判断博文是否被点赞
			String key = BLOG_LIKE + ":" + blogId;
			Boolean isLiked = redisTemplate.opsForSet().isMember(key, currentUser.getId());
			//设置isLike
			blog.setLiked(isLiked);

			//判断博文用户的关注信息
			User user = blog.getUser();
			Long userId = blog.getUserId();
			int count = followService.count(new LambdaQueryWrapper<Follow>()
					.eq(Follow::getFollowUserId, userId)
					.eq(Follow::getUserId, currentUser.getId()));
			user.setFollowed(count > 0);
			blog.setUser(user);
		}
		List<BlogComment> blogComments = blogCommentService.queryBlogComments(id, way);
		blog.setBlogComments(blogComments);
		return Result.ok(blog);
	}

}
