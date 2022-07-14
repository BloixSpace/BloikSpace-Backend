package ink.wyy.service;

import ink.wyy.bean.Commodity;

import javax.servlet.http.HttpServletRequest;

public interface CommodityService {
    String add(Commodity commodity);

    String update(HttpServletRequest req, Integer userId);

    String delete(String id, Integer userId);

    String get(String id);

    String getList(int page, int pageSize, String order, String category, Integer userId);
}
