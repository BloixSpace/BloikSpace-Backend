package ink.wyy.controller;

import com.google.gson.Gson;
import ink.wyy.bean.User;
import ink.wyy.dao.FollowDao;
import ink.wyy.dao.FollowDaoImpl;
import ink.wyy.service.FollowService;
import ink.wyy.service.FollowServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

@WebServlet(value = "/follow/*", loadOnStartup = 1)
public class FollowController extends HttpServlet {

    FollowService followService;
    Gson gson;

    @Override
    public void init() throws ServletException {
        FollowDao followDao = new FollowDaoImpl("localhost:3306", "noixForum", "wyy", "qwqwq123");
        followService = new FollowServiceImpl(followDao);
        gson = new Gson();
        System.out.println("FollowController Init Over");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/follow/findFans":
                doFind(req, resp, "fan");
                break;
            case "/follow/findFollowers":
                doFind(req, resp, "follow");
                break;
            default:
                resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/follow/add":
                doAddDel(req, resp, "add");
                break;
            case "/follow/delete":
                doAddDel(req, resp, "del");
                break;
            default:
                resp.sendError(404);
        }
    }

    private void doAddDel(HttpServletRequest req, HttpServletResponse resp, String mode) throws ServletException, IOException {
        String follow_id = req.getParameter("user_id");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        HashMap<String, Object> res = new HashMap<>();
        if (follow_id == null || follow_id.equals("")) {
            res.put("status", 0);
            res.put("errMsg", "id不能为空");
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        Integer userId = user.getId();
        Integer followId = Integer.valueOf(follow_id);

        String s;
        if (mode.equals("add")) s = followService.add(userId, followId);
        else s = followService.del(userId, followId);
        resp.getWriter().write(s);
    }

    private void doFind(HttpServletRequest req, HttpServletResponse resp, String mode) throws ServletException, IOException {
        String user_id = req.getParameter("user_id");
        String s_page = req.getParameter("page");
        String page_size = req.getParameter("page_size");

        HashMap<String, Object> res = new HashMap<>();
        if (user_id == null || user_id.equals("")) {
            res.put("status", 0);
            res.put("errMsg", "id不能为空");
            resp.getWriter().write(gson.toJson(res));
            return;
        }
        Integer userId = Integer.valueOf(user_id);
        Integer page = 1;
        Integer pageSize = 20;
        if (s_page != null && !s_page.equals("")) {
            page = Integer.valueOf(s_page);
        }
        if (page_size != null && !page_size.equals("")) {
            pageSize = Integer.valueOf(page_size);
        }

        String s;
        if(mode.equals("fan")) s = followService.findFans(userId, page, pageSize);
        else s = followService.findFollowers(userId, page, pageSize);
        resp.getWriter().write(s);
    }
}
