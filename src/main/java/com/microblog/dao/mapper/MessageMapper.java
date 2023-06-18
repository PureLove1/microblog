package com.microblog.dao.mapper;

import com.microblog.domain.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 贺畅
 * @Entity com.microblog.domain.Message
 */
@Repository
public interface MessageMapper extends BaseMapper<Message> {

	List<Message> getHistoryMessage(Long fromId, Long toId);
}




