package com.example.redis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Auther: mingweilin
 * @Date: 1/10/2020 14:34
 * @Description:
 */
@Data
public class Article {
    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long status;
    @JsonIgnore
    private List<String> tags;
    private List<Comment> comments;
}
