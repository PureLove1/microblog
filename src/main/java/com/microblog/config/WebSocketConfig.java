package com.microblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author 贺畅
 * @date 2023/2/21
 */
@Configuration
public class WebSocketConfig {

	/**
	 * 注入ServerEndpointExporter对象扫描带有@ServerEndpoint注解的类
	 * @return
	 */
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
}
