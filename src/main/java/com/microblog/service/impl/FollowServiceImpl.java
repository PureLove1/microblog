package com.microblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.microblog.annotation.HasAnyRole;
import com.microblog.common.UserHolder;
import com.microblog.common.Result;
import com.microblog.domain.Follow;
import com.microblog.domain.User;
import com.microblog.dao.mapper.UserMapper;
import com.microblog.pojo.UserVO;
import com.microblog.service.FollowService;
import com.microblog.dao.mapper.FollowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.microblog.constant.RedisKeyPrefix.FOLLOW_SET;
import static com.microblog.constant.StatusCode.*;
import static com.microblog.constant.UserRole.ROLE_USER;

/**
 * @author 贺畅
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>
		implements FollowService {
	private final Logger logger = LoggerFactory.getLogger(FollowServiceImpl.class);

	@Autowired
	private FollowMapper followMapper;

	@Autowired
	private UserMapper userMapper;


	/**
	 * 关注和取关
	 *
	 * @param id
	 * @param isFollow
	 * @return
	 */
	@Override
	public Result follow(Long id, Boolean isFollow) {
		//获取当前用户id
		User currentUser = UserHolder.getCurrentUser();
		Long userId = currentUser.getId();
		LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<Follow>();
		wrapper.eq(Follow::getFollowUserId, id).eq(Follow::getUserId, userId);
		//判断是取关操作还是关注操作
		if (isFollow) {
			if (getOne(wrapper) == null) {
				//关注：添加一条记录到follow表
				Follow follow = new Follow();
				follow.setUserId(userId);
				follow.setFollowUserId(id);
				save(follow);
				return Result.ok("关注成功！");
			} else {
				return Result.error("已经关注了!", SYSTEM_EXECUTION_ERROR);
			}
		} else {
			if (getOne(wrapper) == null) {
				return Result.ok("已经取关了！");
			} else {
				//取关，删除follow表记录
				remove(wrapper);
			}

			return Result.ok("取关成功！");
		}
	}

	/**
	 * 判断是否关注该用户
	 *
	 * @param id
	 * @return
	 */
	@Override
	@HasAnyRole(ROLE_USER)
	public Result followOrNot(Long id) {
		//获取当前用户id
		Long userId = UserHolder.getCurrentUser().getId();
		LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<Follow>();
		wrapper.eq(Follow::getFollowUserId, id).eq(Follow::getUserId, userId);
		int count = count(wrapper);
		return Result.ok(count > 0);
	}

	/**
	 * 查询该id对应用户关注的人的信息
	 * @param id 需要查询的用户的id
	 * @return
	 */
	@Override
	public Result listAllFollowUsers(Long id) {
		//查询该用户的所有关注
		List<Follow> follows = followMapper.selectList(new LambdaQueryWrapper<Follow>().eq(Follow::getUserId, id));
		//查询当前用户所有关注
		Long currentUserId = UserHolder.getCurrentUser().getId();

		//判断是否查询的是自己的关注信息，是则无需计算直接返回结果
		if(id.equals(currentUserId)){
			List<UserVO> followList = new ArrayList<>();
			for (Follow follow : follows) {
				//查询该用户关注的人的信息
				UserVO userVO = userMapper.listUserInfo(follow.getFollowUserId());
				userVO.setFollowed(true);
				followList.add(userVO);
			}
			return Result.ok(followList);
		}

		List<Follow> currentUserFollow = followMapper.selectList(new LambdaQueryWrapper<Follow>().eq(Follow::getUserId, currentUserId));
		//将该用户的关注放入HashSet
		HashSet<Long> followSet = new HashSet(currentUserFollow.size());
		for (Follow follow : currentUserFollow) {
			followSet.add(follow.getFollowUserId());
		}

		List<UserVO> followList = new ArrayList<>();
		for (Follow follow : follows) {
			//查询该用户关注的人的信息
			UserVO userVO = userMapper.listUserInfo(follow.getFollowUserId());
			if (followSet.contains(userVO.getId())) {
				//对于共同关注设置为已关注
				userVO.setFollowed(true);
			}
			followList.add(userVO);
		}
		return Result.ok(followList);
	}

	/**
	 * 查询当前id对应用户的粉丝数
	 * @param id
	 * @return
	 */
	@Override
	public Result listAllFanUsers(Long id) {
		//查询该用户的所有粉丝
		List<Follow> fans = followMapper.selectList(new LambdaQueryWrapper<Follow>().eq(Follow::getFollowUserId, id));
		//查询当前用户所有关注
		Long currentUserId = UserHolder.getCurrentUser().getId();

		//当前用户关注和粉丝列表有无重复
		List<Follow> currentUserFollow = followMapper.selectList(new LambdaQueryWrapper<Follow>().eq(Follow::getUserId, currentUserId));
		//将该用户的关注放入HashSet
		HashSet<Long> followSet = new HashSet(currentUserFollow.size());
		for (Follow follow : currentUserFollow) {
			followSet.add(follow.getFollowUserId());
		}

		List<UserVO> fanList = new ArrayList<>();
		for (Follow follow : fans) {
			//查询该用户粉丝的信息
			UserVO userVO = userMapper.listUserInfo(follow.getUserId());
			if (followSet.contains(userVO.getId())) {
				//对于共同关注设置为已关注
				userVO.setFollowed(true);
			}
			fanList.add(userVO);
		}
		return Result.ok(fanList);
	}

}




