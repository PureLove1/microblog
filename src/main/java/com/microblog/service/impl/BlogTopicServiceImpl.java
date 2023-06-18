package com.microblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.microblog.domain.BlogTopic;
import com.microblog.service.BlogTopicService;
import com.microblog.dao.mapper.BlogTopicMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class BlogTopicServiceImpl extends ServiceImpl<BlogTopicMapper, BlogTopic>
    implements BlogTopicService{

}




