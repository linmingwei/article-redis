package com.example.redis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Auther: mingweilin
 * @Date: 1/16/2020 12:14
 * @Description:
 */
@Data
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
public class BaseEntity {
    //    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;
    //    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;

    private Integer timestamps;


    @JsonIgnore
    private Integer current = 0;
    @JsonIgnore
    private Integer size = 10;

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current * size;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = current * (size + 1) - 1;
    }

    public BaseEntity() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
//        this.timestamps = (int)Math.ceil((double)System.currentTimeMillis()/1000L);
    }
}
