package com.microblog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.microblog.common.Result;
import com.microblog.domain.Channel;

import java.util.List;

/**
 * @author 贺畅
 */
public interface ChannelService extends IService<Channel> {
	/**
	 * 查询频道数据
	 * @return
	 */
	Result getAllChannels();
}
