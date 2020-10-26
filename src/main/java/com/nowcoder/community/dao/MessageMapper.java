package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    /**
     * 查询当前的会话列表，每个会话返回最新的一条消息
     * @param userId 用户id
     * @param offset 第几页
     * @param limit 每页的数据条数
     * @return 会话列表
     */
    List<Message> selectConversations(int userId,int offset,int limit);

    /**
     * 查询当前用户的会话数量，为分页显示使用
     * @param userId 用户id
     * @return 会话数量
     */
    int selectConversationCount(int userId);

    /**
     * 查询某一会话中的所有消息
     * @param conversationId 会话ID
     * @param offset 第几页
     * @param limit 每页的条数
     * @return 该会话中的消息列表
     */
    List<Message> selectLetters(String conversationId,int offset,int limit);

    /**
     * 返回某一会话中消息的数量
     * @param conversationId 会话id
     * @return 消息数量
     */
    int selectLetterCount(String conversationId);

    /**
     * 查询未读消息的数量
     * @param userId 用户id
     * @param conversationId 会话id
     * @return 返回该用户/会话的未读消息数量
     */
    int selectLetterUnreadCount(int userId,String conversationId);

    /**
     * 新增一条消息
     * @param message 消息
     * @return 新增行数
     */
    int insertMessage(Message message);

    /**
     * 修改消息状态
     * @param ids 消息id列表
     * @param status 状态
     * @return
     */
    int updateStatus(List<Integer> ids,int status);
}
