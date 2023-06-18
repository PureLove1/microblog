package com.microblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.microblog.common.Result;
import com.microblog.domain.Blog;
import com.microblog.domain.Topic;
import com.microblog.pojo.BlogAndTopic;
import com.microblog.service.TopicService;
import com.microblog.dao.mapper.TopicMapper;
import com.microblog.util.RedisIdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.microblog.constant.RedisKeyPrefix.BLOG_COMMENT_ID;

/**
 * @author 贺畅
 */
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic>
		implements TopicService {
	private static final Logger logger = LoggerFactory.getLogger(TopicServiceImpl.class);

	@Autowired
	private TopicMapper topicMapper;

	@Autowired
	private RedisIdWorker redisIdWorker;

	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveTopicAndBlogTopic(BlogAndTopic blogAndTopic) {
		logger.info("处理消息接收，添加blogAndTopic关系");
		Blog blog = blogAndTopic.getBlog();
		Long blogId = blog.getId();
		List<String> topics = blogAndTopic.getTopicContentList();
		for (String topic : topics) {
			//查询数据库中是否已经存在该话题
			Topic result = topicMapper.selectOne(new LambdaQueryWrapper<Topic>().eq(Topic::getContent, topic));
			if (result == null) {
				//不存在则生成id并插入数据到topic表
				long id = redisIdWorker.nextId(BLOG_COMMENT_ID);
				Topic topic1 = new Topic();
				topic1.setContent(topic);
				topic1.setId(id);
				topic1.setOwnerId(blogAndTopic.getUserId());
				topic1.setDegree(1);
				topicMapper.insert(topic1);
				topicMapper.saveTopicBlog(blogId, id);
			} else {
				update(new UpdateWrapper<Topic>().eq("id", result.getId()).setSql("degree = degree + 1"));
				//存在则只修改blog_topic表
				topicMapper.saveTopicBlog(blogId, result.getId());
			}
			LocalDateTime now = LocalDateTime.now();
			int year = now.getYear();
			int month = now.getMonthValue();
			int day = now.getDayOfMonth();
			int hour = now.getHour();
			long timestamp = LocalDateTime.of(year, month, day, hour, 0).toEpochSecond(ZoneOffset.UTC);
			String timestampString = String.valueOf(timestamp);
			// 小时key添加主题
			if (!redisTemplate.opsForZSet().addIfAbsent(timestampString, topic, 1.0)) {
				logger.error("已经存在当前博文话题");
				redisTemplate.opsForZSet().incrementScore(timestampString, topic, 1.0);
			}
		}
	}

	@Override
	public Result getTopTenHotSearch() {
		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();
		int month = now.getMonthValue();
		int day = now.getDayOfMonth();
		int hour = now.getHour();
		long timestamp = LocalDateTime.of(year, month, day, hour, 0).toEpochSecond(ZoneOffset.UTC);
		String timestampString = String.valueOf(timestamp);
		List<String> otherKeys = new ArrayList();
		for (int i = 1; i <= 23; i++) {
			timestamp = LocalDateTime.of(year, month, day, hour, 0).minusHours(i).toEpochSecond(ZoneOffset.UTC);
			String timestampStr = String.valueOf(timestamp);
			otherKeys.add(timestampStr);
		}
		redisTemplate.opsForZSet().unionAndStore(timestampString, otherKeys, "rank:hour:" + timestampString);
		Set set = redisTemplate.opsForZSet().reverseRangeWithScores("rank:hour:" + timestampString, 0, 9);
		return Result.ok(set);
	}

	@Override
	public Result getTopicByName(String name) {
		if(name==null){
			return Result.ok();
		}
		return Result.ok(topicMapper.getTopicByName(name));
	}
}




