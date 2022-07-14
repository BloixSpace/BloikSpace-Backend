package ink.wyy.controller;

import com.google.gson.Gson;
import ink.wyy.bean.User;
import ink.wyy.dao.UserDao;
import ink.wyy.dao.UserDaoImpl;
import ink.wyy.service.UserService;
import ink.wyy.service.UserServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@WebServlet(value = "/user/*", loadOnStartup = 1)
public class UserController extends HttpServlet {

    UserService userService;
    Gson gson;

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        UserDao userDao = new UserDaoImpl();
        context.setAttribute("userDao", userDao);
        userService = new UserServiceImpl(userDao);
        context.setAttribute("userService", userService);
        gson = new Gson();
        System.out.println("UserController Init Over");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/user/login":
                doLogin(req, resp);
                break;
            case "/user/register":
                doRegister(req, resp);
                break;
            case "/user/setUserInfo":
                doSetUserInfo(req, resp);
                break;
            case "/user/updatePassword":
                doUpdatePassword(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/user/logout":
                doLogout(req, resp);
                break;
            case "/user/getUserInfo":
                doGetUserInfo(req, resp);
                break;
            case "/user/getUserList":
                doGetUserList(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    private void doLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        HttpSession session = req.getSession();
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        HashMap<String, Object> res = new HashMap<>();

        if (session.getAttribute("user") != null) {
            res.put("status", 0);
            res.put("errMsg", "已登录");
            writer.write(gson.toJson(res));
            // {"status":0,"errMsg":"已登录"}
            return;
        }
        if (username == null || username.equals("") ||
                password == null || password.equals("")) {
            res.put("status", 0);
            res.put("errMsg", "用户名和密码不能为空");
            writer.write(gson.toJson(res));
            return;
        }
        User user = userService.login(username, password);
        if (user.getErrorMsg() != null) {
            res.put("status", 0);
            res.put("errMsg", user.getErrorMsg());
            writer.write(gson.toJson(res));
            return;
        }
        session.setAttribute("user", user);
        res.put("status", 1);
        res.put("id", user.getId());
        writer.write(gson.toJson(res));
    }

    private void doRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        HttpSession session = req.getSession();
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        HashMap<String, Object> res = new HashMap<>();

        if (session.getAttribute("user") != null) {
            res.put("status", 0);
            res.put("errMsg", "已登录");
            writer.write(gson.toJson(res));
            return;
        }
        if (username == null || username.equals("") ||
                password == null || password.equals("")) {
            res.put("status", 0);
            res.put("errMsg", "用户名和密码不能为空");
            writer.write(gson.toJson(res));
            return;
        }
        User user = userService.register(username, password);
        if (user.getErrorMsg() != null) {
            res.put("status", 0);
            res.put("errMsg", user.getErrorMsg());
            writer.write(gson.toJson(res));
            return;
        }
        session.setAttribute("user", user);
        res.put("status", 1);
        res.put("id", user.getId());
        writer.write(gson.toJson(res));
    }

    private void doGetUserInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        PrintWriter writer = resp.getWriter();
        HashMap<String, Object> res = new HashMap<>();

        res.put("status", 1);
        res.put("avatarUri", user.getAvatarUri());
        res.put("signature", user.getSignature());
        res.put("username", user.getUsername());
        res.put("id", user.getId());
        res.put("createDate", user.getCreateDate().toString());
        res.put("level", user.getLevel());
        writer.write(gson.toJson(res));
    }

    private void doLogout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("user") != null) {
            session.removeAttribute("user");
        }
        resp.sendRedirect("/");
    }

    private void doSetUserInfo(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        user.setSignature(req.getParameter("signature"));
        user.setAvatarUri(req.getParameter("avatarUri"));
        user = userService.UserSetUserInfo(user);
        session.setAttribute("user", user);

        HashMap<String, Object> res = new HashMap<>();
        res.put("status", 1);
        resp.getWriter().write(gson.toJson(res));
    }

    private void doUpdatePassword(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException {
        String oldPwd = req.getParameter("oldPassword");
        String newPwd = req.getParameter("newPassword");

        HashMap<String, Object> res = new HashMap<>();
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            res.put("status", 0);
            res.put("errMsg", "未登录");
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        user = userService.updatePassword(user, newPwd, oldPwd);
        if (user.getErrorMsg() != null) {
            res.put("status", 0);
            res.put("errMsg", user.getErrorMsg());
            user.setErrorMsg(null);
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        session.setAttribute("user", user);
        res.put("status", 1);
        resp.getWriter().write(gson.toJson(res));
    }

    private void doGetUserList(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException {
        String s_page = req.getParameter("page");
        String s_pageSize = req.getParameter("page_size");
        String order = req.getParameter("order");
        String s_desc = req.getParameter("desc");

        Boolean desc = false;
        if (s_page == null || s_page.equals("")) {
            s_page = "1";
        }
        if (s_pageSize == null || s_pageSize.equals("")) {
            s_pageSize = "20";
        }
        if (s_desc == null || s_desc.equals("")) {
            s_desc = "false";
        }
        if (order == null || order.equals("")) {
            order = "username";
        }
        Integer page = Integer.valueOf(s_page);
        Integer pageSize = Integer.valueOf(s_pageSize);
        if (s_desc.equals("true")) desc = true;

        String res = userService.getUserList(page, pageSize, order, desc);
        resp.getWriter().write(res);
    }
}
