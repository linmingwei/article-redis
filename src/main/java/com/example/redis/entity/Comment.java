package com.example.redis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * @Auther: mingweilin
 * @Date: 1/10/2020 14:36
 * @Description:
 */
@Data
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
public class Comment extends BaseEntity {
    private Long id;
    private Long articleId;
    private String user;
    private String contact;
    private String content;
    private Long pid;
    //审核状态
    private Long status;

}
