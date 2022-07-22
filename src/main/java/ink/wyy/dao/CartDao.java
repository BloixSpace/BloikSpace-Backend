package ink.wyy.dao;

import ink.wyy.bean.Cart;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface CartDao {

    String add(Integer commodityId, Integer userId, Integer buyNum);

    String delete(Integer id);

    String update(Integer id, Integer buyNum);

    HashMap<String, Object> getList(Integer page, Integer pageSize, String order, Integer userId);

    String empty(Integer userId);

    Cart findById(Integer id);

    Cart findByCommodityId(Integer commodityId, Integer userId);
}
