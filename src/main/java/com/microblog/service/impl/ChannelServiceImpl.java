package com.microblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.microblog.dao.mapper.ChannelMapper;
import com.microblog.domain.Channel;
import com.microblog.service.ChannelService;
import org.springframework.stereotype.Service;


/**
 * @author 贺畅
 * @date 2023/4/27
 */
@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel>
		implements ChannelService {
}
