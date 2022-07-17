package ink.wyy.service;

import ink.wyy.bean.Review;

import java.util.HashMap;

public interface ReviewService {

    String add(Review review, Integer userId);

    String delete(Integer id, Integer userId);

    String update(Review review, Integer userId);

    String getReviewList(int page, int pageSize, Integer orderId, Integer commodityId, String order, Boolean desc);
}
