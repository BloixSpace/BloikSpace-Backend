package ink.wyy.service;

import com.google.gson.Gson;
import ink.wyy.bean.Order;
import ink.wyy.bean.Review;
import ink.wyy.dao.OrderDao;
import ink.wyy.dao.OrderDaoImpl;
import ink.wyy.dao.ReviewDao;
import ink.wyy.dao.ReviewDaoImpl;

import java.util.HashMap;
import java.util.Objects;

public class ReviewServiceImpl implements ReviewService {

    OrderDao orderDao;
    ReviewDao reviewDao;
    Gson gson;

    public ReviewServiceImpl(ReviewDao reviewDao) {
        orderDao = new OrderDaoImpl();
        this.reviewDao = reviewDao;
        gson = new Gson();
    }

    @Override
    public String add(Review review, Integer userId) {
        Integer orderId = review.getOrderId();
        if (orderId == null || orderId == 0) {
            return "{\"status\":0,\"errMsg\":\"order_id不能为空\"}";
        }
        Review review1 = reviewDao.findByOrderId(orderId);
        Order order = orderDao.findById(orderId);
        if (order == null) {
            return "{\"status\":0,\"errMsg\":\"订单不存在\"}";
        }
        if (order.getReceiptTime() == null || order.getReceiptTime().equals("")) {
            return "{\"status\":0,\"errMsg\":\"订单尚未确认收货，确认收货后方可评价\"}";
        }
        if (review1 != null) {
            return "{\"status\":0,\"errMsg\":\"评价已存在，不能重复评价\"}";
        }
        if (review.getStar() == null || review.getStar() > 5 || review.getStar() < 1) {
            return "{\"status\":0,\"errMsg\":\"star在1～5之间\"}";
        }
        if (review.getContent() == null || review.getContent().equals("")) {
            review.setContent("用户未撰写评论");
        }
        if (!Objects.equals(order.getUserId(), userId)) {
            return "{\"status\":0,\"errMsg\":\"无操作权限\"}";
        }
        review.setUserId(order.getUserId());
        review.setSellerId(order.getSellerId());
        review.setCommodityId(order.getCommodityId());
        review = reviewDao.add(review);
        HashMap<String, Object> res = new HashMap<>();
        if (review.getErrorMsg() != null) {
            res.put("status", 0);
            res.put("errMsg", review.getErrorMsg());
            return gson.toJson(res);
        }
        res.put("status", 1);
        res.put("id", review.getId());
        return gson.toJson(res);
    }

    @Override
    public String delete(Integer id, Integer userId) {
        Review review = reviewDao.findById(id);
        if (review == null) {
            return "{\"status\":0,\"errMsg\":\"评价不存在\"}";
        }
        if (userId != -1 && !Objects.equals(review.getUserId(), userId)) {
            return "{\"status\":0,\"errMsg\":\"无操作权限\"}";
        }
        String msg = reviewDao.delete(id);
        HashMap<String, Object> res = new HashMap<>();
        if (msg != null) {
            res.put("status", 0);
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String update(Review review, Integer userId) {
        String content = review.getContent();
        Integer star = review.getStar();
        if (star != null && (star < 1 || star > 5)) {
            return "{\"status\":0,\"errMsg\":\"star在1～5之间\"}";
        }
        review = reviewDao.findById(review.getId());
        if (!Objects.equals(review.getUserId(), userId)) {
            return "{\"status\":0,\"errMsg\":\"无操作权限\"}";
        }
        if (star != null) {
            review.setStar(star);
        }
        if (content != null && !content.equals("")) {
            review.setContent(content);
        }
        String msg = reviewDao.update(review);
        HashMap<String, Object> res = new HashMap<>();
        if (msg != null) {
            res.put("status", 0);
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String getReviewList(int page, int pageSize, Integer orderId, Integer commodityId, String order, Boolean desc) {
        if (order == null || order.equals("")) {
            order = "update_time";
        }
        switch (order) {
            case "id":
            case "star":
            case "update_time":
                break;
            default:
                return "{\"status\":0,\"errMsg\":\"order错误\"}";
        }
        HashMap<String, Object> res = reviewDao.getReviewList(page, pageSize, orderId, commodityId, order, desc);
        if (res == null) {
            return "{\"status\":0,\"errMsg\":\"查询错误\"}";
        }
        return gson.toJson(res);
    }
}
