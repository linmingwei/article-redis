package com.example.redis.controller;

import com.example.redis.entity.Article;
import com.example.redis.entity.Comment;
import com.example.redis.util.CommonUtil;
import com.example.redis.util.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.core.query.SortQueryBuilder;
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
    private RedisTemplate redisTemplate;
    private ObjectMapper objectMapper;
    private Jackson2HashMapper hashMapper;

    @Autowired
    private ArticleController(RedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.hashMapper = new Jackson2HashMapper(objectMapper, false);
    }


    @GetMapping("/articles")
    public Page list(Integer current, Integer size) {
        Page page = new Page<Article>(current, size);
        List<Article> articles = new ArrayList<>();
        Set<Integer> ids = redisTemplate.opsForZSet().reverseRange("article:list", page.getStart(), page.getEnd());
        page.setTotal(redisTemplate.opsForZSet().size("article:list"));
        if (ids == null || ids.size() == 0) {
            return null;
        }
        ids.forEach(id -> {
            Map<String, Object> articleMap = redisTemplate.boundHashOps("article:" + id).entries();
            Article article = objectMapper.convertValue(articleMap, Article.class);
            setComments(article);
            articles.add(article);
        });
        page.setRecords(articles);
        return page;
    }

    @GetMapping("/articles/{id}")
    public Article get(@PathVariable Integer id) {
        Map<String, Object> articleMap = redisTemplate.boundHashOps("article:" + id).entries();
        Article article = null;
        if (Objects.nonNull(articleMap) && articleMap.size() != 0) {
            article = (Article) objectMapper.convertValue(articleMap, Article.class);
            setComments(article);
        }
        return article;
    }


    @PostMapping("/articles")
    public String add(Article article) {
        Long id = redisTemplate.opsForValue().increment("article:count");
        article.setId(id);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        redisTemplate.opsForZSet().add("article:list", id, CommonUtil.getTimestamps());
        String key = "article:" + id;
        Map<String, Object> toHash = hashMapper.toHash(article);
        redisTemplate.opsForHash().putAll(key, toHash);
        List<String> tags = article.getTags();
        if (tags != null && tags.size() != 0) {
            redisTemplate.opsForSet().add("article:" + id + ":tags", tags.toArray());
            tags.forEach(tag -> redisTemplate.opsForSet().add("tag:" + tag + ":articles", id));
        }
        return "文章添加成功！id为" + id;
    }

    @GetMapping("/articles/tag/{tag}")
    public List<Article> getByTag(@PathVariable String tag, Integer current, Integer size) {
        Page<Article> page = new Page<>(current, size);
        String key = "tag:" + tag + ":articles";
//        Set ids = redisTemplate.opsForSet().members(key);
        SortQuery<String> query = SortQueryBuilder.sort(key).by("article:*->createTime").alphabetical(true).limit(page.getStart(), page.getSize()).build();
        List ids = redisTemplate.sort(query);
        List<Article> articles = new ArrayList<>();
        ids.forEach(id -> {
            Map articleMap = redisTemplate.boundHashOps("article:" + id).entries();
            if (articleMap == null || articleMap.size() == 0) {
                redisTemplate.opsForSet().remove(key, id);
            } else {
                Article article = objectMapper.convertValue(articleMap, Article.class);
                articles.add(article);
            }
        });
        return articles;
    }

    @PutMapping("/articles/{id}")
    public String update(Article article) {
        Object id = redisTemplate.opsForHash().get("article:" + article.getId(), "id");
        if (Objects.isNull(id)) return "文章不存在";
        String key = "article:" + article.getId();
        article.setUpdateTime(LocalDateTime.now());
        redisTemplate.opsForHash().putAll(key, hashMapper.toHash(article));
        return "文章更新成功，id为：" + article.getId();
    }

    @DeleteMapping("/articles/{id}")
    public String delete(@PathVariable Integer id) {
        String key = "article:" + id;
        String commentKey = key + ":comments";
        String tagKey = key + ":tags";
        redisTemplate.delete(Arrays.asList(key, commentKey, tagKey));
        redisTemplate.opsForZSet().remove("article:list", id);
        return "文章删除成功！id为：" + id;
    }

    private Article setComments(Article article) {
        Map<String, Object> commentMap = redisTemplate.boundHashOps("article:" + article.getId() + ":comments").entries();
        List<Comment> comments = new ArrayList<>();
        commentMap.entrySet().stream().forEach(e -> comments.add(objectMapper.convertValue(e.getValue(), Comment.class)));
        article.setComments(comments);
        return article;
    }

}
