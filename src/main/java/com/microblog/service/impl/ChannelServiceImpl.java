package com.microblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.microblog.common.Result;
import com.microblog.common.exception.CustomException;
import com.microblog.dao.mapper.ChannelMapper;
import com.microblog.domain.Channel;
import com.microblog.service.ChannelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.microblog.constant.RedisKeyPrefix.CHANNEL_LIST;


/**
 * @author 贺畅
 * @date 2023/4/27
 */
@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel>
		implements ChannelService {

	private static final Logger logger = LoggerFactory.getLogger(ChannelServiceImpl.class);

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 查询频道数据
	 * @return
	 */
	@Override
	public Result getAllChannels() {
		//获取缓存的频道数据
		Object o = redisTemplate.opsForValue().get(CHANNEL_LIST);
		if (o == null) {
			//缓存未命中,查询id和content
			LambdaQueryWrapper<Channel> wrapper = Wrappers.lambdaQuery(Channel.class)
					.select(Channel::getId, Channel::getContent)
					.orderByDesc(Channel::getCreateTime);
			List<Channel> channelList = list(wrapper);
			redisTemplate.opsForValue().set(CHANNEL_LIST, channelList, 2, TimeUnit.DAYS);
			return Result.ok(channelList);
		} else {
			try {
				//缓存命中，进行类型转换
				List<Channel> result = (List<Channel>) o;
				return Result.ok(result);
			} catch (Exception e) {
				throw new CustomException(e.getMessage());
			}
		}
	}
}
