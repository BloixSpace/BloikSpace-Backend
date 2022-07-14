package ink.wyy.controller;

import com.google.gson.Gson;
import ink.wyy.bean.Commodity;
import ink.wyy.bean.User;
import ink.wyy.dao.CommodityDao;
import ink.wyy.dao.CommodityDaoImpl;
import ink.wyy.service.CommodityService;
import ink.wyy.service.CommodityServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(value = "/commodity/*", loadOnStartup = 1)
public class CommodityController extends HttpServlet {

    CommodityService commodityService;
    Gson gson;

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        CommodityDao commodityDao = new CommodityDaoImpl();
        context.setAttribute("commodityDao", commodityDao);
        commodityService = new CommodityServiceImpl(commodityDao);
        context.setAttribute("commodityService", commodityService);
        gson = new Gson();
        System.out.println("CommodityController Init Over");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/commodity/add":
                doAdd(req, resp);
                break;
            case "/commodity/update":
                doUpdate(req, resp);
                break;
            case "/commodity/delete":
                doDeleteCommodity(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/commodity/get":
                doGetCommodity(req, resp);
                break;
            case "/commodity/list":
                doGetList(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    private void doGetCommodity(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String res = commodityService.get(id);
        resp.getWriter().write(res);
    }

    private void doAdd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String category = req.getParameter("category");
        String picUri = req.getParameter("pic");
        String s_price = req.getParameter("price");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        Commodity commodity = new Commodity(title, content, category);
        commodity.setUserId(user.getId());
        commodity.setPicUri(picUri);
        if (s_price != null) {
            commodity.setPrice(Double.valueOf(s_price));
        }

        String res = commodityService.add(commodity);
        resp.getWriter().write(res);
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        Integer userId = user.getId();
        String res = commodityService.update(req, userId);
        resp.getWriter().write(res);
    }

    private void doDeleteCommodity(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        Integer userId = user.getId();
        String msg = commodityService.delete(id, userId);
        resp.getWriter().write(msg);
    }

    private void doGetList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String s_page = req.getParameter("page");
        String s_pageSize = req.getParameter("page_size");
        String s_userId = req.getParameter("user_id");
        String order = req.getParameter("order");
        String category = req.getParameter("category");
        int page = 1;
        int pageSize = 20;
        Integer userId = null;
        if (s_page != null && !s_page.equals("")) {
            page = Integer.parseInt(s_page);
        }
        if (s_pageSize != null && !s_pageSize.equals("")) {
            pageSize = Integer.parseInt(s_pageSize);
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
        resp.getWriter().write(commodityService.getList(page, pageSize, order, category, userId));
    }
}
