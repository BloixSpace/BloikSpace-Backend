package ink.wyy.dao.impl;

import ink.wyy.bean.Cart;
import ink.wyy.dao.CartDao;
import ink.wyy.util.C3P0Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartDaoImpl implements CartDao {

    @Override
    public String add(Integer commodityId, Integer userId, Integer buyNum) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "insert into tb_cart (commodity_id, user_id, buy_num, time) values (?, ?, ?, now());";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, commodityId);
            statement.setInt(2, userId);
            statement.setInt(3, buyNum);
            int num = statement.executeUpdate();
            if (num == 1) {
                return null;
            } else {
                return "添加失败";
            }
        } catch (SQLException e) {
            System.out.println(e);
            return "添加失败";
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public String delete(Integer id) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "delete from tb_cart where id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            int num = statement.executeUpdate();
            if (num == 1) {
                return null;
            } else {
                return "删除失败";
            }
        } catch (SQLException e) {
            System.out.println(e);
            return "删除失败";
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public String update(Integer id, Integer buyNum) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "update tb_cart set buy_num=? where id=?;";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, buyNum);
            statement.setInt(2, id);
            int num = statement.executeUpdate();
            if (num == 1) {
                return null;
            } else {
                return "更新失败";
            }
        } catch (SQLException e) {
            System.out.println(e);
            return "更新失败";
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public HashMap<String, Object> getList(Integer page, Integer pageSize, String order, Integer userId) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "select b.id, a.id, a.title, a.pic, a.price, a.stock, a.user_id, b.buy_num from " +
                    "tb_cart as b join tb_commodity a on a.id = b.commodity_id " +
                    "where b.user_id=?";
            String totSql = "select count(*) from " +
                    "tb_cart as b join tb_commodity a on a.id = b.commodity_id " +
                    "where b.user_id=?";
            if (order != null) {
                sql += " order by " + order + " desc ";
            }
            if (page != null) {
                sql += " limit ?, ?";
            }
            PreparedStatement statement = con.prepareStatement(sql);
            PreparedStatement statement1 = con.prepareStatement(totSql);
            statement.setInt(1, userId);
            if (page != null) {
                statement.setInt(2, (page - 1) * pageSize);
                statement.setInt(3, pageSize);
            }
            statement1.setInt(1, userId);
            ResultSet resultSet = statement1.executeQuery();
            resultSet.next();
            ResultSet rs = statement.executeQuery();

            HashMap<String, Object> res = new HashMap<>();
            if (page != null) {
                int allNum = resultSet.getInt(1);
                int pageNum = allNum / pageSize;
                if (allNum % pageSize != 0) pageNum++;
                res.put("page_num", pageNum);
                res.put("page", page);
            }

            List<Map<String, Object>> list = new ArrayList<>();

            int num = 0;
            while (rs.next()) {
                num++;
                Integer id = rs.getInt(1);
                Integer commodityId = rs.getInt(2);
                String title = rs.getString(3);
                String pic = rs.getString(4);
                Double price = rs.getDouble(5);
                int stock = rs.getInt(6);
                Integer sellerId = rs.getInt(7);
                Integer buyNum = rs.getInt(8);
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("commodity_id", commodityId);
                map.put("title", title);
                map.put("pic", pic);
                map.put("price", price);
                map.put("stock", stock);
                map.put("seller_id", sellerId);
                map.put("buy_num", buyNum);
                list.add(map);
            }
            res.put("commodities", list);
            res.put("num", num);
            return res;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public String empty(Integer userId) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "delete from tb_cart where user_id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, userId);
            int num = statement.executeUpdate();
            if (num >= 1) {
                return null;
            } else {
                return "清空失败";
            }
        } catch (SQLException e) {
            System.out.println(e);
            return "清空失败";
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public Cart findById(Integer id) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "select * from tb_cart where id=?;";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Cart cart = new Cart();
                cart.setId(rs.getInt("id"));
                cart.setCommodityId(rs.getInt("commodity_id"));
                cart.setUserId(rs.getInt("user_id"));
                cart.setBuyNum(rs.getInt("buy_num"));
                return cart;
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
    public Cart findByCommodityId(Integer commodityId, Integer userId) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "select * from tb_cart where commodity_id=? and user_id=?;";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, commodityId);
            statement.setInt(2, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Cart cart = new Cart();
                cart.setId(rs.getInt("id"));
                cart.setCommodityId(rs.getInt("commodity_id"));
                cart.setUserId(rs.getInt("user_id"));
                cart.setBuyNum(rs.getInt("buy_num"));
                return cart;
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
}
