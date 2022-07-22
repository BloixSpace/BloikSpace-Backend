package ink.wyy.controller;

import com.mysql.cj.result.IntegerValueFactory;
import com.sun.org.apache.xpath.internal.operations.Bool;
import ink.wyy.bean.Star;
import ink.wyy.bean.User;
import ink.wyy.dao.ReviewDao;
import ink.wyy.dao.StarDao;
import ink.wyy.dao.impl.ReviewDaoImpl;
import ink.wyy.dao.impl.StarDaoImpl;
import ink.wyy.service.StarService;
import ink.wyy.service.impl.ReviewServiceImpl;
import ink.wyy.service.impl.StarServiceImpl;
import ink.wyy.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/star/*")
public class StarController extends HttpServlet {

    private StarService starService;

    @Override
    public void init() throws ServletException {
        StarDao starDao = new StarDaoImpl();
        starService = new StarServiceImpl(starDao);
        getServletContext().setAttribute("starDao", starDao);
        getServletContext().setAttribute("starService", starService);
        System.out.println("Star Init Over");
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
            case "/star/add":
                doAdd(req, resp, request);
                break;
            case "/star/delete":
                doUnstar(req, resp, request);
                break;
            default:
                resp.sendError(404);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/star/num":
                doNum(req, resp);
                break;
            case "/star/list":
                doList(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    private void doAdd(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String s_commodityId = request.get("commodity_id");
        if (s_commodityId == null || s_commodityId.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        Integer commodityId = Integer.valueOf(s_commodityId);
        User user = (User) req.getSession().getAttribute("user");
        Star star = new Star();
        star.setUserId(user.getId());
        star.setCommodityId(commodityId);
        String msg = starService.add(star);
        resp.getWriter().write(msg);
    }

    private void doUnstar(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String s_commodityId = request.get("commodity_id");
        if (s_commodityId == null || s_commodityId.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        Integer commodityId = Integer.valueOf(s_commodityId);
        User user = (User) req.getSession().getAttribute("user");
        String msg = starService.delete(commodityId, user.getId());
        resp.getWriter().write(msg);
    }

    private void doNum(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String s_commodityId = req.getParameter("commodity_id");
        if (s_commodityId == null || s_commodityId.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        Integer commodityId = Integer.valueOf(s_commodityId);
        String msg = starService.getNumByCommodityId(commodityId);
        resp.getWriter().write(msg);
    }

    private void doList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String s_page = req.getParameter("page");
        String s_pageSize = req.getParameter("page_size");
        String s_commodityId = req.getParameter("commodity_id");
        String order = req.getParameter("order");
        String s_desc = req.getParameter("desc");
        User user = (User) req.getSession().getAttribute("user");

        int page = 1;
        int pageSize = 20;
        Integer commodityId = null;
        Boolean desc = false;
        if (s_page != null && !s_page.equals("")) {
            page = Integer.parseInt(s_page);
        }
        if (s_pageSize != null && !s_pageSize.equals("")) {
            pageSize = Integer.parseInt(s_pageSize);
        }
        if (s_commodityId != null && !s_commodityId.equals("")) {
            commodityId = Integer.valueOf(s_commodityId);
        }
        if (s_desc != null && s_desc.equals("true")) {
            desc = true;
        }
        String res = starService.getList(page, pageSize, commodityId, user.getId(), order, desc);
        resp.getWriter().write(res);
    }

}