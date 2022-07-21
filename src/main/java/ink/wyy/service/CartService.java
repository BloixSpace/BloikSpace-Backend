package ink.wyy.service;

import ink.wyy.bean.Order;

public interface CartService {

    String add(Integer commodityId, Integer userId, Integer buyNum);

    String delete(Integer id, Integer userId);

    String empty(Integer userId);

    String getList(int page, int pageSize, String order, Integer userId);

    String settle(Integer userId, Order order);
}
