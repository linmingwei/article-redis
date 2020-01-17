package com.example.redis.util;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * @Auther: mingweilin
 * @Date: 1/17/2020 15:45
 * @Description:
 */
public class Page<T> {
    private int current;
    private int size;
    private long total;
    @JsonIgnore
    private int start;
    @JsonIgnore
    private int end;

    private List<T> records;

    public Page(Integer current, Integer size) {
        this.current = current == null ? 0 : current <= 0 ? 0 : current - 1;
        this.size = size == null ? 10 : size <= 0 ? 10 : size;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getStart() {
        return current * size;
    }

    public int getEnd() {
        return current * size + size - 1;
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
