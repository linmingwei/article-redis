package com.example.redis.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.redis.entity.Article;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther: mingweilin
 * @Date: 1/10/2020 14:39
 * @Description:
 */
@RestController
public class ArticleController {
    @Autowired
    private RedisTemplate redisTemplate;

    private Jackson2HashMapper mapper = new Jackson2HashMapper(false);


    @GetMapping("/articles")
    public List<Article> list() {
        Jackson2HashMapper mapper = new Jackson2HashMapper(false);
        List<Article> articles = new ArrayList<>();
        List<String> ids = redisTemplate.opsForList().range("article:list", 0, -1);
        ids.forEach(id ->{
            Map<String,Object> articleMap = redisTemplate.boundHashOps("article:" + id).entries();
            Article article = (Article) mapper.fromHash(articleMap);
            articles.add(article);
        });
        return articles;
    }
    @GetMapping("/articles/{id}")
    public Article get(@PathVariable Integer id) {
        Map<String,Object> articleMap = redisTemplate.boundHashOps("article:" + id).entries();
        Article article = (Article) mapper.fromHash(articleMap);
        return article;
    }
    @PostMapping("/articles")
    public String add(Article article) {
        Long id = redisTemplate.opsForValue().increment("article:count");
        article.setId(id);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        redisTemplate.opsForList().leftPush("article:list",id.toString());
        String key = "article:" + id;
        Map<String, Object> toHash = mapper.toHash(article);
        redisTemplate.opsForHash().putAll(key, toHash);

        return "文章添加成功！id为"+id;
    }

    @PutMapping("/articles/{id}")
    public String update(Article article) {
        String key = "article:" + article.getId();
        article.setUpdateTime(LocalDateTime.now());
        redisTemplate.opsForHash().putAll(key, mapper.toHash(article) );
        return "文章更新成功，id为："+article.getId();
    }

    @DeleteMapping("/articles/{id}")
    public String delete(@PathVariable Integer id) {
        String key = "article:" + id;
        redisTemplate.delete(key);
        redisTemplate.opsForList().remove("article:list",0,id);
        return "文章删除成功！id为："+id;
    }
}
