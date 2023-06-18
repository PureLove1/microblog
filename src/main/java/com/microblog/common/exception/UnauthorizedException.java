package com.microblog.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author 贺畅
 * @date 2022/12/22
 */
@Getter
@AllArgsConstructor
public class UnauthorizedException extends RuntimeException {
	private String message;
}
