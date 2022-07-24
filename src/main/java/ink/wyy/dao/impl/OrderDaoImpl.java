package ink.wyy.dao.impl;

import ink.wyy.bean.Commodity;
import ink.wyy.bean.Order;
import ink.wyy.dao.OrderDao;
import ink.wyy.util.C3P0Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 订单系统
 */

public class OrderDaoImpl implements OrderDao {

    public Order add(Order order) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "insert into tb_order (user_id, commodity_id, address, phone, remark, nickname, seller_id, buy_num, order_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, now())";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, order.getUserId());
            statement.setInt(2, order.getCommodityId());
            statement.setString(3, order.getAddress());
            statement.setString(4, order.getPhone());
            statement.setString(5, order.getRemark());
            statement.setString(6, order.getNickname());
            statement.setInt(7, order.getSellerId());
            statement.setInt(8, order.getBuyNum());
            int num = statement.executeUpdate();
            if (num == 1) {
                Statement statement1 = con.createStatement();
                ResultSet rs = statement1.executeQuery("SELECT LAST_INSERT_ID()");
                rs.next();
                order.setId(rs.getInt(1));
            } else {
                order.setErrorMsg("插入失败");
            }
            return order;
        } catch (SQLException e) {
            System.out.println(e);
            order.setErrorMsg("插入失败");
            return order;
        } finally {
            C3P0Util.close(con);
        }
    }

    public String delete(Integer id) {
        Connection con = C3P0Util.getConnection();
        try {
            con.setAutoCommit(false);
            String sql = "delete from tb_order where id=?;";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            int num = statement.executeUpdate();
            if (num == 1) {
                con.commit();
                return null;
            } else {
                con.rollback();
                return "删除失败";
            }
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException ee) {
                System.out.println(ee);
            }
            return "删除失败";
        } finally {
            C3P0Util.close(con);
        }
    }

    public Order findById(Integer id) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "select * from tb_order as a inner join tb_commodity tc on a.commodity_id = tc.id where a.id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Integer userId = rs.getInt("a.user_id");
                Integer commodityId = rs.getInt("a.commodity_id");
                Boolean isShip = rs.getInt("a.is_ship") == 1;
                String receiptTime = rs.getString("a.receipt_time");
                String address = rs.getString("a.address");
                String phone = rs.getString("a.phone");
                String nickname = rs.getString("a.nickname");
                String remark = rs.getString("a.remark");
                String orderTime = rs.getString("a.order_time");
                String shipTime = rs.getString("a.ship_time");
                Integer sellerId = rs.getInt("a.seller_id");
                String commodityTitle = rs.getString("tc.title");
                String commodityPic = rs.getString("tc.pic");
                Integer buyNum = rs.getInt("a.buy_num");
                Order order = new Order(userId, commodityId, address, phone);
                order.setOrderTime(orderTime);
                order.setCommodityTitle(commodityTitle);
                order.setCommodityPic(commodityPic);
                order.setShip(isShip);
                order.setId(id);
                order.setSellerId(sellerId);
                order.setNickname(nickname);
                order.setRemark(remark);
                order.setReceiptTime(receiptTime);
                order.setBuyNum(buyNum);
                if (shipTime != null && !shipTime.equals("")) {
                    order.setShipTime(shipTime);
                }
                return order;
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public String update(Order order) {
        Connection con = C3P0Util.getConnection();
        try {
            con.setAutoCommit(false);
            String sql = "update tb_order set phone=?, address=?, remark=?, nickname=? where id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, order.getPhone());
            statement.setString(2, order.getAddress());
            statement.setString(3, order.getRemark());
            statement.setString(4, order.getNickname());
            statement.setInt(5, order.getId());
            int num = statement.executeUpdate();
            if (num == 1) {
                con.commit();
                return null;
            } else {
                return "更新失败";
            }
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException ee) {
                System.out.println(ee);
            }
            return "更新失败";
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public String ship(Integer id) {
        Connection con = C3P0Util.getConnection();
        try {
            con.setAutoCommit(false);
            String sql = "update tb_order set is_ship=1, ship_time=now() where id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            int num = statement.executeUpdate();
            if (num == 1) {
                con.commit();
                return null;
            } else {
                return "发货失败";
            }
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException ee) {
                System.out.println(ee);
            }
            return "发货失败";
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public String receipt(Integer id) {
        Connection con = C3P0Util.getConnection();
        try {
            con.setAutoCommit(false);
            String sql = "update tb_order set receipt_time=now() where id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            int num = statement.executeUpdate();
            if (num == 1) {
                con.commit();
                return null;
            } else {
                return "确认收货失败";
            }
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException ee) {
                System.out.println(ee);
            }
            return "确认收货失败";
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public HashMap<String, Object> getOrderList(int page, int pageSize, String order, Integer userId, Integer commodityId, Integer sellerId, Boolean isShip) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "select * from tb_order";
            String totSql = "select count(*) from tb_order";
            if (userId != null || commodityId != null || isShip != null || sellerId != null) {
                sql += " where ";
                totSql += " where ";
                if (userId != null) {
                    sql += "user_id=? ";
                    totSql += "user_id=? ";
                }
                if (commodityId != null) {
                    if (userId != null) {
                        sql += "and ";
                        totSql += "and ";
                    }
                    sql += "commodity_id=? ";
                    totSql += "commodity_id=? ";
                }
                if (sellerId != null) {
                    if (userId != null || commodityId != null) {
                        sql += "and ";
                        totSql += "and ";
                    }
                    sql += "seller_id=? ";
                    totSql += "seller_id=? ";
                }
                if (isShip != null) {
                    if (userId != null || commodityId != null || sellerId != null) {
                        sql += "and ";
                        totSql += "and ";
                    }
                    sql += "is_ship=? ";
                    totSql += "is_ship=? ";
                }
            }
            sql += " order by " + order + " desc limit ?, ?";
            PreparedStatement statement = con.prepareStatement(sql);
            PreparedStatement statement1 = con.prepareStatement(totSql);
            int x = 1;
            if (userId != null || commodityId != null || isShip != null || sellerId != null) {
                if (userId != null) {
                    statement.setInt(x, userId);
                    statement1.setInt(x, userId);
                    x++;
                }
                if (commodityId != null) {
                    statement.setInt(x, commodityId);
                    statement1.setInt(x, commodityId);
                    x++;
                }
                if (sellerId != null) {
                    statement.setInt(x, sellerId);
                    statement1.setInt(x, sellerId);
                    x++;
                }
                if (isShip != null) {
                    if (isShip) {
                        statement.setInt(x, 1);
                        statement1.setInt(x, 1);
                    } else {
                        statement.setInt(x, 0);
                        statement1.setInt(x, 0);
                    }
                    x++;
                }
            }
            statement.setInt(x, (page - 1) * pageSize);
            statement.setInt(x + 1, pageSize);
            ResultSet totSet = statement1.executeQuery();
            totSet.next();
            int allNum = totSet.getInt(1);
            int pageNum = allNum / pageSize;
            if (allNum % pageSize != 0) pageNum++;
            totSet.close();
            statement1.close();
            ResultSet rs = statement.executeQuery();
            HashMap<String, Object> res = new HashMap<>();
            res.put("page", page);
            res.put("page_num", pageNum);
            List<Order> list = new ArrayList<>();
            int num = 0;
            while (rs.next()) {
                num++;
                Integer id = rs.getInt("id");
                Integer userId1 = rs.getInt("user_id");
                Integer commodityId1 = rs.getInt("commodity_id");
                Integer sellerId1 = rs.getInt("seller_id");
                Boolean isShip1 = rs.getInt("is_ship") == 1;
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                String nickname = rs.getString("nickname");
                String remark = rs.getString("remark");
                String orderTime = rs.getString("order_time");
                String shipTime = rs.getString("ship_time");
                String receiptTime = rs.getString("receipt_time");
                Integer buyNum = rs.getInt("buy_num");
                Order order1 = new Order(userId1, commodityId1, address, phone);
                order1.setOrderTime(orderTime);
                order1.setId(id);
                order1.setSellerId(sellerId1);
                order1.setShip(isShip1);
                order1.setNickname(nickname);
                order1.setRemark(remark);
                order1.setReceiptTime(receiptTime);
                order1.setBuyNum(buyNum);
                if (shipTime != null && !shipTime.equals("")) {
                    order1.setShipTime(shipTime);
                }
                list.add(order1);
            }
            res.put("orders", list);
            res.put("num", num);
            return res;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        } finally {
            C3P0Util.close(con);
        }
    }
}
