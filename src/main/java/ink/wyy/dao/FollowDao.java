package ink.wyy.dao;

import java.util.HashMap;
import java.util.List;

public interface FollowDao {
    Boolean add(Integer userId, Integer followId);
    Boolean del(Integer userId, Integer followId);
    // 查询我的粉丝
    HashMap<String, Object> findFans(Integer userId, Integer page, Integer pageSize);
    // 查询我关注的
    HashMap<String, Object> findFollowers(Integer userId, Integer page, Integer pageSize);
    // 查询互关
    HashMap<String, Object> findFriends(Integer userId, Integer page, Integer pageSize);
    Boolean exist(Integer userId, Integer followId);
}
