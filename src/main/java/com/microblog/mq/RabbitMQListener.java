package com.microblog.mq;

import com.microblog.pojo.BlogAndTopic;
import com.microblog.service.TopicService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 贺畅
 * @date 2023/2/27
 **/
@Component
public class RabbitMQListener {

	@Autowired
	private TopicService topicService;

	@RabbitListener(queues = "addTopic.queue")
	public void listenSimpleQueueMessage(BlogAndTopic blogAndTopic) throws InterruptedException {
		//保存主题到数据库
		topicService.saveTopicAndBlogTopic(blogAndTopic);
	}

}

