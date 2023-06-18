package com.microblog.dao.mapper;

import com.microblog.domain.Topic;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 贺畅
 * @Entity com.microblog.domain.Topic
 */
@Repository
public interface TopicMapper extends BaseMapper<Topic> {

	void saveTopicBlog(Long blogId, long topicId);

	List<Topic> getTopTenHotSearch();

	List<Topic> getTopicByName(String name);
}




