package com.microblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.microblog.common.UserHolder;
import com.microblog.dao.elasticsearch.BlogRepository;
import com.microblog.domain.Blog;
import com.microblog.domain.Follow;
import com.microblog.domain.User;
import com.microblog.pojo.UserBaseInfo;
import com.microblog.pojo.UserInfo;
import com.microblog.service.ElasticSearchService;
import com.microblog.service.FollowService;
import com.microblog.service.UserService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.microblog.constant.RedisKeyPrefix.BLOG_LIKE;

/**
 * @author 贺畅
 * @date 2023/4/26
 */
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {
	@Autowired
	private BlogRepository blogRepository;

	@Autowired
	private ElasticsearchRestTemplate elasticTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private FollowService followService;

	@Override
	public void deleteAll() {
		blogRepository.deleteAll();
	}


	@Override
	public void saveBlog(Blog blog) {
		blogRepository.save(blog);
	}

	@Override
	public void deleteBlog(Long id) {
		blogRepository.deleteAll();
	}

	@Override
	public Page<Blog> searchBlog(String query, int currentPage, int pageSize) {
		//分页条件(当前页，每页显示多少数据)
		PageRequest pageRequest = PageRequest.of(currentPage, pageSize);

		//构造搜索条件，关键词条，是否排序，是否分页，是否搜索结果高亮显示【关键词前后添加标签（自己指定）】
		NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withQuery(QueryBuilders.matchQuery("content", query))
//				.withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
//				.withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
				.withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
				.withPageable(pageRequest)
				.build();

		//获取查询命中情况
		SearchHits<Blog> search = elasticTemplate.search(searchQuery, Blog.class);
		if (search.getTotalHits() <= 0) {
			//如果没有命中数据
			return null;
		}

		//获取查询命中结果的内容
		List<SearchHit<Blog>> searchHits = search.getSearchHits();

		//定义一个保存返回查询结果实体类的集合
		List<Blog> blogs = new ArrayList<>();

		for (SearchHit<Blog> searchHit : searchHits) {
			blogs.add(searchHit.getContent());
		}

		HashSet<User> users = new HashSet<>();

		//获取当前用户
		//判断登录状态，未登录无需进行遍历，因为不登录无法点赞
		User currentUser = UserHolder.getCurrentUser();
		if (currentUser != null) {
			for (int i = 0; i < blogs.size(); i++) {
				//遍历博文
				Blog blog = blogs.get(i);
				Long blogId = blog.getId();
				UserBaseInfo user = userService.getUserBaseInfoById(blog.getUserId());

				//判断博文是否被点赞
				String key = BLOG_LIKE + ":" + blogId;
				Boolean isLiked = redisTemplate.opsForSet().isMember(key, currentUser.getId());

				//设置isLike
				blog.setLiked(isLiked);

				//判断博文用户的关注信息

				Long userId = blog.getUserId();
				int count = followService.count(new LambdaQueryWrapper<Follow>()
						.eq(Follow::getFollowUserId, userId)
						.eq(Follow::getUserId, currentUser.getId()));
				user.setFollowed(count > 0);
				blog.setUserBaseInfo(user);
				blogs.set(i, blog);
			}
		}
		//查询结果封装分页
		Page<Blog> page = new PageImpl<Blog>(blogs, pageRequest, search.getTotalHits());
		return page;
	}
}
