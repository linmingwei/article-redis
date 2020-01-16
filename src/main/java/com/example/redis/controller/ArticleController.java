package com.example.redis.controller;

import com.example.redis.entity.Article;
import com.example.redis.entity.Comment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @Auther: mingweilin
 * @Date: 1/10/2020 14:39
 * @Description:
 */
@RestController
public class ArticleController {
    @Autowired
    private RedisTemplate redisTemplate;

    private Jackson2HashMapper hashMapper = new Jackson2HashMapper(false);
    private ObjectMapper objectMapper = new ObjectMapper();


    @GetMapping("/articles")
    public List<Article> list() {
        Jackson2HashMapper mapper;
        mapper = new Jackson2HashMapper(false);
        List<Article> articles = new ArrayList<>();
        List<Integer> ids = redisTemplate.opsForList().range("article:list", 0, -1);
        if (ids == null || ids.size() == 0) {
            return null;
        }
        ids.forEach(id -> {
            Map<String, Object> articleMap = redisTemplate.boundHashOps("article:" + id).entries();
            Article article = (Article) mapper.fromHash(articleMap);
            setComments(article);
            articles.add(article);
        });
        return articles;
    }

    @GetMapping("/articles/{id}")
    public Article get(@PathVariable Integer id) {
        Map<String, Object> articleMap = redisTemplate.boundHashOps("article:" + id).entries();
        Article article = null;
        if (Objects.nonNull(articleMap) && articleMap.size() != 0) {
            article = (Article) hashMapper.fromHash(articleMap);
        }
        setComments(article);
        setTags(article);
        return article;
    }


    @PostMapping("/articles")
    public String add(Article article) {
        Long id = redisTemplate.opsForValue().increment("article:count");
        article.setId(id);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        redisTemplate.opsForList().leftPush("article:list", id);
        String key = "article:" + id;
        Map<String, Object> toHash = hashMapper.toHash(article);
        redisTemplate.opsForHash().putAll(key, toHash);
        List<String> tags = article.getTags();
        if (tags != null && tags.size() != 0) {
            redisTemplate.opsForSet().add("article:" + id + ":tags", tags.toArray());
            tags.forEach(tag -> redisTemplate.opsForList().leftPush("tag:" + tag + ":articles", id));
        }
        return "文章添加成功！id为" + id;
    }

    @PutMapping("/articles/{id}")
    public String update(Article article) {
        String key = "article:" + article.getId();
        article.setUpdateTime(LocalDateTime.now());
        redisTemplate.opsForHash().putAll(key, hashMapper.toHash(article));
        return "文章更新成功，id为：" + article.getId();
    }

    @DeleteMapping("/articles/{id}")
    public String delete(@PathVariable Integer id) {
        String key = "article:" + id;
        redisTemplate.delete(key);
        redisTemplate.opsForList().remove("article:list", 0, id);
        return "文章删除成功！id为：" + id;
    }

    private Article setComments(Article article) {
        Map<String, Object> commentMap = redisTemplate.boundHashOps("article:" + article.getId() + ":comments").entries();
        List<Comment> comments = new ArrayList<>();
        commentMap.entrySet().stream().forEach(e-> comments.add(objectMapper.convertValue(e.getValue(),Comment.class)));
        article.setComments(comments);
        return  article;
    }

    private void setTags(Article article) {

    }

}
