package com.microblog.service;

import com.microblog.domain.Blog;
import org.springframework.data.domain.Page;

/**
 * @author 贺畅
 * @date 2023/4/26
 */
public interface ElasticSearchService {
	//向es服务器提交新发布的帖子
	void saveBlog(Blog blog);

	//删除es服务器中的某id的帖子
	void deleteBlog(Long id);

	//搜索方法(分页查询，关键字，第几页，每页显示多少条数据)
	// current>=0
	Page<Blog> searchBlog(String keyWord, int current, int limit);

	/**
	 * 删除全部
	 */
	void deleteAll();
}
