package ink.wyy.dao;

import ink.wyy.bean.Star;

import java.util.HashMap;

public interface StarDao {

    Star add(Star star);

    String delete(Integer id);

    Integer getNumByCommodityId(Integer commodityId);

    Star findById(Integer commodityId, Integer userId);

    HashMap<String, Object> getList(Integer page, Integer pageSize, Integer commodityId, Integer userId, String order, Boolean desc);
}
