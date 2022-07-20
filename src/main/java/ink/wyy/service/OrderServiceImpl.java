package ink.wyy.service;

import com.google.gson.Gson;
import ink.wyy.bean.Commodity;
import ink.wyy.bean.Notice;
import ink.wyy.bean.Order;
import ink.wyy.dao.CommodityDao;
import ink.wyy.dao.CommodityDaoImpl;
import ink.wyy.dao.NoticeDaoImpl;
import ink.wyy.dao.OrderDao;

import java.util.HashMap;
import java.util.Objects;

public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;

    private final CommodityDao commodityDao;

    private final Gson gson;

    private final NoticeService noticeService;

    public OrderServiceImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
        commodityDao = new CommodityDaoImpl();
        this.gson = new Gson();
        this.noticeService = new NoticeServiceImpl(new NoticeDaoImpl());
    }

    @Override
    public String add(Order order) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("status", 0);
        if (order.getCommodityId() == null || order.getCommodityId() == 0) {
            res.put("errMsg", "commodity_id不能为空");
            return gson.toJson(res);
        }
        if (order.getAddress() == null || order.getAddress().equals("")) {
            res.put("errMsg", "address不能为空");
            return gson.toJson(res);
        }
        if (order.getPhone() == null || order.getPhone().equals("")) {
            res.put("errMsg", "phone不能为空");
            return gson.toJson(res);
        }
        if (order.getNickname() == null || order.getNickname().equals("")) {
            order.setNickname(order.getPhone());
        }
        Commodity commodity = commodityDao.findById(order.getCommodityId());
        if (commodity == null) {
            res.put("errMsg", "商品不存在");
            return gson.toJson(res);
        }

        String msg = commodityDao.buy(order.getCommodityId(), 1);
        order.setSellerId(commodity.getUserId());
        if (msg != null) {
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        Order order1 = orderDao.add(order);
        if (order1.getErrorMsg() != null) {
            res.put("errMsg", order1.getErrorMsg());
            commodityDao.buy(order.getCommodityId(), -1);
            return gson.toJson(res);
        }
        Notice noticeAdd = new Notice(commodity.getUserId(), "order add", "您有一条新的订单，请及时处理。");
        noticeAdd.setOrderId(order1.getId());
        noticeAdd.setCommodityId(commodity.getId());
        noticeService.add(noticeAdd);
        if (commodity.getStock() == 1) {
            Notice noticeStock = new Notice(commodity.getUserId(), "commodity stock", "有一件商品售罄了，请及时补货。");
            noticeStock.setCommodityId(commodity.getId());
            noticeService.add(noticeStock);
        }
        res.put("status", 1);
        res.put("id", order1.getId());
        return gson.toJson(res);
    }

    @Override
    public String delete(Integer id, Integer userId) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("status", 0);
        Order order = orderDao.findById(id);
        if (order == null) {
            res.put("errMsg", "订单不存在");
            return gson.toJson(res);
        }
        // 买家，卖家，管理员有权删除
        Commodity commodity = commodityDao.findById(order.getCommodityId());
        if (userId != -1 && !userId.equals(commodity.getUserId()) && !Objects.equals(order.getUserId(), userId)) {
            res.put("errMsg", "无操作权限");
            return gson.toJson(res);
        }
        // 发货后卖家和管理员仍然可以删除订单
        if (userId != -1 && !userId.equals(commodity.getUserId()) && order.getShip()) {
            res.put("errMsg", "已发货，请联系卖家删除订单");
            return gson.toJson(res);
        }
        // 确认收货后，卖家无法删除订单，管理员可以（防止卖家通过删除订单的方式删除评价）
        if (userId != -1 && order.getReceiptTime() != null) {
            res.put("errMsg", "买家已确认收货，无法删除订单");
            return gson.toJson(res);
        }
        if (userId != -1 && !userId.equals(commodity.getUserId()) && order.getReceiptTime() != null) {
            res.put("errMsg", "已确认收货，无法删除订单");
            return gson.toJson(res);
        }
        String msg = orderDao.delete(id);
        if (msg != null) {
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        commodityDao.buy(order.getCommodityId(), -1);
        Notice noticeUpdate = new Notice(order.getSellerId(), "order delete", "您有一条订单取消，请注意查看。");
        noticeUpdate.setOrderId(order.getId());
        noticeUpdate.setCommodityId(order.getCommodityId());
        noticeService.add(noticeUpdate);
        // 如果不是买家自己取消的订单，也通知用买家
        if (!userId.equals(order.getUserId())) {
            noticeUpdate.setUserId(order.getUserId());
            noticeService.add(noticeUpdate);
        }
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String update(Order order, Integer userId) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("status", 0);
        Integer id = order.getId();
        Order oldOrder = orderDao.findById(id);
        if (oldOrder == null) {
            res.put("errMsg", "订单不存在");
            return gson.toJson(res);
        }
        // 买家，管理员有权修改
        if (userId != -1 && !Objects.equals(oldOrder.getUserId(), userId)) {
            res.put("errMsg", "无操作权限");
            return gson.toJson(res);
        }
        if (userId != -1 && oldOrder.getShip()) {
            res.put("errMsg", "已发货，请联系卖家修改订单");
            return gson.toJson(res);
        }
        if (userId != -1 && oldOrder.getReceiptTime() != null) {
            res.put("errMsg", "已确认收获，无法修改订单");
            return gson.toJson(res);
        }
        if (order.getPhone() != null && !order.getPhone().equals("")) {
            oldOrder.setPhone(order.getPhone());
        }
        if (order.getAddress() != null && !order.getAddress().equals("")) {
            oldOrder.setAddress(order.getAddress());
        }
        if (order.getNickname() != null && !order.getNickname().equals("")) {
            oldOrder.setNickname(order.getNickname());
        }
        if (order.getRemark() != null && !order.getRemark().equals("")) {
            oldOrder.setRemark(order.getRemark());
        }
        String msg = orderDao.update(oldOrder);
        if (msg != null) {
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        Notice noticeUpdate = new Notice(oldOrder.getSellerId(), "order update", "买家更新了一条订单信息，请注意查看。");
        noticeUpdate.setOrderId(oldOrder.getId());
        noticeUpdate.setCommodityId(oldOrder.getCommodityId());
        noticeService.add(noticeUpdate);
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String get(Integer id, Integer userId) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("status", 0);
        Order order = orderDao.findById(id);
        if (order == null) {
            res.put("errMsg", "订单不存在");
            return gson.toJson(res);
        }
        Commodity commodity = commodityDao.findById(order.getCommodityId());
        if (userId != -1 && !userId.equals(commodity.getUserId()) && !Objects.equals(order.getUserId(), userId)) {
            res.put("errMsg", "无操作权限");
            return gson.toJson(res);
        }
        return gson.toJson(order);
    }

    @Override
    public String ship(Integer id, Integer userId) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("status", 0);
        Order order = orderDao.findById(id);
        if (order == null) {
            res.put("errMsg", "订单不存在");
            return gson.toJson(res);
        }
        if (order.getShip()) {
            res.put("errMsg", "已发货，不能重复发货");
            return gson.toJson(res);
        }
        // 只有卖家有权利发货
        Commodity commodity = commodityDao.findById(order.getCommodityId());
        if (!userId.equals(commodity.getUserId())) {
            res.put("errMsg", "无操作权限");
            return gson.toJson(res);
        }
        String msg = orderDao.ship(id);
        if (msg != null) {
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        Notice noticeShip = new Notice(order.getUserId(), "order ship", "您的订单已发货");
        noticeShip.setOrderId(order.getId());
        noticeShip.setCommodityId(order.getCommodityId());
        noticeService.add(noticeShip);
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String receipt(Integer id, Integer userId) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("status", 0);
        Order order = orderDao.findById(id);
        if (order == null) {
            res.put("errMsg", "订单不存在");
            return gson.toJson(res);
        }
        if (order.getReceiptTime() != null) {
            res.put("errMsg", "已确认收货，不能重复确认");
            return gson.toJson(res);
        }
        if (!userId.equals(order.getUserId())) {
            res.put("errMsg", "无操作权限");
            return gson.toJson(res);
        }
        String msg = orderDao.receipt(id);
        if (msg != null) {
            res.put("errMsg", msg);
            return gson.toJson(res);
        }
        Notice noticeReceipt = new Notice(order.getSellerId(), "order receipt", "您有一条订单已确认收货，请注意查看。");
        noticeReceipt.setOrderId(order.getId());
        noticeReceipt.setCommodityId(order.getCommodityId());
        noticeService.add(noticeReceipt);
        res.put("status", 1);
        return gson.toJson(res);
    }

    @Override
    public String getOrderList(int page, int pageSize, String order, Integer userId, Integer commodityId, Integer sellerId, Boolean isShip) {
        HashMap<String, Object> res = new HashMap<>();
        if (order == null || order.equals("")) {
            order = "order_time";
        }
        switch (order) {
            case "id":
            case "user_id":
            case "commodity_id":
            case "is_ship":
            case "ship_time":
            case "order_time":
                break;
            default:
                res.put("status", 0);
                res.put("errMsg", "order不存在");
                return gson.toJson(res);
        }
        res = orderDao.getOrderList(page, pageSize, order, userId, commodityId, sellerId, isShip);
        return gson.toJson(res);
    }
}
