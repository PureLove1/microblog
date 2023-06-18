package com.microblog.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 验证码生成
 * @author 贺畅
 */
public class RandomCodeUtil {

    /**
     * 生成随机验证码
     * @return
     */
    public static String randomCode(){
        // 避免 Random 实例被多线程使用，虽然共享该实例是线程安全的，但会因竞争同一 seed导致的性能下降
        int i = ThreadLocalRandom.current().nextInt(0, 999999);
        return String.valueOf(i);
    }

}
