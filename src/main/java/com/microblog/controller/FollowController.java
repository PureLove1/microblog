package com.microblog.controller;

import com.microblog.annotation.HasAnyRole;
import com.microblog.common.Result;
import com.microblog.service.FollowService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.microblog.constant.UserRole.ROLE_USER;

/**
 * @author 贺畅
 * @date 2022/11/28
 */
@RestController
@RequestMapping("/follow")
public class FollowController {
    @Resource
    private FollowService followService;

    /**
     * 实现关注和取关
     * @param id 被关注或被取关的id，用户的id从ThreadLocal获取
     * @param isFollow 关注还是取关操作
     * @return 操作结果
     */
    @PutMapping("/{id}/{isFollow}")
    @HasAnyRole(ROLE_USER)
    public Result follow(@PathVariable Long id, @PathVariable Boolean isFollow){
        return followService.follow(id, isFollow);
    }


    /**
     * 用于判断某用户是否被当前用户关注
     * @param id
     * @return
     */
    @Deprecated
    @GetMapping("/or/not/{id}")
    public Result followOrNot(@PathVariable Long id){
        return followService.followOrNot(id);
    }

    /**
     * 用于查询用户关注列表
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @HasAnyRole({ROLE_USER})
    public Result listAllFollowUsers(@PathVariable Long id){
        return followService.listAllFollowUsers(id);
    }


    /**
     * 用于查询用户粉丝列表
     * @param id
     * @return
     */
    @GetMapping("/fan/{id}")
    @HasAnyRole({ROLE_USER})
    public Result listAllFanUsers(@PathVariable Long id){
        return followService.listAllFanUsers(id);
    }
}
