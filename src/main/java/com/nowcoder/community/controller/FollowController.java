package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author chen
 */
@Controller
public class FollowController {
    @Autowired
    FollowService followService;
    @Autowired
    HostHolder hostHolder;

    @PostMapping("/follow")
    @ResponseBody
    @LoginRequired
    public String follow(int entityType,int entityId){
        User user = hostHolder.getUser();

        followService.follow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"已关注！");
    }
    @PostMapping("/unfollow")
    @ResponseBody
    @LoginRequired
    public String unfollow(int entityType,int entityId){
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"已取消关注！");
    }
}
