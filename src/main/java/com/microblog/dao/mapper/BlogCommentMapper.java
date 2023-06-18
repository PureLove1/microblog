package com.microblog.dao.mapper;

import com.microblog.domain.BlogComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 贺畅
 * @Entity com.microblog.domain.BlogComments
 */
@Repository
public interface BlogCommentMapper extends BaseMapper<BlogComment> {
	List<BlogComment> queryCommentsAndUser(Long blogId,Integer way);
}




