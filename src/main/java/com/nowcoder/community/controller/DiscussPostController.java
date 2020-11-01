package com.nowcoder.community.controller;

import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
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

    @Autowired
    private LikeService likeService;

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

        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPost.getId());
        //点赞状态
        int likeStatus = hostHolder.getUser()==null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,discussPost.getId());
        model.addAttribute("likeCount",likeCount);
        model.addAttribute("likeStatus",likeStatus);


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

                //点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                //点赞状态
                likeStatus = hostHolder.getUser()==null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                map.put("likeCount",likeCount);
                map.put("likeStatus",likeStatus);

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
                        //点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        //点赞状态
                        likeStatus = hostHolder.getUser()==null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        map1.put("likeCount",likeCount);
                        map1.put("likeStatus",likeStatus);

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
