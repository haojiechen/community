package com.nowcoder.community.util;

/**
 * @author chen
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";

    /**
     * 生成某个实体赞的key
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 对应实体赞的key
     */
    public static final String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }

    /**
     * 某个用户关注的实体
     * followee:userId:entityType -> zset(entityId,now)
     * @param userId 用户id
     * @param entityType 实体类型
     * @return key
     */
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    /**
     * 某个实体拥有的粉丝
     * follower:entityId:entityType -> zset(userId,now)
     * @param entityId 用户id
     * @param entityType 实体类型
     * @return key
     */
    public static String getFollowerKey(int entityId,int entityType){
        return PREFIX_FOLLOWER+SPLIT+entityId+SPLIT+entityType;
    }
}
