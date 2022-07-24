package ink.wyy.service;

import ink.wyy.bean.User;

import java.util.Map;

public interface UserService {
    User login(String username, String password);
    User register(String username, String password);
    User UserSetUserInfo(User user);
    User AdminSetUserInfo(Map<String, String> req);
    String getUser(User user);
    User addUser(Map<String, String> req);
    String delUser(Integer id);
    User updatePassword(User user, String newPwd, String oldPwd);
    String getUserList(int page, int pageSize, String order, boolean desc);
}
