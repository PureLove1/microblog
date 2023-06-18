package com.microblog.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 基于redis原子自增的id生成器
 * @author 贺畅
 */
@Component
public class RedisIdWorker {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据日期时间戳生成id
     * @param KeyPrefix
     * @return
     */
    public long nextId(String KeyPrefix){
        //1.生成时间戳
        //1.1获取当天的0时0分0秒的时间戳
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonth().getValue();
        int day = now.getDayOfMonth();
        long beginTimeStamp = LocalDateTime.of(year, month, day, 0, 0, 0).toEpochSecond(ZoneOffset.UTC);
        long current = now.toEpochSecond(ZoneOffset.UTC);
        long idTimeStamp = current - beginTimeStamp;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy:MM:dd");
        String date = fmt.format(now);
        //2.生成序列号,key不存在会自动创建
        long count = redisTemplate.opsForValue().increment("icr:" + KeyPrefix + ":" + date);
        //3.拼接并返回
        long id = idTimeStamp << 32 | count;
        return id;
    }
}
