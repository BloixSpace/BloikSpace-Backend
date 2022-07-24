package ink.wyy.service.impl;

import com.google.gson.Gson;
import ink.wyy.bean.Cart;
import ink.wyy.bean.Commodity;
import ink.wyy.bean.Order;
import ink.wyy.dao.*;
import ink.wyy.dao.impl.CommodityDaoImpl;
import ink.wyy.dao.impl.OrderDaoImpl;
import ink.wyy.service.CartService;
import ink.wyy.service.OrderService;
import ink.wyy.util.JsonUtil;

import java.io.IOException;
import java.util.*;

public class CartServiceImpl implements CartService {

    private CartDao cartDao;
    private OrderService orderService;
    private CommodityDao commodityDao;
    private Gson gson;

    public CartServiceImpl(CartDao cartDao) {
        this.cartDao = cartDao;
        orderService = new OrderServiceImpl(new OrderDaoImpl());
        this.gson = new Gson();
        commodityDao = new CommodityDaoImpl();
    }

    @Override
    public String add(Integer commodityId, Integer userId, Integer buyNum) {
        HashMap<String, Object> res = new HashMap<>();
        if (buyNum == null || buyNum <= 0) {
            res.put("status", 0);
            res.put("errMsg", "buy_num不合法");
            return gson.toJson(res);
        }
        Cart cart = cartDao.findByCommodityId(commodityId, userId);
        String msg = null;
        if (cart != null) {
            Integer newBuyNum = cart.getBuyNum() + buyNum;
            Commodity commodity = commodityDao.findById(cart.getCommodityId());
            if (newBuyNum > commodity.getStock()) {
                newBuyNum = commodity.getStock();
            }
            msg = cartDao.update(cart.getId(), newBuyNum);
        } else {
            Commodity commodity = commodityDao.findById(commodityId);
            if (buyNum > commodity.getStock()) {
                buyNum = commodity.getStock();
            }
            msg = cartDao.add(commodityId, userId, buyNum);
        }
        if (msg != null) {
            res.put("status", 0);
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String batchDelete(List list, Integer userId) throws IOException {
        StringBuilder errMsg = new StringBuilder();
        HashMap<String, Object> res = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Integer id = ((Double) list.get(i)).intValue();
            String msg = delete(id, userId);
            HashMap<String, String> map = JsonUtil.stringToMap(msg);
            if (map.get("status").equals("0")) {
                if (!errMsg.toString().equals("")) {
                    errMsg.append("\n");
                }
                errMsg.append(id).append(": ").append(map.get("errMsg"));
            }
        }
        if (!errMsg.toString().equals("")) {
            res.put("status", 0);
            res.put("errMsg", errMsg);
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
    public String update(Integer id, Integer userId, Integer buyNum) {
        Cart cart = cartDao.findById(id);
        Commodity commodity = commodityDao.findById(cart.getCommodityId());
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
        if (buyNum < 0) {
            res.put("status", 0);
            res.put("errMsg", "购买数量不能为负");
            return gson.toJson(res);
        }
        if (buyNum > commodity.getStock()) {
            res.put("status", 0);
            res.put("errMsg", "库存不足");
            return gson.toJson(res);
        }
        String msg = cartDao.update(id, buyNum);
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
    public String settle(Integer userId, Order order, List list) {
        HashMap<String, Object> res = new HashMap<>();
        if (list.size() == 0) {
            res.put("status", 0);
            res.put("errMsg", "结算商品不能为空");
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

        List<Integer> listOfOrder = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            // 通过传入list找到对应购物车条目和商品
            Cart cart = cartDao.findById(((Double) list.get(i)).intValue());
            Commodity commodity = commodityDao.findById(cart.getCommodityId());
            // 购买完成后，删除对应购物车条目
            cartDao.delete(cart.getId());
            int stock = commodity.getStock();
            if (stock == 0) continue;
            order.setCommodityId(cart.getCommodityId());
            order.setSellerId(commodity.getUserId());
            // 设置购买数量为购物车要求购买数量和库存的最小值
            order.setBuyNum(Math.min(cart.getBuyNum(), commodity.getStock()));
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
        res.put("status", 1);
        res.put("orders", listOfOrder);
        return gson.toJson(res);
    }
}
