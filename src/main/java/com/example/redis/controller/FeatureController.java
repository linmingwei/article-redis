package com.example.redis.controller;

import cn.hutool.extra.mail.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: mingweilin
 * @Date: 1/16/2020 16:33
 * @Description:
 */
@RestController
public class FeatureController {
    @Autowired
    private RedisTemplate redisTemplate;

    private static final Pattern VALID_EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",Pattern.CASE_INSENSITIVE);

    @PostMapping("/subscribe")
    public String emailSubscribe(String email) throws UnsupportedEncodingException {
        String base64Email = Base64.getEncoder().encodeToString(email.getBytes("utf-8"));
        String confirmLink = "http://localhost:8080/emailConfirm?code="+base64Email;

        if (!StringUtils.isEmpty(email) && validate(email)) {
            MailUtil.send("linmingwei100@gmail.com","test","<h1>测试确认邮件</h1>" +
                    "<p><a href='"+confirmLink+"'>"+confirmLink+"</a>",true);
            return "确认邮件发送成功，请查收";
        }
        return "邮箱有误，请重新填写";
    }

    @GetMapping("/emailConfirm")
    public String emailConfirm(String code) throws UnsupportedEncodingException {
        String email = new String(Base64.getDecoder().decode(code),"utf-8");
        redisTemplate.opsForSet().add("queue:email:notification",email);
        return "邮箱添加成功";
    }
    private boolean validate(String email){
        Matcher matcher = VALID_EMAIL_REGEX.matcher(email);
        return matcher.find();
    }
}
