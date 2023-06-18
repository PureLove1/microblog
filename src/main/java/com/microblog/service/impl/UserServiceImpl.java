package com.microblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.microblog.common.JwtClaims;
import com.microblog.common.Result;
import com.microblog.common.UserHolder;
import com.microblog.dao.mapper.FollowMapper;
import com.microblog.domain.Follow;
import com.microblog.domain.TokenVO;
import com.microblog.domain.User;
import com.microblog.pojo.UserBaseInfo;
import com.microblog.pojo.UserHeaderAndFanNum;
import com.microblog.pojo.UserHeaderVO;
import com.microblog.pojo.UserInfo;
import com.microblog.service.UserService;
import com.microblog.dao.mapper.UserMapper;
import com.microblog.util.JwtTokenUtil;
import com.microblog.util.PasswordUtil;
import com.microblog.util.RememberMeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.microblog.constant.StatusCode.*;

/**
 * @author 贺畅
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
		implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private FollowMapper followMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 根据邮箱查询用户
	 *
	 * @param email 用户注册邮箱
	 * @return 用户全部信息
	 */
	@Override
	public User getUserByEmail(String email) {
		return getOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
	}

	/**
	 * 邮箱密码登录
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	@Override
	public Result emailPasswordLogin(HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		if (email != null && password != null) {
			User user = getUserByEmail(email);
			if (user != null && PasswordUtil.validatePassword(password, user.getSalt(), user.getPassword())) {
				String token = JwtTokenUtil.createJwt(new JwtClaims(user));
				String refreshToken = JwtTokenUtil.createRefreshToken(new JwtClaims(user));
				String rememberMe = request.getParameter("rememberMe");
				if (Boolean.parseBoolean(rememberMe)) {
					RememberMeUtil.setTokenIntoCookie(response, token);
				}
				TokenVO tokenVO = new TokenVO(user, token,refreshToken);
				return Result.ok("登陆成功，正在跳转", tokenVO);
			}
		}
		return
				Result.error("登陆失败，邮箱或密码错误", USER_WRONG_EMAIL_CODE_ERROR);
	}

	/**
	 * 邮箱验证码登录
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@Override
	public Result emailCodeLogin(HttpServletRequest request, HttpServletResponse response) {
		String email = request.getParameter("email");
		if (email != null) {
			User user = getUserByEmail(email);
			if (user != null) {
				String code = request.getParameter("verificationCode");
				String rightCode = (String) redisTemplate.opsForValue().get(email);
				if (code.equals(rightCode)) {
					String rememberMe = request.getParameter("rememberMe");
					String token = JwtTokenUtil.createJwt(new JwtClaims(user));
					String refreshToken = JwtTokenUtil.createRefreshToken(new JwtClaims(user));
					if (Boolean.parseBoolean(rememberMe)) {
						RememberMeUtil.setTokenIntoCookie(response, token);
					}
					TokenVO tokenVO = new TokenVO(user, token,refreshToken);
					return Result.ok("登陆成功，正在跳转", tokenVO);
				}
				return
						Result.error("输入的验证码有误", USER_WRONG_IMAGE_CODE_ERROR);
			} else {
				return
						Result.error("该邮箱尚未注册", USER_NOT_EXISTS_ERROR);
			}
		}
		return
				Result.error("邮箱不得为空", USER_REQUIRED_PARAMETER_IS_NULL_ERROR);
	}

	@Override
	public Result getUserByName(String name) {
		if (name == null || "".equals(name)) {
			return Result.ok();
		}
		List<UserHeaderVO> userList = userMapper.getUserByName(name);
		logger.info("用户信息{}", userList);
		return Result.ok(userList);
	}

	@Override
	public UserBaseInfo getUserBaseInfoById(Long id) {
		return userMapper.getUserBaseInfoById(id);
	}

	@Override
	public Result qeuryUser(String query, Integer currentPage, Integer pageSize) {
		List<UserHeaderAndFanNum> userList = userMapper.queryUser(query, (currentPage-1)*pageSize,pageSize);
		for (int i = 0; i < userList.size(); i++) {
			UserHeaderAndFanNum user = userList.get(i);

			Integer integer = followMapper.selectCount(new LambdaQueryWrapper<Follow>()
					.eq(Follow::getUserId, UserHolder.getCurrentUser().getId())
					.eq(Follow::getFollowUserId, user.getId()));
			if (integer != 0) {
				user.setFollowed(true);
			}
			userList.set(i,user);
		}
		return Result.ok(userList);
	}

	@Override
	public Result qeuryUserByIntroduce(String introduce, Integer currentPage, Integer pageSize) {
		List<UserHeaderAndFanNum> userList = userMapper.queryUserByIntroduce(introduce, (currentPage-1)*pageSize,pageSize);
		for (int i = 0; i < userList.size(); i++) {
			UserHeaderAndFanNum user = userList.get(i);

			Integer integer = followMapper.selectCount(new LambdaQueryWrapper<Follow>()
					.eq(Follow::getUserId, UserHolder.getCurrentUser().getId())
					.eq(Follow::getFollowUserId, user.getId()));
			if (integer != 0) {
				user.setFollowed(true);
			}
			userList.set(i,user);
		}


		return Result.ok(userList);
	}

	@Override
	public Result getUserAndFanByName(String name, Integer currentPage, Integer pageSize) {
		if(currentPage!=null&&pageSize!=null){
			currentPage=(currentPage-1)*pageSize;
		}
		List<UserHeaderAndFanNum> userList = userMapper.getUserAndFanByName(name,currentPage,pageSize);
		for (int i = 0; i < userList.size(); i++) {
			UserHeaderAndFanNum user = userList.get(i);

			Integer integer = followMapper.selectCount(new LambdaQueryWrapper<Follow>()
					.eq(Follow::getUserId, UserHolder.getCurrentUser().getId())
					.eq(Follow::getFollowUserId, user.getId()));
			if (integer != 0) {
				user.setFollowed(true);
			}
			userList.set(i,user);
		}
		return Result.ok(userList);
	}
}




