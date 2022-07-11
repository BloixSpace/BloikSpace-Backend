package ink.wyy.service;

import java.util.List;

public interface FollowService {
    String add(Integer userId, Integer followId);
    String del(Integer userId, Integer followId);
    String findFans(Integer userId, Integer page, Integer pageSize);
    String findFollowers(Integer userId, Integer page, Integer pageSize);
    String findFriends(Integer userId);
}
