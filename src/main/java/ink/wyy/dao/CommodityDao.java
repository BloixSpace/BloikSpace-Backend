package ink.wyy.dao;

import ink.wyy.bean.Commodity;

import java.util.HashMap;

public interface CommodityDao {
    Commodity add(Commodity commodity);
    String update(Commodity commodity);
    Boolean delete(Integer id);
    Commodity findById(Integer id);

    String buy(Integer id, Integer num);

    HashMap<String, Object> getList(int page, int pageSize, String order, String category, String key, Integer userId, Boolean desc);
}
