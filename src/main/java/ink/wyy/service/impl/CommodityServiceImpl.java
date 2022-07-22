package ink.wyy.service.impl;

import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Bool;
import ink.wyy.bean.Commodity;
import ink.wyy.dao.CommodityDao;
import ink.wyy.service.CommodityService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommodityServiceImpl implements CommodityService {

    private final CommodityDao commodityDao;
    private final Gson gson;

    public CommodityServiceImpl(CommodityDao commodityDao) {
        this.commodityDao = commodityDao;
        gson = new Gson();
    }

    @Override
    public String add(Commodity commodity) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("status", 0);
        if (commodity.getTitle() == null || commodity.getTitle().equals("")) {
            res.put("errMsg", "title不能为空");
            return gson.toJson(res);
        }
        if (commodity.getPicUri() == null || commodity.getPicUri().equals("")) {
            res.put("errMsg", "pic不能为空");
            return gson.toJson(res);
        }
        if (commodity.getPrice() == null || commodity.getPrice() <= 0) {
            res.put("errMsg", "price不合法");
            return gson.toJson(res);
        }
        if (commodity.getStock() == null || commodity.getStock() <= 0) {
            res.put("errMsg", "stock不合法");
            return gson.toJson(res);
        }
        commodity = commodityDao.add(commodity);
        if (commodity.getErrorMsg() != null) {
            res.put("errMsg", commodity.getErrorMsg());
            return gson.toJson(res);
        }
        res.put("status", 1);
        res.put("id", commodity.getId());
        return gson.toJson(res);
    }

    @Override
    public String update(Map<String, String> req, Integer userId) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("status", 0);
        String s_id = req.get("id");
        if (s_id == null) {
            res.put("errMsg", "id不能为空");
            return gson.toJson(res);
        }
        Integer id = null;
        try {
            id = Integer.valueOf(s_id);
        } catch (Exception e) {
            res.put("errMsg", "id不合法");
            return gson.toJson(res);
        }
        Commodity commodity = commodityDao.findById(id);
        if (commodity == null) {
            res.put("errMsg", "商品不存在");
            return gson.toJson(res);
        }
        if (!Objects.equals(commodity.getUserId(), userId)) {
            res.put("errMsg", "无操作权限");
            return gson.toJson(res);
        }
        String title = req.get("title");
        String content = req.get("content");
        String category = req.get("category");
        String picUri = req.get("pic");
        String s_price = req.get("price");
        String s_stock = req.get("stock");
        if (title != null && !title.equals("")) {
            commodity.setTitle(title);
        }
        if (content != null && !content.equals("")) {
            commodity.setContent(content);
        }
        if (category != null && !category.equals("")) {
            commodity.setCategory(title);
        }
        if (picUri != null && !picUri.equals("")) {
            commodity.setPicUri(picUri);
        }
        if (s_price != null && !s_price.equals("")) {
            try {
                commodity.setPrice(Double.valueOf(s_price));
            } catch (Exception e) {
                res.put("errMsg", "价格不合法");
                return gson.toJson(res);
            }
        }
        if (s_stock != null && !s_stock.equals("")) {
            try {
                Integer stock = Integer.valueOf(s_stock);
                commodity.setStock(stock);
            } catch (Exception e) {
                res.put("errMsg", "库存不合法");
                return gson.toJson(res);
            }
        }
        String msg = commodityDao.update(commodity);
        if (msg == null) {
            res.put("status", 1);
        } else {
            res.put("errMsg", msg);
        }
        return gson.toJson(res);
    }

    @Override
    public String delete(String ids, Integer userId) {
        HashMap<String, Object> res = new HashMap<>();
        if (ids == null || ids.equals("")) {
            res.put("status", 0);
            res.put("errMsg", "id不能为空");
            return gson.toJson(res);
        }
        int id = Integer.parseInt(ids);
        Commodity commodity = commodityDao.findById(id);
        if (commodity == null) {
            res.put("status", 0);
            res.put("errMsg", "文章不存在");
            return gson.toJson(res);
        }
        if (userId != -1 && !Objects.equals(commodity.getUserId(), userId)) {
            res.put("status", 0);
            res.put("errMsg", "无操作权限");
            return gson.toJson(res);
        }
        Boolean ok = commodityDao.delete(id);
        if (!ok) {
            res.put("status", 0);
            res.put("errMsg", "删除失败");
        } else {
            res.put("status", 1);
        }
        return gson.toJson(res);
    }

    @Override
    public String get(String ids) {
        HashMap<String, Object> res = new HashMap<>();
        if (ids == null || ids.equals("")) {
            res.put("status", 0);
            res.put("errMsg", "id不能为空");
            return gson.toJson(res);
        }
        int id = Integer.parseInt(ids);
        Commodity commodity = commodityDao.findById(id);
        if (commodity == null) {
            res.put("status", 0);
            res.put("errMsg", "商品不存在");
            return gson.toJson(res);
        }
        res.put("status", 1);
        res.put("id", commodity.getId());
        res.put("title", commodity.getTitle());
        res.put("content", commodity.getContent());
        res.put("category", commodity.getCategory());
        res.put("user_id", commodity.getUserId());
        res.put("pic", commodity.getPicUri());
        res.put("price", commodity.getPrice());
        res.put("stock", commodity.getStock());
        res.put("sales", commodity.getSales());
        res.put("release_time", commodity.getCreateDate());
        res.put("update_time", commodity.getUpdateDate());
        return gson.toJson(res);
    }

    @Override
    public String getList(int page, int pageSize, String order, String category, String key, Integer userId, Boolean desc) {
        if (key == null) {
            key = "";
        }
        if (desc == null) {
            desc = false;
        }
        HashMap<String, Object> map = commodityDao.getList(page, pageSize, order, category, key, userId, desc);
        if (map == null) {
            map = new HashMap<>();
            map.put("status", 0);
            map.put("errMsg", "order不存在");
        }
        return gson.toJson(map);
    }
}
