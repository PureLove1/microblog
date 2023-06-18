package com.microblog.dao.elasticsearch;

import com.microblog.MicroblogApplication;
import com.microblog.domain.Blog;
import com.microblog.service.BlogService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 贺畅
 * @date 2023/3/1
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes=MicroblogApplication.class)
public class TestElasticSearch {
//	@Autowired
//	private ElasticsearchTemplate elasticeSearchTemplate;

	@Autowired
	private BlogRepository blogRepository;

	@Autowired
	private BlogService blogService;

	@Test
	public void search(){


	}

//	@Test
//	public void save(){
//		for (Blog blog : blogService.selectAllBlog(2L)) {
//			blogRepository.save(blog);
//		}
//
//	}

	@Test
	public void delete(){
		blogRepository.deleteById(338945934098435L);
		blogRepository.deleteById(338683941093378L);
		blogRepository.deleteById(337794882863105L);
	}
}
