package ink.wyy.service;

import ink.wyy.bean.Order;

import java.io.IOException;
import java.util.List;

public interface CartService {

    String add(Integer commodityId, Integer userId, Integer buyNum);

    String batchDelete(List list, Integer userId) throws IOException;

    String delete(Integer id, Integer userId);

    String update(Integer id, Integer userId, Integer buyNum);

    String empty(Integer userId);

    String getList(int page, int pageSize, String order, Integer userId);

    String settle(Integer userId, Order order, List list);
}
