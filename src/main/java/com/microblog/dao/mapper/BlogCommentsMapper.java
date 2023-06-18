package com.microblog.dao.mapper;

import com.microblog.domain.BlogComments;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 贺畅
 * @Entity com.microblog.domain.BlogComments
 */
@Repository
public interface BlogCommentsMapper extends BaseMapper<BlogComments> {
	List<BlogComments> queryCommentsAndUser(Long blogId,Integer way);
}




