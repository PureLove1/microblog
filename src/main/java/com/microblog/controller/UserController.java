package com.microblog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.microblog.annotation.HasAnyRole;
import com.microblog.common.Result;
import com.microblog.common.UserHolder;
import com.microblog.constant.StatusCode;
import com.microblog.domain.Blog;
import com.microblog.domain.User;

import com.microblog.pojo.Password;
import com.microblog.pojo.UserPublicInfo;
import com.microblog.service.BlogService;
import com.microblog.service.UserService;
import com.microblog.util.PasswordUtil;
import com.microblog.util.RedisIdWorker;
import com.mysql.jdbc.log.LogFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.microblog.constant.UserRole.ROLE_USER;
import static com.microblog.util.PasswordUtil.*;


/**
 * @author 贺畅
 * @date 2022/11/28
 */
@RestController
@RequestMapping("/user")
public class UserController {

	private static Logger logger = LoggerFactory.getLogger(UserController.class);

	/**
	 * RedisTemplate注入
	 */
	@Autowired
	private RedisTemplate redisTemplate;


	@Autowired
	private UserService userService;

	@Autowired
	private BlogService blogService;

	@Value("${spring.mail.username}")
	private String from;

	/**
	 * 根据用户id查询公开博文或所有波纹
	 *
	 * @param userId
	 * @return
	 */
	@GetMapping("/blog/{userId}")
	@HasAnyRole(ROLE_USER)
	public Result getUserBlog(@PathVariable Long userId) {
		User currentUser = UserHolder.getCurrentUser();
		Long id;
		List<Blog> blogs;
		if (currentUser != null) {
			id = currentUser.getId();
			//自己查自己
			if (id.equals(userId)) {
				blogs = blogService.selectAllBlogByMyself(userId);
			} else {
				//查别人
				blogs = blogService.selectAllBlog(userId);
			}
		} else {
			blogs = blogService.selectAllBlog(userId);
		}
		return Result.ok("查询成功", blogs);
	}

	@GetMapping("/email/{email}")
	public Result checkEmail(@PathVariable String email) {
		User user = userService.getUserByEmail(email);
		if (user == null) {
			return Result.ok(true);
		} else {
			return Result.ok("该邮箱已被注册！", false);
		}
	}

	@GetMapping("/name/{name}")
	@HasAnyRole(ROLE_USER)
	public Result getUserByName(@PathVariable String name, String ref, Integer currentPage, Integer pageSize) {
		if ("user".equals(ref)) {
			return userService.getUserAndFanByName(name, currentPage, pageSize);
		}
		return userService.getUserByName(name);
	}

	@GetMapping("/query/{query}")
	@HasAnyRole(ROLE_USER)
	public Result queryUser(@PathVariable String query, Integer currentPage, Integer pageSize) {
		return userService.qeuryUser(query, currentPage, pageSize);
	}

	@GetMapping("/introduce/{introduce}")
	@HasAnyRole(ROLE_USER)
	public Result queryUserByIntroduce(@PathVariable String introduce, Integer currentPage, Integer pageSize) {
		return userService.qeuryUserByIntroduce(introduce, currentPage, pageSize);
	}

	@GetMapping("/{id}")
	public Result getUser(@PathVariable Long id) {
		User user = userService.getOne(new LambdaQueryWrapper<User>().eq(id != null, User::getId, id));
		user.setPassword("******");
		if (user == null) {
			// 返回空数据
			return Result.ok();
		}
		// 返回用户信息
		return Result.ok(user);
	}

	@PutMapping
	@HasAnyRole(ROLE_USER)
	public Result updateUser(@RequestBody User user) {
		logger.info(user.toString());
		Long id = UserHolder.getCurrentUser().getId();
		user.setId(id);
		if (userService.updateById(user)) {
			return Result.ok("用户信息修改成功");
		}
		// 返回用户信息
		return Result.error("修改用户信息失败");
	}

	@PutMapping("/password")
	@HasAnyRole(ROLE_USER)
	public Result updateUserPassword(@RequestBody Password pass) throws NoSuchAlgorithmException {
		String password = pass.getPassword();
		String opassword = pass.getOPassword();

		logger.info(password + "----" + opassword);
		Long id = UserHolder.getCurrentUser().getId();
		User user = userService.getById(id);
		if (validatePassword(opassword, user.getSalt(), user.getPassword())) {
			String newPassword = encodePassword(password, user.getSalt());
			user.setPassword(newPassword);
			if (userService.updateById(user)) {
				return Result.ok("用户密码修改成功");
			} else {
				return Result.error("修改用户密码时出现未知错误");
			}
		} else {
			return Result.error("修改失败，原密码错误");
		}


	}

	@PutMapping("/retrieve")
	public Result retrievePassword(HttpServletRequest request) throws NoSuchAlgorithmException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String code = request.getParameter("code");
		if (!(email != null && password != null && code != null)) {
			return Result.error("请求参数错误");
		}
		Object o = redisTemplate.opsForValue().get(email);
		String s;
		if (o != null) {
			s = (String) o;
			if (s.equals(code)) {
				String salt = getSalt();
				String encodedPassword = encodePassword(password, salt);
				User user = new User();
				user.setSalt(salt);
				user.setPassword(encodedPassword);
				user.setEmail(email);
				user.setName(email);
				user.setRole((byte) 0);
				if (userService.update(new UpdateWrapper<User>()
						.eq("email", email)
						.setSql("password=" + encodedPassword)
						.setSql("salt=" + salt))) {
					return Result.ok("修改密码成功，正在跳转至登录界面");
				} else {
					return Result.error("修改密码失败");
				}
			} else {
				return Result.error("输入的验证码错误");
			}
		} else {
			return Result.error("验证码已过期");
		}

	}

	@PostMapping
	public Result register(HttpServletRequest request) throws NoSuchAlgorithmException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String code = request.getParameter("code");
		if (!(email != null && password != null && code != null)) {
			return Result.error("请求参数错误");
		}
		Object o = redisTemplate.opsForValue().get(email);
		String s;
		if (o != null) {
			s = (String) o;
			if (s.equals(code)) {
				String salt = getSalt();
				String encodedPassword = encodePassword(password, salt);
				User user = new User();
				user.setSalt(salt);
				user.setPassword(encodedPassword);
				user.setEmail(email);
				user.setName("无名");
				user.setImage("http://43.143.129.55:8080/group1/M00/00/00/CgAIEWRCN5-AQbYJAAB_UGs6DHU927.jpg");
				user.setRole((byte) 0);
				if (userService.save(user)) {
					return Result.ok("注册成功，正在跳转至登录界面");
				} else {
					return Result.error("注册失败");
				}
			} else {
				return Result.error("输入的验证码错误");
			}
		} else {
			return Result.error("验证码已过期");
		}
	}

	@GetMapping
	@HasAnyRole(ROLE_USER)
	public Result getOwnInfo() {
		User currentUser = UserHolder.getCurrentUser();
		if (currentUser == null) {
			return Result.error("登陆状态异常", StatusCode.USER_LOGIN_ERROR);
		} else {
			User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getId, currentUser.getId()));
			UserPublicInfo user = new UserPublicInfo(one);
			return Result.ok(user);
		}
	}


}

