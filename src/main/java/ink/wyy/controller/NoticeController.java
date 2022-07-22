package ink.wyy.controller;

import ink.wyy.bean.User;
import ink.wyy.dao.NoticeDao;
import ink.wyy.dao.impl.NoticeDaoImpl;
import ink.wyy.service.NoticeService;
import ink.wyy.service.impl.NoticeServiceImpl;
import ink.wyy.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(value = "/notice/*", loadOnStartup = 1)
public class NoticeController extends HttpServlet {

    private NoticeService noticeService;

    @Override
    public void init() throws ServletException {
        NoticeDao noticeDao = new NoticeDaoImpl();
        noticeService = new NoticeServiceImpl(noticeDao);
        getServletContext().setAttribute("noticeDao", noticeDao);
        getServletContext().setAttribute("noticeService", noticeService);
        System.out.println("Notice Init Over");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, String> request = JsonUtil.jsonToMap(req);
        if (request == null) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"请求有误\"}");
            return;
        }

        String uri = req.getRequestURI();
        switch (uri) {
            case "/notice/read":
                doRead(req, resp, request);
                break;
            case "/notice/delete":
                doDeleteNotice(req, resp, request);
                break;
            default:
                resp.sendError(404);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/notice/getList":
                doGetList(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    private void doRead(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String s_id = request.get("id");
        Integer id = null;
        User user = (User) req.getSession().getAttribute("user");
        if (s_id == null || s_id.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        id = Integer.valueOf(s_id);
        String res = noticeService.read(id, user.getId());
        resp.getWriter().write(res);
    }

    private void doDeleteNotice(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String s_id = request.get("id");
        Integer id = null;
        User user = (User) req.getSession().getAttribute("user");
        if (s_id == null || s_id.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        int userId = user.getId();
        if (user.getLevel() >= 3) userId = -1;
        id = Integer.valueOf(s_id);
        String res = noticeService.delete(id, userId);
        resp.getWriter().write(res);
    }

    private void doGetList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String s_page = req.getParameter("page");
        String s_pageSize = req.getParameter("page_size");
        String order = req.getParameter("order");
        String s_unread = req.getParameter("unread");
        String s_desc = req.getParameter("desc");
        User user = (User) req.getSession().getAttribute("user");

        int page = 1;
        int pageSize = 20;
        boolean desc = false;
        if (s_page != null && !s_page.equals("")) {
            page = Integer.parseInt(s_page);
        }
        if (s_pageSize != null && !s_pageSize.equals("")) {
            pageSize = Integer.parseInt(s_pageSize);
        }
        if (s_desc != null && !s_desc.equals("")) {
            if (s_desc.equals("true")) {
                desc = true;
            }
        }
        String res = noticeService.getList(page, pageSize, order, s_unread, desc, user.getId());
        resp.getWriter().write(res);
    }
}
