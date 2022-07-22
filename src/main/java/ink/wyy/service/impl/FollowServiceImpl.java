package ink.wyy.service.impl;

import com.google.gson.Gson;
import ink.wyy.dao.FollowDao;
import ink.wyy.service.FollowService;

import java.util.HashMap;

public class FollowServiceImpl implements FollowService {

    FollowDao followDao;
    Gson gson;

    public FollowServiceImpl(FollowDao followDao) {
        this.followDao = followDao;
        gson = new Gson();
    }

    @Override
    public String add(Integer userId, Integer followId) {
        Boolean isExist = followDao.exist(userId, followId);
        if (isExist) {
            return "{\"status\":0,\"errMsg\":\"关注已存在\"}";
        }
        Boolean ok = followDao.add(userId, followId);
        if (ok) {
            return "{\"status\":1}";
        } else {
            return "{\"status\":0,\"errMsg\":\"用户不存在\"}";
        }
    }

    @Override
    public String del(Integer userId, Integer followId) {
        Boolean ok = followDao.del(userId, followId);
        if (ok) {
            return "{\"status\":1}";
        } else {
            return "{\"status\":0,\"errMsg\":\"关注不存在\"}";
        }
    }

    @Override
    public String findFans(Integer userId, Integer page, Integer pageSize) {
        HashMap<String, Object> res = followDao.findFans(userId, page, pageSize);
        if (res == null) {
            return "{\"status\":0,\"errMsg\":\"关注不存在\"}";
        }
        return gson.toJson(res);
    }

    @Override
    public String findFollowers(Integer userId, Integer page, Integer pageSize) {
        HashMap<String, Object> res = followDao.findFollowers(userId, page, pageSize);
        if (res == null) {
            return "{\"status\":0,\"errMsg\":\"关注不存在\"}";
        }
        return gson.toJson(res);
    }

    @Override
    public String findFriends(Integer userId) {
        return null;
    }
}
