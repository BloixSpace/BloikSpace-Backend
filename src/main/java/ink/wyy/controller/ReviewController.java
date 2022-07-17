package ink.wyy.controller;

import ink.wyy.bean.Review;
import ink.wyy.bean.User;
import ink.wyy.dao.ReviewDao;
import ink.wyy.dao.ReviewDaoImpl;
import ink.wyy.service.ReviewService;
import ink.wyy.service.ReviewServiceImpl;
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

@WebServlet(value = "/review/*", loadOnStartup = 1)
public class ReviewController extends HttpServlet {

    ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        ReviewDao reviewDao = new ReviewDaoImpl();
        reviewService = new ReviewServiceImpl(reviewDao);
        getServletContext().setAttribute("reviewService", reviewService);
        getServletContext().setAttribute("reviewDao", reviewDao);
        System.out.println("Review Init Over");
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
            case "/review/add":
                doAdd(req, resp, request);
                break;
            case "/review/delete":
                doDeleteReview(req, resp, request);
                break;
            case "/review/update":
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
            case "/review/getList":
                doGetList(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    private void doAdd(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        String content = request.get("content");
        String s_orderId = request.get("order_id");
        String s_star = request.get("star");
        Integer star = null;
        Integer orderId = null;
        if (s_star != null) star = Integer.valueOf(s_star);
        if (s_orderId != null) orderId = Integer.valueOf(s_orderId);
        Review review = new Review(orderId, star, content);
        String res = reviewService.add(review, user.getId());
        resp.getWriter().write(res);
    }

    private void doDeleteReview(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        String s_id = request.get("id");
        if (s_id == null || s_id.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        User user = (User) req.getSession().getAttribute("user");
        Integer id = Integer.valueOf(s_id);
        Integer userId = user.getId();
        if (user.getLevel() >= 3) userId = -1;
        String res = reviewService.delete(id, userId);
        resp.getWriter().write(res);
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp, Map<String, String> request) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        String content = request.get("content");
        String s_id = request.get("id");
        String s_star = request.get("star");
        Integer star = null;
        Integer id = null;
        if (s_star != null) star = Integer.valueOf(s_star);
        if (s_id == null || s_id.equals("")) {
            resp.getWriter().write("{\"status\":0,\"errMsg\":\"id不能为空\"}");
            return;
        }
        id = Integer.valueOf(s_id);
        Review review = new Review();
        review.setId(id);
        review.setContent(content);
        review.setStar(star);
        Integer userId = user.getId();
        if (user.getLevel() >= 3) userId = -1;
        String res = reviewService.update(review, userId);
        resp.getWriter().write(res);
    }

    private void doGetList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String s_page = req.getParameter("page");
        String s_pageSize = req.getParameter("page_size");
        String order = req.getParameter("order");
        String s_orderId = req.getParameter("order_id");
        String s_commodityId = req.getParameter("commodity_id");
        String s_desc = req.getParameter("desc");
        int page = 1;
        int pageSize = 20;
        Integer commodityId = null;
        Integer orderId = null;
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
        if (s_orderId != null && !s_orderId.equals("")) {
            orderId = Integer.valueOf(s_orderId);
        }
        if (s_desc != null && s_desc.equals("true")) {
            desc = true;
        }
        String res = reviewService.getReviewList(page, pageSize, orderId, commodityId, order, desc);
        resp.getWriter().write(res);
    }

}
