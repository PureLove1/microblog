package com.microblog.controller;

import com.microblog.common.Result;
import com.microblog.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 贺畅
 * @date 2022/11/28
 */
@RestController
@RequestMapping("/channel")
public class ChannelController {

	@Autowired
	private ChannelService channelService;

	@GetMapping
	public Result getAllChannels(){
		return Result.ok(channelService.list());
	}
}
