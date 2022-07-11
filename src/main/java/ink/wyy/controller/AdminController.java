package ink.wyy.controller;

import com.google.gson.Gson;
import ink.wyy.bean.Article;
import ink.wyy.bean.User;
import ink.wyy.service.ArticleService;
import ink.wyy.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {

    UserService userService;
    ArticleService articleService;
    Gson gson;

    @Override
    public void init() throws ServletException {
        userService = (UserService) getServletContext().getAttribute("userService");
        articleService = (ArticleService) getServletContext().getAttribute("articleService");
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/admin/setUserInfo":
                doSetUserInfo(req, resp);
                break;
            case "/admin/addUser":
                doAddUser(req, resp);
                break;
            case "/admin/deleteUser":
                doDeleteUser(req, resp);
                break;
            case "/admin/deleteArticle":
                doDeleteArticle(req, resp);
                break;
        }
    }

    private void doSetUserInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = userService.AdminSetUserInfo(req);

        HashMap<String, Object> res = new HashMap<>();

        if (user.getErrorMsg() != null) {
            res.put("status", 0);
            res.put("errMsg", user.getErrorMsg());
            resp.getWriter().write(gson.toJson(res));
            return;
        }

        res.put("status", 1);
        resp.getWriter().write(gson.toJson(res));
    }

    private void doAddUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = userService.addUser(req);

        HashMap<String, Object> res = new HashMap<>();
        if (user.getErrorMsg() != null) {
            res.put("status", 0);
            res.put("errMsg", user.getErrorMsg());
            resp.getWriter().write(gson.toJson(res));
            return;
        }

        res.put("status", 1);
        res.put("id", user.getId());
        resp.getWriter().write(gson.toJson(res));
    }

    private void doDeleteUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        HashMap<String, Object> res = new HashMap<>();
        if (id == null || id.equals("")) {
            res.put("status", 0);
            res.put("errMsg", "id不能为空");
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        String msg = userService.delUser(Integer.valueOf(id));
        if (msg != null) {
            res.put("status", 0);
            res.put("errMsg", msg);
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        res.put("status", 1);
        resp.getWriter().write(gson.toJson(res));
    }

    private void doDeleteArticle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        HashMap<String, Object> res = new HashMap<>();
        if (id == null || id.equals("")) {
            res.put("status", 0);
            res.put("errMsg", "id不能为空");
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        Article article = articleService.get(Integer.valueOf(id));
        if (article.getErrorMsg() != null) {
            res.put("status", 0);
            res.put("errMsg", article.getErrorMsg());
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        String msg = articleService.delete(article.getId(), article.getUserId());
        if (msg != null) {
            res.put("status", 0);
            res.put("errMsg", msg);
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        res.put("status", 1);
        resp.getWriter().write(gson.toJson(res));
    }
}
