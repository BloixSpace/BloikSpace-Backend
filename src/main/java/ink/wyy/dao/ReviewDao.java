package ink.wyy.dao;

import ink.wyy.bean.Review;

import java.util.HashMap;

public interface ReviewDao {

    Review add(Review review);

    String delete(Integer id);

    String update(Review review);

    Review findById(Integer id);

    Review findByOrderId(Integer id);

    HashMap<String, Object> getReviewList(int page, int pageSize, Integer orderId, Integer commodityId, String order, Boolean desc);
}
