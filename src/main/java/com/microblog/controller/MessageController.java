package com.microblog.controller;

import com.microblog.annotation.HasAnyRole;
import com.microblog.common.Result;
import com.microblog.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.microblog.constant.UserRole.*;

/**
 * @author 贺畅
 */
@RestController
@RequestMapping("/message")
public class MessageController {

	@Autowired
	private MessageService messageService;

	/**
	 * 获取历史聊天记录
	 * @param fromId
	 * @param toId
	 * @return
	 */
	@GetMapping("/history/{fromId}/{toId}")
	@HasAnyRole(ROLE_USER)
	public Result getHistoryMessage(@PathVariable("fromId") Long fromId,
	                                @PathVariable("toId") Long toId) {
		return messageService.getHistoryMessage(fromId, toId);
	}
}
