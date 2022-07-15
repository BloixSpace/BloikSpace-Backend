package ink.wyy.dao;

import ink.wyy.bean.Order;

import java.util.HashMap;

public interface OrderDao {

    Order add(Order order);

    String delete(Integer id);

    String update(Order order);

    String ship(Integer id);

    Order findById(Integer id);

    HashMap<String, Object> getOrderList(int page, int pageSize, String order, Integer userId, Integer commodityId, Integer seller_id, Boolean isShip);
}
