package com.example.redis.controller;

import cn.hutool.core.bean.BeanUtil;
import com.example.redis.entity.Article;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/articles")
    public List<Article> list() {
        HashOperations forHash = redisTemplate.opsForHash();
        List<Article> articles = new ArrayList<>();
        List<String> ids = redisTemplate.opsForList().range("article:list", 0, -1);
        ids.forEach(id ->{
            Map<Object,Object> articleMap = redisTemplate.boundHashOps("article:" + id).entries();
            Article article = BeanUtil.mapToBean(articleMap, Article.class, true);
            articles.add(article);
        });
        return articles;
    }
    @GetMapping("/articles/{id}")
    public Article get(@PathVariable Integer id) {
        Map<Object,Object> articleMap = redisTemplate.boundHashOps("article:" + id).entries();
        Article article = BeanUtil.mapToBean(articleMap, Article.class, true);
        return article;
    }
    @PostMapping("/articles")
    public String add(Article article) {
        Long id = redisTemplate.opsForValue().increment("article:count");
        redisTemplate.opsForList().leftPush("article:list",id.toString());
        String key = "article:" + id;
        redisTemplate.opsForHash().putAll(key, BeanUtil.beanToMap(article));

        return "文章添加成功！id为"+id;
    }

    @PutMapping("/articles/{id}")
    public String update(Article article) {
        String key = "article:" + article.getId();
        redisTemplate.opsForHash().putAll(key, BeanUtil.beanToMap(article));
        return "文章更新成功，id为："+article.getId();
    }

    @DeleteMapping("/articles/{id}")
    public String delete(@PathVariable Integer id) {
        String key = "article:" + id;
        redisTemplate.delete(key);
        return "文章删除成功！id为："+id;
    }
}
