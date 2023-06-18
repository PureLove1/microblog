package com.microblog.service;

import com.microblog.common.Result;
import com.microblog.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.microblog.pojo.UserBaseInfo;
import com.microblog.pojo.UserHeaderVO;
import com.microblog.pojo.UserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 *
 * @author 贺畅
 */

public interface UserService extends IService<User> {

	/***
	 * 根据用户邮箱获取用户信息
	 * @param email 用户邮箱
	 * @return 用户全部信息（包括密码）
	 */
	User getUserByEmail(String email);

	/**
	 * 邮箱密码登录
	 * @param request
	 * @return
	 */
	Result emailPasswordLogin(HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException;

	/**
	 * 邮箱验证码登录
	 * @param request
	 * @param response
	 * @return
	 */
	Result emailCodeLogin(HttpServletRequest request, HttpServletResponse response);

	Result getUserByName(String name);

	UserBaseInfo getUserBaseInfoById(Long id);

	Result qeuryUser(String query, Integer currentPage, Integer pageSize);

	Result qeuryUserByIntroduce(String introduce, Integer currentPage, Integer pageSize);

	Result getUserAndFanByName(String name, Integer currentPage, Integer pageSize);
}
