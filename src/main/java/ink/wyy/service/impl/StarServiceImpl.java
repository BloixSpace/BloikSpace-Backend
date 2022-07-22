package ink.wyy.service.impl;

import com.google.gson.Gson;
import ink.wyy.bean.Star;
import ink.wyy.dao.StarDao;
import ink.wyy.service.StarService;

import java.util.HashMap;

public class StarServiceImpl implements StarService {

    private final StarDao starDao;
    private final Gson gson;

    public StarServiceImpl(StarDao starDao) {
        this.starDao = starDao;
        gson = new Gson();
    }

    @Override
    public String add(Star star) {
        Star oldStar = starDao.findById(star.getCommodityId(), star.getUserId());
        HashMap<String, Object> res = new HashMap<>();
        if (oldStar != null) {
            res.put("status", 0);
            res.put("errMsg", "收藏已存在");
            return gson.toJson(res);
        }
        Star msg = starDao.add(star);
        if (msg.getErrMsg() != null) {
            res.put("status", 0);
            res.put("errMsg", msg.getErrMsg());
            return gson.toJson(res);
        }
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String delete(Integer commodityId, Integer userId) {
        Star star = starDao.findById(commodityId, userId);
        HashMap<String, Object> res = new HashMap<>();
        if (star == null) {
            res.put("status", 0);
            res.put("errMsg", "收藏不存在");
            return gson.toJson(res);
        }
        String msg = starDao.delete(star.getId());
        if (msg != null) {
            res.put("status", 0);
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String getNumByCommodityId(Integer commodityId) {
        Integer num = starDao.getNumByCommodityId(commodityId);
        if (num == -1) {
            return "{\"status\":0,\"errMsg\":\"查询失败\"}";
        }
        return "{\"status\":1,\"num\":" + num + "}";
    }

    @Override
    public String getList(Integer page, Integer pageSize, Integer commodityId, Integer userId, String order, Boolean desc) {
        if (order == null || order.equals("")) {
            order = "id";
        }
        switch (order) {
            case "id":
            case "user_id":
            case "commodity_id":
                break;
            default:
                return "{\"status\":0,\"errMsg\":\"order不合法\"}";
        }
        HashMap<String, Object> res = starDao.getList(page, pageSize, commodityId, userId, order, desc);
        return gson.toJson(res);
    }
}
