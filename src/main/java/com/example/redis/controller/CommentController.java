package com.example.redis.controller;

import com.example.redis.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("")
    public String add(Comment comment) {
        if (comment.getArticleId() == null || StringUtils.isEmpty(comment.getContent())) {
            return null;
        }
        Object aid = redisTemplate.opsForHash().get("article:" + comment.getArticleId(), "id");
        if(Objects.isNull(aid)) return "文章不存在";
        Long id = redisTemplate.opsForValue().increment("comment:count");
        comment.setId(id);

        redisTemplate.opsForHash().put("article:" + comment.getArticleId() + ":comments", id.toString(), comment);
        return "comment id: " + id;
    }

    @DeleteMapping("/{articleId}/{id}")
    public String delete(@PathVariable Long id, @PathVariable Long articleId) {
        redisTemplate.opsForHash().delete("article:" + articleId.toString() + ":comments", id.toString());
        return "delete success";
    }
}
