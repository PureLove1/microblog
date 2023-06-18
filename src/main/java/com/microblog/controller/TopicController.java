package com.microblog.controller;

import com.microblog.common.Result;
import com.microblog.domain.Topic;
import com.microblog.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;



/**
 * 主题控制器
 * @author 贺畅
 */
@RestController
@RequestMapping("/topic")
public class TopicController {
    @Autowired
    private TopicService topicService;

    /**
     * 热搜前10
     * @return
     */
    @GetMapping("/topTen")
    public Result getTopTenHotSearch(){
        return topicService.getTopTenHotSearch();
    }

    @GetMapping("/name/{name}")
    public Result getTopicByName(@PathVariable String name){
        return topicService.getTopicByName(name);
    }
}
