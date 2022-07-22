package ink.wyy.service;

import ink.wyy.bean.Star;

public interface StarService {

    String add(Star star);

    String delete(Integer commodityId, Integer userId);

    String getNumByCommodityId(Integer commodityId);

    String getList(Integer page, Integer pageSize, Integer commodityId, Integer userId, String order, Boolean desc);
}
