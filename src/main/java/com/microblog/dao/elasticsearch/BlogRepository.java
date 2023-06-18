package com.microblog.dao.elasticsearch;

import com.microblog.domain.Blog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 贺畅
 */
@Repository
public interface BlogRepository extends ElasticsearchRepository<Blog,Long> {
}
