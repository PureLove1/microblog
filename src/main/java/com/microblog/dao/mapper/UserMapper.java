package com.microblog.dao.mapper;

import com.microblog.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.microblog.pojo.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 贺畅
 * @Entity com.microblog.domain.User
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
	UserVO listUserInfo(Long id);

	List<UserHeaderVO> getUserByName(String name);

	List<UserHeaderAndFanNum> getUserAndFanByName(String name, Integer currentPage, Integer pageSize);

	UserBaseInfo getUserBaseInfoById(Long id);

	List<UserHeaderAndFanNum> queryUser(String query, Integer currentPage, Integer pageSize);

	List<UserHeaderAndFanNum> queryUserByIntroduce(String introduce, Integer currentPage, Integer pageSize);
}




