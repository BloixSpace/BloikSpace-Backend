package ink.wyy.service;

import ink.wyy.bean.Order;

public interface OrderService {

    String add(Order order);

    String delete(Integer id, Integer userId);

    String update(Order order, Integer userId);

    String get(Integer id, Integer userId);

    String ship(Integer id, Integer userId);

    String getOrderList(int page, int pageSize, String order, Integer userId, Integer commodityId, Integer sellerId, Boolean isShip);
}
