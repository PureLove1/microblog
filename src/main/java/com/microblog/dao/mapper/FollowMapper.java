package com.microblog.dao.mapper;

import com.microblog.domain.Follow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.microblog.pojo.UserVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 贺畅
 * @Entity com.microblog.domain.Follow
 */
@Repository
public interface FollowMapper extends BaseMapper<Follow> {
}




