package ink.wyy.service;

import com.google.gson.Gson;
import ink.wyy.bean.User;
import ink.wyy.dao.UserDao;
import ink.wyy.util.MD5Util;

import java.util.HashMap;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final Gson gson;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
        gson = new Gson();
    }

    @Override
    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setErrorMsg("用户名或密码错误");
            return user;
        }
        if (!user.getPassword().equals(password)) {
            user.setErrorMsg("用户名或密码错误");
            return user;
        }
        return user;
    }

    @Override
    public User register(String username, String password) {
        if (userDao.findByUsername(username) != null) {
            User user = new User();
            user.setErrorMsg("用户名已被使用");
            return user;
        }
        User user = new User(username, password);
        user.setSignature("这个用户太懒，还没有签名。");
        user.setAvatarUri("/img/logo.jpg");
        user.setLevel(0);
        String errorMsg = userDao.insert(user);
        user = login(username, password);
        user.setErrorMsg(errorMsg);
        return user;
    }

    @Override
    public User UserSetUserInfo(User user) {
        String signature = user.getSignature();
        Integer id = user.getId();
        String avatarUri = user.getAvatarUri();
        Integer level = user.getLevel();
        user = userDao.findById(id);
        if (signature != null && !signature.equals("")) {
            user.setSignature(signature);
        }
        if (avatarUri != null && !avatarUri.equals("")) {
            user.setAvatarUri(avatarUri);
        }
        if (user.getLevel() != 0 && level != null) {
            user.setErrorMsg("角色不能更改");
            return user;
        }
        if (user.getLevel() == 0 && level == null) {
            user.setErrorMsg("请设置角色");
            return user;
        }
        if (level != null) {
            user.setLevel(level);
        }
        userDao.updateUserInfo(id, user);
        return user;
    }

    @Override
    public User AdminSetUserInfo(Map<String, String> req) {
        String sss = req.get("id");
        Integer id = null;
        if (sss != null && !sss.equals("")) {
            id = Integer.valueOf(req.get("id"));
        }
        if (id == null || id.equals(0)) {
            User user = new User();
            user.setErrorMsg("id不能为空");
            return user;
        }
        User newUser = userDao.findById(id);
        if (newUser == null) {
            newUser = new User();
            newUser.setErrorMsg("用户不存在");
            return newUser;
        }
        String username = req.get("username");
        String password = req.get("password");
        password = MD5Util.getMD5Str(password, "b1oikSpace");
        String avatarUri = req.get("avatarUri");
        String signature = req.get("signature");
        Integer level = null;
        if (req.get("level") != null && !req.get("level").equals("")) {
            level = Integer.valueOf(req.get("level"));
        }
        if (username != null && !username.equals("")) {
            newUser.setUsername(username);
        }
        if (password != null && !password.equals("")) {
            newUser.setPassword(password);
        }
        if (avatarUri != null && !avatarUri.equals("")) {
            newUser.setAvatarUri(avatarUri);
        }
        if (signature != null && !signature.equals("")) {
            newUser.setSignature(signature);
        }
        if (level != null) {
            newUser.setLevel(level);
        }
        userDao.update(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User getUser(User user) {
        if (user.getId() != null) {
            user = userDao.findById(user.getId());
        } else if (user.getUsername() != null) {
            user = userDao.findByUsername(user.getUsername());
        }
        return user;
    }

    @Override
    public User addUser(Map<String, String> req) {
        String username = req.get("username");
        if (username == null || username.equals("")) {
            User user = new User();
            user.setErrorMsg("用户名不能为空");
            return user;
        }
        String password = req.get("password");
        if (password == null || password.equals("")) {
            password = "12345678";
        }
        User user = register(username, password);
        if (user.getErrorMsg() != null) {
            return user;
        }
        req.put("id", user.getId().toString());
        user = AdminSetUserInfo(req);
        return user;
    }

    @Override
    public String delUser(Integer id) {
        User user = userDao.findById(id);
        if (user == null) {
            return "用户不存在";
        }
        String msg = userDao.delete(id);
        return msg;
    }

    @Override
    public User updatePassword(User user, String newPwd, String oldPwd) {
        user = userDao.findById(user.getId());
        if (oldPwd == null || !oldPwd.equals(user.getPassword())) {
            user.setErrorMsg("密码错误");
            return user;
        }
        user.setPassword(newPwd);
        String msg = userDao.update(user.getId(), user);
        if (msg != null) {
            user.setErrorMsg(msg);
        }
        return user;
    }

    @Override
    public String getUserList(int page, int pageSize, String order, boolean desc) {
        HashMap<String, Object> res = userDao.getUserList(page, pageSize, order, desc);
        if (res == null) {
            res = new HashMap<>();
            res.put("status", 0);
            res.put("errMsg", "order不存在");
        }
        return gson.toJson(res);
    }
}
