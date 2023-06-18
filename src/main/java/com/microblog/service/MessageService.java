package com.microblog.service;

import com.microblog.common.Result;
import com.microblog.domain.Message;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface MessageService extends IService<Message> {

	/**
	 * 获取历史消息记录
	 * @param fromId
	 * @param toId
	 * @return
	 */
	Result getHistoryMessage(Long fromId, Long toId);
}
