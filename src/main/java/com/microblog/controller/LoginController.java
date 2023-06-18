package com.microblog.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.code.kaptcha.Producer;

import com.microblog.common.Result;

import com.microblog.common.exception.CustomException;

import com.microblog.domain.TokenVO;
import com.microblog.domain.User;
import com.microblog.service.UserService;
import com.microblog.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.microblog.constant.CommonConstant.EMAIL_PASSWORD_LOGIN;
import static com.microblog.constant.CommonConstant.VERIFICATION_CODE_LOGIN;
import static com.microblog.constant.RedisKeyPrefix.IMAGE_CODE;
import static com.microblog.constant.StatusCode.*;

/**
 * @author 贺畅
 * @date 2022/12/18
 */
@RestController
@RequestMapping("/login")
public class LoginController {

	private static Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private Producer producer;

	@Autowired
	private UserService userService;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private TemplateEngine templateEngine;

	@Value("${spring.mail.username}")
	private String from;

	@PostMapping
	public Result login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String loginWay = request.getParameter("loginWay");
		if (loginWay != null) {
			byte loginWayByte = Byte.parseByte(loginWay);
			// 邮箱密码登录
			if (loginWayByte == EMAIL_PASSWORD_LOGIN) {
				Cookie[] cookies = request.getCookies();
				String identity = null;
				if (cookies == null) {
					return Result.error("身份认证异常", USER_REQUIRED_PARAMETER_IS_NULL_ERROR);
				}
				for (Cookie cookie : cookies) {
					if ("identity".equals(cookie.getName())) {
						identity = cookie.getValue();
					}
				}
				if (identity != null) {
					String rightCode = (String) redisTemplate.opsForValue().get(identity);
					String code = request.getParameter("code");
					if (code != null && rightCode != null && code.toUpperCase().equals(rightCode.toUpperCase())) {
						return userService.emailPasswordLogin(request, response);
					}else{
						return Result.error("验证码错误或验证码已过期");
					}
				}
			} else if (loginWayByte == VERIFICATION_CODE_LOGIN) {
				logger.info("邮箱验证码登录");
				return userService.emailCodeLogin(request, response);
			}
		}
		return Result.error("请求参数错误！",
				USER_REQUIRED_PARAMETER_IS_NULL_ERROR);
	}

	/**
	 * 图片验证码
	 *
	 * @param servletRequest
	 * @param servletResponse
	 * @throws IOException
	 */
	@GetMapping("/getCode")
	public void getImageCode(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException {
		// 将servletRequest和servletResponse转换成HttpServletRequest和HttpServletResponse
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		// 声明 变量
		String uuidString;
		String key = null;

		// 判断请求是否存在名为identity的cookie
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("identity".equals(cookie.getName())) {
					key = cookie.getValue();
				}
			}
		}

		// 如果不存在名为identity的cookie就通过UUID+时间戳生成新的cookie
		if (key == null) {
			UUID uuid = UUID.randomUUID();
			uuidString = uuid.toString();
			long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
			key = IMAGE_CODE + uuidString + timestamp;

			// 设置cookie
			Cookie identity = new Cookie("identity", key);
			identity.setPath("/");
			identity.setMaxAge(600000);
			response.addCookie(identity);
		}

		//使用kaptcha生成随机验证码，并根据key和生成的文字存入redis
		String text = producer.createText();
		BufferedImage image = producer.createImage(text);
		redisTemplate.opsForValue().set(key, text, 60, TimeUnit.SECONDS);

		// 输出图片，前端以blob对象接收
		response.setContentType("image/png");
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			ImageIO.write(image, "png", outputStream);
		} catch (IOException e) {
			//若出现异常，输出到日志中
			logger.error("生成验证码失败{}", e.getMessage());
			throw new CustomException("未知异常");
		}
	}

	/**
	 * 获取邮箱验证码
	 *
	 * @return
	 * @throws MessagingException
	 */
	@PostMapping("/getEmailCode")
	public Result getEmailCode(ServletRequest servletRequest) throws MessagingException, CustomException {
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		String to = req.getParameter("to");
		logger.info("发送邮件至{}", to);
		User emailOwner = userService.getUserByEmail(to);
		if (emailOwner == null) {
			logger.warn("错误的邮箱参数");
			throw new CustomException("邮件地址错误");
		}
		//构建 MimeMessage（邮件的主体） Spring 提供了 MimeMessageHelper 帮助类
		String subject = "邮箱登陆验证码";
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		//生成随机数
		int randomCode = (int) ((Math.random() * 9 + 1) * 100000);
		String codeString = String.valueOf(randomCode);
		Context context = new Context();

		context.setVariable("username", emailOwner.getName());
		context.setVariable("code", codeString);
		context.setVariable("to", to);

		logger.error("设置的验证码是" + codeString);
		//随机数放入redis，并设置过期时间五分钟
		String content = templateEngine.process("/mailtemplate", context);
		try {
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			//设置邮件内容支持HTML文件格式
			helper.setText(content, true);
		} catch (MessagingException e) {
			logger.error("发送邮件失败：" + e.getMessage());
			throw new CustomException("邮件发送失败，请稍候再试！");
		}
		redisTemplate.opsForValue().set(to, codeString, 5L, TimeUnit.MINUTES);
		//调用 send 方法
		javaMailSender.send(helper.getMimeMessage());

		return Result.ok("邮件发送成功，五分钟内有效");
	}

	/**
	 * 异步校验邮箱是否被注册
	 *
	 * @param email 邮箱
	 * @return 校验结果
	 */
	@GetMapping("/checkEmail/{email}")
	public Result checkEmail(@PathVariable String email) {
		LambdaQueryWrapper<User> wrapper =
				new LambdaQueryWrapper<User>()
						.eq(User::getEmail, email);
		int count = userService.count(wrapper);
		if (count == 0) {
			return Result.ok();
		}
		return
				Result.error("该邮箱已被注册", USER_ALREADY_EXISTS_ERROR);
	}

//	/**
//	 * 携带cookie重新获取凭证
//	 * @param servletRequest
//	 * @return
//	 * @throws Exception
//	 */
//	@GetMapping("/getToken")
//	public Result getToken(ServletRequest servletRequest) throws Exception {
//		HttpServletRequest request = (HttpServletRequest) servletRequest;
//		Cookie[] cookies = request.getCookies();
//		String token=null;
//		if (cookies != null) {
//			for (Cookie cookie : request.getCookies()) {
//				if (cookie.getName().equals("token")) {
//					token = cookie.getValue();
//					logger.info("token详情信息{}",token);
//				}
//			}
//			if (token!=null){
//				User user = JSON.parseObject(JwtTokenUtil.parseJwtToken(token), User.class);
//				logger.info("用户详情信息{}",user.toString());
//				Long id = user.getId();
//				User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getId, id));
//				TokenVO tokenVO = new TokenVO(one, token);
//				return Result.ok(tokenVO);
//			}
//		}
//		return Result.error("cookie token校验失败",USER_LOGIN_EXPIRED_ERROR);
//	}
}
