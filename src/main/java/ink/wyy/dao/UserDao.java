package ink.wyy.dao;

import ink.wyy.bean.User;

import java.util.HashMap;

public interface UserDao {
    String insert(User user);
    String delete(int id);
    String update(Integer id, User user);
    String updateUserInfo(Integer id, User user);
    User findByUsername(String username);
    User findById(int id);
    HashMap<String, Object> getUserList(int page, int pageSize, String order, boolean desc);
}
