package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * @author chen
 */
@Service
public class AlphaService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    AlphaService()
    {
        System.out.println("实例化AlphaService");
    }
    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    //REQUIRED : 支持外部事务，如果不存在则创建新事务（默认）
    //REQUIRES_NEW : 挂起外部事务，创建一个新事务
    //NESTED : 如果当前存在外部事务，则嵌套在该事务中执行，否则和REQUIRED一样
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED )
    public Object save1(){
        User user = new User();
        user.setUsername("Alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
        user.setEmail("Alpha@qq.com");
        user.setHeaderUrl("xxx.xx.com");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("新人报道！");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");
        return "ok";
    }

    @Autowired
    private TransactionTemplate transactionTemplate;

    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(transactionStatus -> {
            User user = new User();
            user.setUsername("Alpha");
            user.setSalt(CommunityUtil.generateUUID().substring(0,5));
            user.setPassword(CommunityUtil.md5("123"+user.getSalt()));
            user.setEmail("Alpha@qq.com");
            user.setHeaderUrl("xxx.xx.com");
            user.setCreateTime(new Date());
            userMapper.insertUser(user);

            DiscussPost post = new DiscussPost();
            post.setUserId(user.getId());
            post.setTitle("Hello");
            post.setContent("新人报道！");
            post.setCreateTime(new Date());
            discussPostMapper.insertDiscussPost(post);

            Integer.valueOf("abc");
            return "ok";
        });
    }


}
