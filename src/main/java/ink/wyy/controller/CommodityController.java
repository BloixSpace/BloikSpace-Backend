package ink.wyy.controller;

import com.google.gson.Gson;
import ink.wyy.bean.Commodity;
import ink.wyy.bean.User;
import ink.wyy.dao.CommodityDao;
import ink.wyy.dao.impl.CommodityDaoImpl;
import ink.wyy.service.CommodityService;
import ink.wyy.service.impl.CommodityServiceImpl;
import ink.wyy.util.JsonUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        HashMap<String, String> request = JsonUtil.jsonToMap(req);
        if (request == null) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"请求有误\"}");
            return;
        }

        String uri = req.getRequestURI();
        switch (uri) {
            case "/commodity/add":
                doAdd(req, resp, request);
                break;
            case "/commodity/update":
                doUpdate(req, resp, request);
                break;
            case "/commodity/delete":
                doDeleteCommodity(req, resp, request);
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

    private void doAdd(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String title = request.get("title");
        String content = request.get("content");
        String category = request.get("category");
        String picUri = request.get("pic");
        String s_price = request.get("price");
        String s_stock = request.get("stock");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        Commodity commodity = new Commodity(title, content, category);
        commodity.setUserId(user.getId());
        commodity.setPicUri(picUri);
        commodity.setPrice(Double.valueOf(s_price));
        commodity.setStock(Integer.valueOf(s_stock));
        if (s_price != null) {
            commodity.setPrice(Double.valueOf(s_price));
        }

        String res = commodityService.add(commodity);
        resp.getWriter().write(res);
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        Integer userId = user.getId();
        String res = commodityService.update(request, userId);
        resp.getWriter().write(res);
    }

    private void doDeleteCommodity(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
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
        String key = req.getParameter("key");
        String s_desc = req.getParameter("desc");
        int page = 1;
        int pageSize = 20;
        Integer userId = null;
        Boolean desc = null;
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
        if (s_desc != null && !s_desc.equals("")) {
            desc = s_desc.equals("true");
        }
        resp.getWriter().write(commodityService.getList(page, pageSize, order, category, key, userId, desc));
    }
}
