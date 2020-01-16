package com.example.redis.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * @Auther: mingweilin
 * @Date: 1/10/2020 14:36
 * @Description:
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
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
