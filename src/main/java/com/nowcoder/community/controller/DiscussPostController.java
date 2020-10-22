package com.nowcoder.community.controller;

import com.nowcoder.community.Service.CommentService;
import com.nowcoder.community.Service.DiscussPostService;
import com.nowcoder.community.Service.UserService;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author chen
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if (user == null){
            return CommunityUtil.getJSONString(403,"您还未登录！");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //报错后续会统一处理
        return CommunityUtil.getJSONString(0,"发布成功！");
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int id, Model model, Page page)
    {
        DiscussPost discussPost = discussPostService.findDiscussPostById(id);
        model.addAttribute("post",discussPost);
        model.addAttribute("user",userService.findUserById(discussPost.getUserId()));

        //评论分页信息
        //page.setLimit(5); ???
        page.setPath("/discuss/detail/" + id);
        page.setRows(discussPost.getCommentCount());

        List<Comment> comments =  commentService.findCommentsByEntity(ENTITY_TYPE_POST,discussPost.getId(),page.getOffset(),page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (comments!=null){
            for (Comment comment : comments){
                Map<String ,Object> map = new HashMap<>();
                map.put("comment",comment);
                map.put("user",userService.findUserById(comment.getUserId()));

                //回复
                List<Comment> replys = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);

                List<Map<String,Object>> replysList = new ArrayList<>();
                if (replys!=null){
                    for (Comment reply: replys){
                        Map<String,Object> map1 = new HashMap<>();
                        map1.put("reply",reply);
                        map1.put("user",userService.findUserById(reply.getUserId()));
                        User target = reply.getTargetId() == 0 ? null: userService.findUserById(reply.getTargetId());
                        map1.put("target",target);
                        replysList.add(map1);
                    }
                }
                map.put("replys",replysList);
                //???
                int replyCount = replysList.size();
                map.put("replyCount",replyCount);
                commentVoList.add(map);
            }
        }
        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }
}
