package ink.wyy.dao;

import ink.wyy.bean.Cart;

import java.util.HashMap;

public interface CartDao {

    String add(Integer commodityId, Integer userId, Integer buyNum);

    String delete(Integer id);

    HashMap<String, Object> getList(Integer page, Integer pageSize, String order, Integer userId);

    String empty(Integer userId);

    Cart findById(Integer id);
}
