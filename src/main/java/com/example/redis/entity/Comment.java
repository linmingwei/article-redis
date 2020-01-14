package com.example.redis.entity;

import lombok.Data;

/**
 * @Auther: mingweilin
 * @Date: 1/10/2020 14:36
 * @Description:
 */
@Data
public class Comment {
    private Long id;
    private Long articleId;
    private String user;
    private String contact;
    private String content;
    private Long pid;
    //审核状态
    private Long status;

}
