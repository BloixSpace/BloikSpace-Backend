package ink.wyy.controller;

import com.google.gson.Gson;
import ink.wyy.bean.Article;
import ink.wyy.bean.User;
import ink.wyy.dao.ArticleDao;
import ink.wyy.dao.ArticleDaoImpl;
import ink.wyy.service.ArticleService;
import ink.wyy.service.ArticleServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;

@WebServlet(value = "/article/*", loadOnStartup = 1)
public class ArticleController extends HttpServlet {

    ArticleService articleService;
    Gson gson;

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        ArticleDao articleDao = new ArticleDaoImpl("localhost:3306", "noixforum", "wyy", "wyy20020929");
        context.setAttribute("articleDao", articleDao);
        articleService = new ArticleServiceImpl(articleDao);
        context.setAttribute("articleService", articleService);
        gson = new Gson();
        System.out.println("ArticleController Init Over");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/article/add":
                doAdd(req, resp);
                break;
            case "/article/update":
                doUpdate(req, resp);
                break;
            case "/article/delete":
                doDeleteArticle(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/article/get":
                doGetArticle(req, resp);
                break;
            case "/article/list":
                doGetList(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    private void doGetArticle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
//        JSONObject res = new JSONObject();
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
        res.put("status", 1);
        res.put("id", article.getId());
        res.put("title", article.getTitle());
        res.put("content", article.getContent());
        res.put("category", article.getCategory());
        res.put("user_id", article.getUserId());
        res.put("release_time", article.getCreateDate());
        res.put("update_time", article.getUpdateDate());
        String s_res = gson.toJson(res);
        resp.getWriter().write(s_res);
    }

    private void doAdd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String category = req.getParameter("category");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        Article article = new Article(title, content, category);
        article.setUserId(user.getId());

        article = articleService.add(article);
        HashMap<String, Object> res = new HashMap<>();
        if (article.getErrorMsg() != null) {
            res.put("status", 0);
            res.put("errMsg", article.getErrorMsg());
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        res.put("status", 1);
        res.put("id", article.getId());
        resp.getWriter().write(gson.toJson(res));
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        Integer userId = user.getId();
        HashMap<String, Object> res = new HashMap<>();
        String msg = articleService.update(req, userId);
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
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        Integer userId = user.getId();
        HashMap<String, Object> res = new HashMap<>();
        if (id == null || id.equals("")) {
            res.put("status", 0);
            res.put("errMsg", "id不能为空");
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        String msg = articleService.delete(Integer.valueOf(id), userId);
        if (msg != null) {
            res.put("status", 0);
            res.put("errMsg", msg);
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        res.put("status", 1);
        resp.getWriter().write(gson.toJson(res));
    }

    private void doGetList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String s_page = req.getParameter("page");
        String s_pageSize = req.getParameter("page_size");
        String s_userId = req.getParameter("user_id");
        String order = req.getParameter("order");
        String category = req.getParameter("category");
        Integer page = 1;
        Integer pageSize = 20;
        Integer userId = null;
        if (s_page != null && !s_page.equals("")) {
            page = Integer.valueOf(s_page);
        }
        if (s_pageSize != null && !s_pageSize.equals("")) {
            pageSize = Integer.valueOf(s_pageSize);
        }
        if (s_userId != null && !s_userId.equals("")) {
            userId = Integer.valueOf(s_userId);
        }
        if (order == null || order.equals("")) {
            order = "update_time";
        }
        if (category == null || category.equals("")) {
            category = null;
        }
        resp.getWriter().write(articleService.getList(page, pageSize, order, category, userId));
    }
}
