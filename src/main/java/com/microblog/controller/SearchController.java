package com.microblog.controller;

import com.microblog.annotation.HasAnyRole;
import com.microblog.common.Result;
import com.microblog.constant.RedisKeyPrefix;
import com.microblog.constant.UserRole;
import com.microblog.domain.Blog;
import com.microblog.service.BlogService;
import com.microblog.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.microblog.constant.RedisKeyPrefix.DAY_HOT_SEARCH;
import static com.microblog.constant.RedisKeyPrefix.HOUR_HOT_SEARCH;

/**
 * @author 贺畅
 * @date 2023/4/9
 */
@RestController
@RequestMapping("/search")
public class SearchController {

	@Autowired
	private ElasticSearchService elasticSearchService;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private BlogService blogService;

	@GetMapping("/blog/{query}")
	@HasAnyRole(UserRole.ROLE_USER)
	public Result searchBlogPage(@PathVariable String query, Integer currentPage, Integer pageSize) {
		saveSearch(query);
		Page<Blog> blogs = elasticSearchService.searchBlog(query, currentPage, pageSize);
		return Result.ok(blogs);
	}

	@GetMapping("/hot")
	public Result getHotSearch(Integer pageSize) {
		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();
		int month = now.getMonthValue();
		int day = now.getDayOfMonth();
		Set set = redisTemplate.opsForZSet().reverseRangeWithScores(
				DAY_HOT_SEARCH + ":" + year + ":" + month + ":" + day, 0, pageSize == null ? 10 : pageSize);
		return Result.ok(set);
	}

	@Async
	void saveSearch(String query) {
		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();
		int month = now.getMonthValue();
		int day = now.getDayOfMonth();
		int hour = now.getHour();
		String key = HOUR_HOT_SEARCH + ":" + year + ":" + month + ":" + day + ":" + hour;
		if (!redisTemplate.opsForZSet().addIfAbsent(key, query, 1.0)) {
			redisTemplate.opsForZSet().getOperations().expire(key, 24, TimeUnit.HOURS);
			redisTemplate.opsForZSet().incrementScore(key, query, 1.0);
		}
	}

}
