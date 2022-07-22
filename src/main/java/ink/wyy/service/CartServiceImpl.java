package ink.wyy.service;

import com.google.gson.Gson;
import ink.wyy.bean.Cart;
import ink.wyy.bean.Order;
import ink.wyy.dao.CartDao;
import ink.wyy.dao.CartDaoImpl;
import ink.wyy.dao.OrderDao;
import ink.wyy.dao.OrderDaoImpl;
import ink.wyy.util.JsonUtil;

import java.io.IOException;
import java.util.*;

public class CartServiceImpl implements CartService {

    private CartDao cartDao;
    private OrderService orderService;
    private Gson gson;

    public CartServiceImpl(CartDao cartDao) {
        this.cartDao = cartDao;
        orderService = new OrderServiceImpl(new OrderDaoImpl());
        this.gson = new Gson();
    }

    @Override
    public String add(Integer commodityId, Integer userId, Integer buyNum) {
        HashMap<String, Object> res = new HashMap<>();
        if (buyNum == null || buyNum <= 0) {
            res.put("status", 0);
            res.put("errMsg", "buy_num不合法");
            return gson.toJson(res);
        }
        String msg = cartDao.add(commodityId, userId, buyNum);
        if (msg != null) {
            res.put("status", 0);
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String delete(Integer id, Integer userId) {
        Cart cart = cartDao.findById(id);
        HashMap<String, Object> res = new HashMap<>();
        if (cart == null) {
            res.put("status", 0);
            res.put("errMsg", "商品不存在");
            return gson.toJson(res);
        }
        if (!Objects.equals(cart.getUserId(), userId)) {
            res.put("status", 0);
            res.put("errMsg", "无操作权限");
            return gson.toJson(res);
        }
        String msg = cartDao.delete(id);
        if (msg != null) {
            res.put("status", 0);
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String empty(Integer userId) {
        String msg = cartDao.empty(userId);
        HashMap<String, Object> res = new HashMap<>();
        if (msg != null) {
            res.put("status", 0);
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String getList(int page, int pageSize, String order, Integer userId) {
        if (order == null || order.equals("")) {
            order = "time";
        }
        switch (order) {
            case "time":
            case "id":
                break;
            default:
                return "\"status\":0,\"errMsg\":\"order错误\"";
        }
        HashMap<String, Object> res = cartDao.getList(page, pageSize, order, userId);
        if (res == null) {
            return "\"status\":0,\"errMsg\":\"查询失败\"";
        }
        return gson.toJson(res);
    }

    @Override
    public String settle(Integer userId, Order order) {
        HashMap<String, Object> res = new HashMap<>();
        List<Map<String, Object>> list = (List<Map<String, Object>>) cartDao.getList(null, null, null, userId).get("commodities");
        if (list.size() == 0) {
            res.put("status", 0);
            res.put("errMsg", "购物车为空");
            return gson.toJson(res);
        }
        if (order.getAddress() == null || order.getAddress().equals("")) {
            res.put("status", 0);
            res.put("errMsg", "地址不能为空");
            return gson.toJson(res);
        }
        if (order.getPhone() == null || order.getPhone().equals("")) {
            res.put("status", 0);
            res.put("errMsg", "手机不能为空");
            return gson.toJson(res);
        }
        if (order.getBuyNum() <= 0) {
            res.put("status", 0);
            res.put("errMsg", "购买数量不合法");
            return gson.toJson(res);
        }

        List<Integer> listOfOrder = new ArrayList<>();
        for (Map<String, Object> map : list) {
            int stock = (int) map.get("stock");
            if (stock == 0) continue;
            order.setCommodityId((Integer) map.get("commodity_id"));
            order.setSellerId((Integer) map.get("seller_id"));
            order.setBuyNum((Integer) map.get("buy_num"));
            String add = orderService.add(order);
            try {
                HashMap<String, String> map1 = JsonUtil.stringToMap(add);
                if (map1.get("id") != null) {
                    listOfOrder.add(Integer.valueOf(map1.get("id")));
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        cartDao.empty(userId);
        res.put("status", 1);
        res.put("orders", listOfOrder);
        return gson.toJson(res);
    }
}
