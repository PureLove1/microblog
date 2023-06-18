package com.microblog.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.microblog.annotation.HasAnyRole;
import com.microblog.common.UserHolder;
import com.microblog.common.exception.*;
import com.microblog.domain.User;
import com.microblog.service.UserService;
import com.microblog.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashSet;

import static com.microblog.constant.UserRole.ROLE_TOURIST;
import static com.microblog.util.StringUtil.*;

/**
 * 权限控制与登录检查
 * @author 贺畅
 * @date 2022/12/20
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
	private final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

	@Autowired
	private UserService userService;

	//token最短长度
	private static final int MIN_TOKEN_LENGTH = 6;

//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//		// 资源请求直接放行
//		if (!(handler instanceof HandlerMethod)) {
//			return true;
//		}
//		// 通过handler获取方法上面的注解
//		HandlerMethod handlerMethod = (HandlerMethod) handler;
//		// 存储访问方法所需要的权限
//		HashSet<Byte> roles = new HashSet<>();
//		HasAnyRole hasAnyRole = handlerMethod.getMethod().getAnnotation(HasAnyRole.class);
//		if (hasAnyRole == null || hasAnyRole.value().length == 0) {
//			// 对于不需要权限的方法直接放行
//			String headerToken = request.getHeader("Authorization");
//			String refreshToken = request.getHeader("RefreshToken");
//
//			String accessToken = null;
//			if (isNotBlank(headerToken) && headerToken.length() > MIN_TOKEN_LENGTH) {
//				// 去除token类别标识
//				accessToken = headerToken.substring(MIN_TOKEN_LENGTH);
//			}
//			if (accessToken == null) {
//				return true;
//			}
//			try {
//				JwtTokenUtil.parseTokenAndStoreUser(accessToken);
//			} catch (ExpiredJwtException e) {
//				//说明access_token已经过期
//				try {
//					//首先对refreshToken进行检测是否过期，没过期就存储refreshToken中携带的用户信息
//					if (JwtTokenUtil.parseTokenAndStoreUser(refreshToken)) {
//						//如果没过期，那就刷新accessToken和refreshToken的内容，将其修改至响应头
//						JwtTokenUtil.refreshToken(accessToken,refreshToken,response);
//					};
//				} catch (ExpiredJwtException ex) {
//					throw new CustomException(ex.getMessage());
//				}
//
//			}
//
//			return true;
//		} else {
//			// 添加需要的权限
//			for (byte i : hasAnyRole.value()) {
//				roles.add(i);
//			}
//		}
//		//尝试获取cookie
//		Cookie[] cookies = request.getCookies();
//		String token = null;
//		// 解析cookie
//		if (cookies != null) {
//			for (Cookie cookie : cookies) {
//				if ("token".equals(cookie.getName())) {
//					token = cookie.getValue();
//				}
//			}
//		}
//		//如果cookie存在token
//		if (isNotBlank(token)) {
//			if (JwtTokenUtil.parseTokenAndStoreUser(token)) {
//				return true;
//			} else {
//				//cookie中的token解析出错
//				logger.warn("错误的token:{}", token);
//			}
//		}
//
//		String headerToken = request.getHeader("Authorization");
//		String subToken = null;
//		if (isNotBlank(headerToken)) {
//			// 去除token类别标识
//			subToken = headerToken.substring(6);
//		} else {
//			if (!roles.contains(ROLE_TOURIST)) {
//				throw new UnauthorizedException("用户尚未登录");
//			} else {
//				return true;
//			}
//		}
//
//		if (isNotBlank(subToken)) {
//			try{
//				JwtTokenUtil.parseTokenAndStoreUser(subToken);
//			}catch (ExpiredJwtException e){
//
//			}
//			if (JwtTokenUtil.parseTokenAndStoreUser(subToken)) {
//				return true;
//			} else {
//				logger.warn("错误的token:{}", subToken);
//				throw new UnauthorizedException("用户尚未登录");
//			}
//		}
//
//		User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getId, UserHolder.getCurrentUser().getId()));
//		Byte role = one.getRole();
//		if (!roles.contains(role) && !roles.contains(ROLE_TOURIST)) {
//			throw new AccessDenyException("权限不足，请求被拒绝");
//		}
//		return true;
//	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 资源请求直接放行
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		// 通过handler获取方法上面的注解
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		// 存储访问方法所需要的权限
		HashSet<Byte> roles = new HashSet<>();
		HasAnyRole hasAnyRole = handlerMethod.getMethod().getAnnotation(HasAnyRole.class);
		//获取access_token
		String headerToken = request.getHeader("Authorization");
		//获取refresh_token
		String refreshHeader = request.getHeader("RefreshToken");
		String accessToken = null;
		String refreshToken = null;
		if (hasAnyRole != null && hasAnyRole.value().length != 0) {
			// 添加需要的权限
			for (byte i : hasAnyRole.value()) {
				roles.add(i);
			}
		}
		if (isNotBlank(headerToken) && headerToken.length() > MIN_TOKEN_LENGTH
				&& isNotBlank(refreshHeader) && refreshHeader.length() > MIN_TOKEN_LENGTH) {
			// 去除access_token类别标识
			accessToken = headerToken.substring(MIN_TOKEN_LENGTH);
			refreshToken = refreshHeader.substring(MIN_TOKEN_LENGTH);
			try {
				//尝试解析access_token
				JwtTokenUtil.parseTokenAndStoreUser(accessToken);
			} catch (ExpiredJwtException e) {
				//说明access_token已经过期，继续判断refresh_token
				try {
					//首先对refreshToken进行检测是否过期，没过期就存储refreshToken中携带的用户信息
					JwtTokenUtil.parseTokenAndStoreUser(refreshToken);
					//如果没过期，那就刷新accessToken和refreshToken的内容，将其修改至响应头
					boolean refreshResult = JwtTokenUtil.refreshToken(accessToken, refreshToken, response);
					if (refreshResult) {
						//token刷新成功后放行
						return true;
					}
				} catch (ExpiredJwtException ex) {
					//如果refresh_token也过期了，那么抛出自定义异常交给全局异常处理器处理
					throw new CustomException(ex.getMessage());
				}
			} catch (Exception ex) {
				//不需要权限的话仍旧放行
				if (roles.isEmpty()) {
					return true;
				}
				//需要权限但token校验失败的直接抛出异常信息
				logger.error(ex.getMessage());
				throw new CustomException(ex.getMessage());
			}
		}
		//拿到token只是拿到了用户信息，还需要进行权限判断
		//不需要权限直接放行
		if (roles.contains(ROLE_TOURIST) || roles.isEmpty()) {
			return true;
		} else if (UserHolder.getCurrentUser() != null) {
			//需要权限且用户信息存在时，获取用户权限数据进行比对
			User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getId, UserHolder.getCurrentUser().getId()));
			Byte role = one.getRole();
			//能对比成功直接放行
			if (roles.contains(role)) {
				return true;
			}else{
				throw new AccessDenyException("权限不足，请求被拒绝");
			}
		}
		// 需要权限但是用户信息不存在，直接抛出异常
		throw new UserNotLoginException("请先登录后再试");

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		// 清除ThreadLocal存储，防止内存泄漏
		UserHolder.clear();
	}
}
