package ink.wyy.controller;

import ink.wyy.bean.Order;
import ink.wyy.bean.User;
import ink.wyy.dao.CartDao;
import ink.wyy.dao.impl.CartDaoImpl;
import ink.wyy.service.CartService;
import ink.wyy.service.impl.CartServiceImpl;
import ink.wyy.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(value = "/cart/*", loadOnStartup = 1)
public class CartController extends HttpServlet {

    CartService cartService;

    @Override
    public void init() throws ServletException {
        CartDao cartDao = new CartDaoImpl();
        cartService = new CartServiceImpl(cartDao);
        getServletContext().setAttribute("cartDao", cartDao);
        getServletContext().setAttribute("cartService", cartService);
        System.out.println("Cart Init Over");
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
            case "/cart/add":
                doAdd(req, resp, request);
                break;
            case "/cart/settle":
                doSettle(req, resp, request);
                break;
            case "/cart/delete":
                doDeleteCart(req, resp, request);
                break;
            case "/cart/update":
                doUpdate(req, resp, request);
                break;
            default:
                resp.sendError(404);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/cart/getList":
                doGetList(req, resp);
                break;
            case "/cart/empty":
                doEmpty(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    private void doAdd(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String s_commodityId = request.get("commodity_id");
        String s_buyNum = request.get("buy_num");
        if (s_commodityId == null || s_commodityId.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"commodity_id不能为空\"}");
            return;
        }
        if (s_buyNum == null || s_buyNum.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"buy_num不能为空\"}");
            return;
        }
        Integer buyNum = Integer.valueOf(s_buyNum);
        Integer commodityId = Integer.valueOf(s_commodityId);
        User user = (User) req.getSession().getAttribute("user");
        String res = cartService.add(commodityId, user.getId(), buyNum);
        resp.getWriter().write(res);
    }

    private void doDeleteCart(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        // 传入一个列表
        List<Double> s_id = (List<Double>) (Object) request.get("id");
        System.out.println(s_id);
        if (s_id.size() == 0) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        User user = (User) req.getSession().getAttribute("user");
        String res = cartService.batchDelete(s_id, user.getId());
        resp.getWriter().write(res);
    }

    private void doEmpty(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        String res = cartService.empty(user.getId());
        resp.getWriter().write(res);
    }

    private void doGetList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        String s_page = req.getParameter("page");
        String s_pageSize = req.getParameter("page_size");
        String order = req.getParameter("order");
        int page = 1;
        int pageSize = 20;
        if (s_page != null && !s_page.equals("")) {
            page = Integer.parseInt(s_page);
        }
        if (s_pageSize != null && !s_pageSize.equals("")) {
            pageSize = Integer.parseInt(s_pageSize);
        }
        String res = cartService.getList(page, pageSize, order, user.getId());
        resp.getWriter().write(res);
    }

    private void doSettle(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String phone = request.get("phone");
        String address = request.get("address");
        String nickname = request.get("nickname");
        String remark = request.get("remark");
        List<Double> list = (List<Double>) ((Object) request.get("id"));
        Integer userId = ((User) req.getSession().getAttribute("user")).getId();
        // 创建一个订单模板
        Order order = new Order();
        order.setPhone(phone);
        order.setAddress(address);
        order.setNickname(nickname);
        order.setRemark(remark);
        order.setUserId(userId);
        String res = cartService.settle(userId, order, list);
        resp.getWriter().write(res);
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        String s_id = request.get("id");
        if (s_id == null || s_id.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        String s_buyNum = request.get("buy_num");
        if (s_buyNum == null || s_buyNum.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"buyNum不能为空\"}");
            return;
        }
        Integer buyNum = Integer.valueOf(s_buyNum);
        Integer id = Integer.valueOf(s_id);
        String msg = cartService.update(id, user.getId(), buyNum);
        resp.getWriter().write(msg);
    }

}