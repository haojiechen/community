package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);
        user = userMapper.selectByName("liubei");
        System.out.println(user);
        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);

    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("111111@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int row = userMapper.insertUser(user);
        System.out.println(row);
        System.out.println(user.getId());
    }
    @Test
    public void testUpdateUser()
    {
        userMapper.updateStatus(150,1);
        userMapper.updateHeader(150,"http://www.nowcoder.com/102.png");
        userMapper.updatePassword(150,"123");
    }


    @Test
    void selectDiscussPosts() {
        List<DiscussPost> res = discussPostMapper.selectDiscussPosts(149,0,10);
        for (DiscussPost post:res)
        {
            System.out.println(post);
        }
    }


    @Test
    void selectDiscussPostRows() {
        int row = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(row);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("123");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date());

        loginTicketMapper.insertLoginTicket(loginTicket);
    }
    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("123");
        System.out.println(loginTicket.getTicket());
        System.out.println(loginTicket.getStatus());

        loginTicketMapper.updateStatus("123",1);
        loginTicket = loginTicketMapper.selectByTicket("123");
        System.out.println(loginTicket.getStatus());
    }
}
