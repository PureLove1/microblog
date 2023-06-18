package com.microblog.ws;

import com.alibaba.fastjson.JSON;
import com.microblog.domain.Message;
import com.microblog.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 贺畅
 * @date 2023/2/21
 * WebSocket端点定义
 */
@ServerEndpoint(value = "/chat/{id}")
@Component
public class ChatEndPoint {
	/**
	 * 存储在线用户
	 */
	private static final Map<Long, Session> ONLINE_USERS = new ConcurrentHashMap<>();

	private static MessageService messageService;

	/**
	 * 解决单例多例冲突导致无法注入
	 * @param messageService
	 */
	@Autowired
	private void setMessageService(MessageService messageService){
		ChatEndPoint.messageService = messageService;
	}

	/**
	 * 建立websocket连接后，被调用
	 * @param session
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("id")Long id) {
		//将session进行存储，用于发送消息
		ONLINE_USERS.put(id,session);
	}

	/**
	 * 浏览器发送消息到服务端，该方法被调用
	 * @param message
	 */
	@OnMessage
	public void onMessage(String message) throws IOException {
		Message messageObj = JSON.parseObject(message, Message.class);
		//消息存入数据库
		Long to = messageObj.getReceiver();
		Session session = ONLINE_USERS.get(to);

		if(session!=null){
			messageObj.setStatus((byte)0);
			messageService.save(messageObj);
			session.getBasicRemote().sendText(message);
		}else{
			messageService.save(messageObj);
		}
	}

	/**
	 * 断开 websocket 连接时被调用
	 * @param session
	 */
	@OnClose
	public void onClose(Session session, @PathParam("id")Long id) {
		//1,从ONLINE_USERS中剔除当前用户的session对象
		ONLINE_USERS.remove(id);
	}
}
