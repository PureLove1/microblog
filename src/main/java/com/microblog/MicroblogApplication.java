package com.microblog;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microblog.domain.Blog;
import com.microblog.service.BlogService;
import com.microblog.service.ElasticSearchService;
import org.elasticsearch.client.ml.EvaluateDataFrameRequest;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.microblog.constant.RedisKeyPrefix.DAY_HOT_SEARCH;
import static com.microblog.constant.RedisKeyPrefix.HOUR_HOT_SEARCH;

/**
 * @author 贺畅
 */
@EnableScheduling
@SpringBootApplication
@MapperScan(basePackages = "com.microblog.dao.mapper")
@EnableTransactionManagement
public class MicroblogApplication {

	/**
	 * 解决redis和elasticsearch之间启动Netty冲突
	 * redis去查    null，elasticsearch
	 */
	@PostConstruct
	public void init() {
		// 解决Netty启动冲突
		// Netty4Utils setAvailableProcessors()方法的 启动开关
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}

	@Autowired
	private BlogService blogService;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private ElasticSearchService elasticSearchService;


	private static Logger logger = LoggerFactory.getLogger(MicroblogApplication.class);

	public static void main(String[] args) {
		logger.info("容器启动");
		// 返回值就是ApplicationContext的子类对象
		SpringApplication.run(MicroblogApplication.class, args);
	}

	/**
	 * 每五分钟计算一下博文的热度
	 */
	@Scheduled(fixedRate = 5 * 60 * 1000)
	public void computeDegree() {
		logger.info("执行计算博文热度");
		List<Blog> list = blogService.list(new LambdaQueryWrapper<Blog>().orderByAsc(Blog::getCreateTime));
		Blog blog1 = list.get(0);
		long start = blog1.getCreateTime().toEpochSecond(ZoneOffset.UTC);

		for (Blog blog : list) {
			LocalDateTime createTime = blog.getCreateTime();
			Integer likeNum = blog.getLikeNum();
			Integer commentsNum = blog.getCommentsNum();
			Integer shareNum = blog.getShareNum();
			LocalDateTime now = LocalDateTime.now();
			long l = createTime.toEpochSecond(ZoneOffset.UTC);
			long nowStamp = now.toEpochSecond(ZoneOffset.UTC);
			//计算相差的天数
			long dayDiff = ((nowStamp - l) / (60 * 60 * 24));
			long res= (nowStamp - start)/(60 * 60 * 24);
			double v = (res - dayDiff) * (shareNum * 2 + likeNum + 1.5 * commentsNum);
			boolean id = blogService.update(new UpdateWrapper<Blog>().setSql("degree=" + v).eq("id", blog.getId()));
			elasticSearchService.saveBlog(blog);
			if(!id){
				logger.error("热度属性计算失败");
			}
		}
	}

	/**
	 * 每五分钟刷新热搜榜
	 */
	@Scheduled(fixedRate =5*60*1000)
	public void refreshDayHotSearch(){
		logger.info("执行计算当日热搜");
		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();
		int month = now.getMonthValue();
		int day = now.getDayOfMonth();
		int hour = now.getHour();
		List<String> otherKeys = new ArrayList();
		for (int i = 1; i <= 23; i++) {
			LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, 0).minusHours(i);
			String otherKey = HOUR_HOT_SEARCH + ":" + localDateTime.getYear() + ":" + localDateTime.getMonth() + ":" + localDateTime.getDayOfMonth() + ":" + localDateTime.getHour();
			otherKeys.add(otherKey);
		}
		redisTemplate.opsForZSet().unionAndStore(HOUR_HOT_SEARCH + ":" + year + ":" + month + ":" + day + ":" + hour,
				otherKeys, DAY_HOT_SEARCH + ":"+year+":"+month+":"+day);
		redisTemplate.opsForZSet().getOperations().expire(DAY_HOT_SEARCH + ":"+year+":"+month+":"+day,7, TimeUnit.DAYS);
	}



}
