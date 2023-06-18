package com.microblog.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author 贺畅
 * @date 2022/12/23
 */
@Component
public class MybatisPlusObjectHandler implements MetaObjectHandler {

	private final Logger logger = LoggerFactory.getLogger(MybatisPlusObjectHandler.class);

	/**
	 * 插入操作，自动填充
	 * @param metaObject
	 */
	@Override
	public void insertFill(MetaObject metaObject) {
		//需要填充的属性名    填充的值
		// 由于各个对象的属性值不同，在填充前需要对源对象是否具有该属性进行判断
		// 否则找不到对应的setter方法mybatis-plus会报错
		if (metaObject.hasSetter("createTime")) {
			metaObject.setValue("createTime", LocalDateTime.now());
		}
		if (metaObject.hasSetter("sex")) {
			metaObject.setValue("sex", (byte)2);
		}
		if (metaObject.hasSetter("commentsNum")) {
			metaObject.setValue("commentsNum", 0);
		}
		if (metaObject.hasSetter("fanNum")) {
			metaObject.setValue("fanNum", 0);
		}
		if (metaObject.hasSetter("followNum")) {
			metaObject.setValue("followNum", 0);
		}
		if (metaObject.hasSetter("likeNum")) {
			metaObject.setValue("likeNum", 0);
		}
		if (metaObject.hasSetter("shareNum")) {
			metaObject.setValue("shareNum", 0);
		}
		if (metaObject.hasSetter("status")) {
			metaObject.setValue("status", (byte) 0);
		}
	}

	/**
	 * 更新操作，自动填充
	 *
	 * @param metaObject
	 */
	@Override
	public void updateFill(MetaObject metaObject) {
		if (metaObject.hasSetter("lastModify")) {
			metaObject.setValue("lastModify", LocalDateTime.now());
		}
	}
}
