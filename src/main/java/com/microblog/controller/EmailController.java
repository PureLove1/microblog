package com.microblog.controller;

import com.microblog.common.Result;
import com.microblog.common.exception.CustomException;
import com.microblog.domain.User;
import com.microblog.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @author 贺畅
 * @date 2023/5/12
 */
@RestController
@RequestMapping("/email")
public class EmailController {
	private static Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TemplateEngine templateEngine;

	@Value("${spring.mail.username}")
	private String from;
	/**
	 * 获取邮箱登录验证码
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

	@GetMapping("/register")
	public Result getRegisterCode(String email) throws MessagingException {
		logger.info("发送邮件至{}", email);
		//构建 MimeMessage（邮件的主体） Spring 提供了 MimeMessageHelper 帮助类
		String subject = "邮箱登陆验证码";
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		//生成随机数
		int randomCode = (int) ((Math.random() * 9 + 1) * 100000);
		String codeString = String.valueOf(randomCode);
		Context context = new Context();

		context.setVariable("username", email);
		context.setVariable("code", codeString);
		context.setVariable("to", email);

		logger.error("设置的验证码是" + codeString);
		//随机数放入redis，并设置过期时间五分钟
		String content = templateEngine.process("/mailtemplate", context);
		try {
			helper.setFrom(from);
			helper.setTo(email);
			helper.setSubject(subject);
			//设置邮件内容支持HTML文件格式
			helper.setText(content, true);
		} catch (MessagingException e) {
			logger.error("发送邮件失败：" + e.getMessage());
			throw new CustomException("邮件发送失败，请稍候再试！");
		}
		redisTemplate.opsForValue().set(email, codeString, 5L, TimeUnit.MINUTES);
		//调用 send 方法
		javaMailSender.send(helper.getMimeMessage());
		return Result.ok("邮件发送成功，五分钟内有效");
	}

	/**
	 * 获取邮箱验证码码
	 * @param email
	 * @return
	 * @throws MessagingException
	 */
	@GetMapping("/retrieve")
	public Result getCode(String email) throws MessagingException {
		logger.info("发送邮件至{}", email);
		//构建 MimeMessage（邮件的主体） Spring 提供了 MimeMessageHelper 帮助类
		String subject = "邮箱验证码";
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		//生成随机数
		int randomCode = (int) ((Math.random() * 9 + 1) * 100000);
		String codeString = String.valueOf(randomCode);
		Context context = new Context();

		context.setVariable("username", email);
		context.setVariable("code", codeString);
		context.setVariable("to", email);

		logger.error("设置的验证码是" + codeString);
		//随机数放入redis，并设置过期时间五分钟
		String content = templateEngine.process("/mailtemplate", context);
		try {
			helper.setFrom(from);
			helper.setTo(email);
			helper.setSubject(subject);
			//设置邮件内容支持HTML文件格式
			helper.setText(content, true);
		} catch (MessagingException e) {
			logger.error("发送邮件失败：" + e.getMessage());
			throw new CustomException("邮件发送失败，请稍候再试！");
		}
		redisTemplate.opsForValue().set(email, codeString, 5L, TimeUnit.MINUTES);
		//调用 send 方法
		javaMailSender.send(helper.getMimeMessage());
		return Result.ok("邮件发送成功，五分钟内有效");
	}
}
