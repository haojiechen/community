package com.nowcoder.community.controller;

import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chen
 */
@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;
    @GetMapping("/index")
    public String getIndexPage(Model model, Page page){
        //方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入Model
        //所以在thymeleaf中可以直接访问page
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> discussPost = new ArrayList<>();
        if(list!=null)
        {
            for (DiscussPost post:list){
                Map<String,Object> map = new HashMap<>();
                User use = userService.findUserById(post.getUserId());
                map.put("post",post);
                map.put("user",use);
                Long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                map.put("likeCount",likeCount);
                discussPost.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPost);
        return "index";
    }

    @GetMapping("/error")
    public String getErrorPage(){
        return "/error/500";
    }
}
