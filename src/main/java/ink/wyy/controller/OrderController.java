package ink.wyy.controller;

import ink.wyy.bean.Order;
import ink.wyy.bean.User;
import ink.wyy.dao.OrderDao;
import ink.wyy.dao.OrderDaoImpl;
import ink.wyy.service.OrderService;
import ink.wyy.service.OrderServiceImpl;
import ink.wyy.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/order/*")
public class OrderController extends HttpServlet {

    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        OrderDao orderDao = new OrderDaoImpl();
        getServletContext().setAttribute("orderDao", orderDao);
        orderService = new OrderServiceImpl(orderDao);
        getServletContext().setAttribute("orderService", orderService);
        System.out.println("Order Init Over");
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
            case "/order/add":
                doAdd(req, resp, request);
                break;
            case "/order/delete":
                doDeleteOrder(req, resp, request);
                break;
            case "/order/update":
                doUpdate(req, resp, request);
                break;
            case "/order/ship":
                doShip(req, resp, request);
                break;
            case "/order/receipt":
                doReceipt(req, resp, request);
                break;
            default:
                resp.sendError(404);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/order/queryList":
                doQueryList(req, resp);
                break;
            case "/order/sellerQueryList":
                doSellerQueryList(req, resp);
                break;
            case "/order/query":
                doQuery(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    private void doAdd(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String s_commodityId = request.get("id");
        String address = request.get("address");
        String phone = request.get("phone");
        String nickname = request.get("nickname");
        String remark = request.get("remark");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (s_commodityId == null || s_commodityId.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        Integer commodityId = Integer.valueOf(s_commodityId);
        Order order = new Order(user.getId(), commodityId, address, phone);
        if (nickname != null && !nickname.equals("")) {
            order.setNickname(nickname);
        }
        if (remark != null && !remark.equals("")) {
            order.setRemark(remark);
        }
        String res = orderService.add(order);
        resp.getWriter().write(res);
    }

    private void doDeleteOrder(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String s_id = request.get("id");
        User user = (User) req.getSession().getAttribute("user");
        if (s_id == null || s_id.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        Integer id = Integer.valueOf(s_id);
        String res = null;
        if (user.getLevel() >= 3) {
            res = orderService.delete(id, -1);
        } else {
            res = orderService.delete(id, user.getId());
        }
        resp.getWriter().write(res);
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String s_id = request.get("id");
        String phone = request.get("phone");
        String address = request.get("address");
        String nickname = request.get("nickname");
        String remark = request.get("remark");
        User user = (User) req.getSession().getAttribute("user");
        if (s_id == null || s_id.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        Integer id = Integer.valueOf(s_id);
        Order order = new Order();
        order.setId(id);
        order.setPhone(phone);
        order.setAddress(address);
        order.setNickname(nickname);
        order.setRemark(remark);
        Integer userId = user.getId();
        if (user.getLevel() >= 3) userId = -1;
        String res = orderService.update(order, userId);
        resp.getWriter().write(res);
    }

    private void doShip(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String s_id = request.get("id");
        User user = (User) req.getSession().getAttribute("user");
        if (s_id == null || s_id.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        Integer id = Integer.valueOf(s_id);
        String res = orderService.ship(id, user.getId());
        resp.getWriter().write(res);
    }

    private void doReceipt(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String s_id = request.get("id");
        User user = (User) req.getSession().getAttribute("user");
        if (s_id == null || s_id.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        Integer id = Integer.valueOf(s_id);
        String res = orderService.receipt(id, user.getId());
        resp.getWriter().write(res);
    }

    private void doQuery(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String s_id = req.getParameter("id");
        User user = (User) req.getSession().getAttribute("user");
        if (s_id == null || s_id.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        Integer id = Integer.valueOf(s_id);
        String res = orderService.get(id, user.getId());
        resp.getWriter().write(res);
    }

    private void doQueryList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String s_page = req.getParameter("page");
        String s_pageSize = req.getParameter("page_size");
        String order = req.getParameter("order");
        String s_isShip = req.getParameter("is_ship");
        String s_commodityId = req.getParameter("commodity_id");
        User user = (User) req.getSession().getAttribute("user");
        int page = 1;
        int pageSize = 20;
        Integer commodityId = null;
        Boolean isShip = null;
        if (s_page != null && !s_page.equals("")) {
            page = Integer.parseInt(s_page);
        }
        if (s_pageSize != null && !s_pageSize.equals("")) {
            pageSize = Integer.parseInt(s_pageSize);
        }
        if (s_isShip != null) {
            if (s_isShip.equals("true")) {
                isShip = true;
            } else if (s_isShip.equals("false")) {
                isShip = false;
            }
        }
        if (s_commodityId != null && !s_commodityId.equals("")) {
            commodityId = Integer.valueOf(s_commodityId);
        }
        String res = orderService.getOrderList(page, pageSize, order, user.getId(), commodityId, null, isShip);
        resp.getWriter().write(res);
    }

    private void doSellerQueryList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String s_page = req.getParameter("page");
        String s_pageSize = req.getParameter("page_size");
        String order = req.getParameter("order");
        String s_isShip = req.getParameter("is_ship");
        String s_userId = req.getParameter("user_id");
        String s_sellerId = req.getParameter("seller_id");
        String s_commodityId = req.getParameter("commodity_id");
        User user = (User) req.getSession().getAttribute("user");
        if (user.getLevel() == 1 || (s_sellerId != null && user.getLevel() != 3)) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"无操作权限\"}");
            return;
        }
        if (user.getLevel() == 2) {
            s_sellerId = String.valueOf(user.getId());
        }
        int page = 1;
        int pageSize = 20;
        Integer commodityId = null;
        Boolean isShip = null;
        Integer sellerId = null;
        Integer userId = null;
        if (s_page != null && !s_page.equals("")) {
            page = Integer.parseInt(s_page);
        }
        if (s_pageSize != null && !s_pageSize.equals("")) {
            pageSize = Integer.parseInt(s_pageSize);
        }
        if (s_isShip != null) {
            if (s_isShip.equals("true")) {
                isShip = true;
            } else if (s_isShip.equals("false")) {
                isShip = false;
            }
        }
        if (s_commodityId != null && !s_commodityId.equals("")) {
            commodityId = Integer.valueOf(s_commodityId);
        }
        if (s_sellerId != null && !s_sellerId.equals("")) {
            sellerId = Integer.valueOf(s_sellerId);
        }
        if (s_userId != null && !s_userId.equals("")) {
            userId = Integer.valueOf(s_userId);
        }
        String res = orderService.getOrderList(page, pageSize, order, userId, commodityId, sellerId, isShip);
        resp.getWriter().write(res);
    }
}
