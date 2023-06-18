package com.microblog.service;

import com.microblog.common.Result;
import com.microblog.domain.Topic;
import com.baomidou.mybatisplus.extension.service.IService;
import com.microblog.pojo.BlogAndTopic;

import java.util.List;

/**
 *
 * @author 贺畅
 */
public interface TopicService extends IService<Topic> {
	/**
	 * @param blogAndTopic 博文和主题
	 * 保存博文话题
	 */
	void saveTopicAndBlogTopic(BlogAndTopic blogAndTopic);

	/**
	 * 获取最热的十条搜索
	 * @return 热搜集合
	 */
	Result getTopTenHotSearch();

	/**
	 * 根据名称模糊查询话题
	 * @param name
	 * @return
	 */
	Result getTopicByName(String name);
}
