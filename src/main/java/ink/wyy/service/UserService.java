package ink.wyy.service;

import ink.wyy.bean.User;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    User login(String username, String password);
    User register(String username, String password);
    User UserSetUserInfo(User user);
    User AdminSetUserInfo(HttpServletRequest req);
    User getUser(User user);
    User addUser(HttpServletRequest req);
    String delUser(Integer id);
    User updatePassword(User user, String newPwd, String oldPwd);
}
