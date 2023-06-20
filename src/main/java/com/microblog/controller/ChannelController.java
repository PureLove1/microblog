package com.microblog.controller;

import com.alibaba.fastjson.JSONObject;
import com.microblog.common.Result;
import com.microblog.common.exception.CustomException;
import com.microblog.domain.Channel;
import com.microblog.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.microblog.constant.RedisKeyPrefix.CHANNEL_LIST;

/**
 * @author 贺畅
 * @date 2022/11/28
 */
@RestController
@RequestMapping("/channel")
public class ChannelController {

	@Autowired
	private ChannelService channelService;

	/**
	 * mysql获取全部频道
	 * @return
	 */
	@GetMapping
	public Result getAllChannels(){
		//在使用Redis缓存的情况下
		return channelService.getAllChannels();
	}
}
