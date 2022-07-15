package ink.wyy.service;

import com.google.gson.Gson;
import ink.wyy.bean.Commodity;
import ink.wyy.dao.CommodityDao;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
        if (commodity.getPrice() == null || commodity.getPrice() == 0) {
            res.put("errMsg", "price不能为空");
            return gson.toJson(res);
        }
        if (commodity.getStock() == null || commodity.getStock() == 0) {
            res.put("errMsg", "stock不能为空");
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
    public String update(HttpServletRequest req, Integer userId) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("status", 0);
        String id = req.getParameter("id");
        if (id == null) {
            res.put("errMsg", "id不能为空");
            return gson.toJson(res);
        }
        Commodity commodity = commodityDao.findById(Integer.valueOf(id));
        if (commodity == null) {
            res.put("errMsg", "文章不存在");
            return gson.toJson(res);
        }
        if (!Objects.equals(commodity.getUserId(), userId)) {
            res.put("errMsg", "无操作权限");
            return gson.toJson(res);
        }
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String category = req.getParameter("category");
        String picUri = req.getParameter("pic");
        String s_price = req.getParameter("price");
        String s_stock = req.getParameter("stock");
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
            commodity.setPrice(Double.valueOf(s_price));
        }
        if (s_stock != null && !s_stock.equals("")) {
            commodity.setStock(Integer.valueOf(s_stock));
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
        res.put("release_time", commodity.getCreateDate());
        res.put("update_time", commodity.getUpdateDate());
        return gson.toJson(res);
    }

    @Override
    public String getList(int page, int pageSize, String order, String category, Integer userId) {
        HashMap<String, Object> map = commodityDao.getList(page, pageSize, order, category, userId);
        if (map == null) {
            map = new HashMap<>();
            map.put("status", 0);
            map.put("errMsg", "order不存在");
        }
        return gson.toJson(map);
    }
}
