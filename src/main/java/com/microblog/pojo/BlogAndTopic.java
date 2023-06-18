package com.microblog.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.microblog.domain.Blog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author 贺畅
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true) //允许忽略部分字段
public class BlogAndTopic implements Serializable {
    private Blog blog;
    private List<String> topicContentList;
    private Long userId;
}
