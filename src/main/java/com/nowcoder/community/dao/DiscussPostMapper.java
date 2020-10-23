package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chen
 */
@Mapper
public interface DiscussPostMapper {


    /**
     * 查找所有用户的帖子或对应userId用户的帖子
     * @param userId 等于0时返回所有用户的帖子
     * @param offset 分页起始行的行号
     * @param limit 每一页的帖子个数
     * @return 返回所有用户的帖子或对应userId用户的帖子
     */
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    //@Param注解用于给参数起别名，当只有一个参数f并且需要在<if>里使用，则必须加别名 ?
    /**
     * 查询帖子的总数量
     * @param userId 等于0时返回所有用户的帖子数量
     * @return 返回所有用户的帖子总数或对应userId用户的帖子数量
     */
    int selectDiscussPostRows(int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);
}
