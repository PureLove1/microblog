package com.microblog.service;

import com.microblog.common.Result;
import com.microblog.domain.Follow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author 贺畅
 */
public interface FollowService extends IService<Follow> {
	/**
	 * @param id 用户id
	 * @param isFollow 关注
	 * @return 关注结果
	 * @description 关注
	 */
	Result follow(Long id, Boolean isFollow);

	/**
	 * @param id 用户id
	 * @return 是否关注
	 * 判断是否关注
	 */
	Result followOrNot(Long id);

	/**
	 *
	 * @param id 需要查询的用户的id
	 * @return
	 */
	Result listAllFollowUsers(Long id);

	Result listAllFanUsers(Long id);
}
