package com.microblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.microblog.common.Result;
import com.microblog.domain.Message;
import com.microblog.service.MessageService;
import com.microblog.dao.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

	@Autowired
	private MessageMapper messageMapper;

	@Override
	public Result getHistoryMessage(Long fromId, Long toId) {
		List<Message> historyMessage = messageMapper.getHistoryMessage(fromId, toId);
		for (Message message : historyMessage) {
			System.out.println(message);
		}
		return Result.ok(historyMessage);
	}
}




