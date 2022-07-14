package ink.wyy.dao;

import ink.wyy.bean.Commodity;
import ink.wyy.util.C3P0Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommodityDaoImpl implements CommodityDao {

    @Override
    public Commodity add(Commodity commodity) {
        Connection con = C3P0Util.getConnection();
        String sql = "insert into commodity (title, content, user_id, category, pic, price, release_time, update_time) VALUES " +
                "(?, ?, ?, ?, ?, ?, now(), now())";
        try {
            con.setAutoCommit(false);
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, commodity.getTitle());
            statement.setString(2, commodity.getContent());
            statement.setInt(3, commodity.getUserId());
            statement.setString(4, commodity.getCategory());
            statement.setString(5, commodity.getPicUri());
            statement.setDouble(6, commodity.getPrice());
            Integer num = statement.executeUpdate();
            if (num == 1) {
                Statement statement1 = con.createStatement();
                ResultSet rs = statement1.executeQuery("SELECT LAST_INSERT_ID()");
                rs.next();
                commodity.setId(rs.getInt(1));
                con.commit();
            } else {
                commodity.setErrorMsg("插入失败");
                con.rollback();
            }
            return commodity;
        } catch (SQLException e) {
            System.out.println(e);
            commodity.setErrorMsg("插入失败");
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
            return commodity;
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public String update(Commodity commodity) {
        Connection con = C3P0Util.getConnection();
        String sql = "update commodity set title=?, content=?, category=?, pic=?, price=? where id=?";
        try {
            con.setAutoCommit(false);
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, commodity.getTitle());
            statement.setString(2, commodity.getContent());
            statement.setString(3, commodity.getCategory());
            statement.setString(4, commodity.getPicUri());
            statement.setDouble(5, commodity.getPrice());
            statement.setInt(6, commodity.getId());
            Integer num = statement.executeUpdate();
            if (num == 1) {
                con.commit();
                return null;
            } else {
                con.rollback();
                return "更新失败";
            }
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
            return "更新失败";
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public Boolean delete(Integer id) {
        Connection con = C3P0Util.getConnection();
        String sql = "delete from commodity where id=" + id.toString();
        try {
            con.setAutoCommit(false);
            Statement statement = con.createStatement();
            Integer num = statement.executeUpdate(sql);
            if (num == 1) {
                con.commit();
                return true;
            } else {
                con.rollback();
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
            return false;
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public Commodity findById(Integer id) {
        Connection con = C3P0Util.getConnection();
        String sql = "select * from commodity where id=" + id.toString();
        try {
            con.setAutoCommit(false);
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            if (!rs.next()) {
                con.rollback();
                return null;
            }
            String title = rs.getString("title");
            String content = rs.getString("content");
            String category = rs.getString("category");
            String createDate = rs.getString("release_time");
            String updateDate = rs.getString("update_time");
            String picUri = rs.getString("pic");
            Double price = rs.getDouble("price");
            Integer userId = rs.getInt("user_id");
            Commodity commodity = new Commodity(title, content, category);
            commodity.setId(id);
            commodity.setPicUri(picUri);
            commodity.setUserId(userId);
            commodity.setCreateDate(createDate);
            commodity.setUpdateDate(updateDate);
            commodity.setPrice(price);
            con.commit();
            return commodity;
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
        } finally {
            C3P0Util.close(con);
        }
        return null;
    }

    @Override
    public HashMap<String, Object> getList(int page, int pageSize, String order, String category, Integer userId) {
        Connection con = C3P0Util.getConnection();
        try {
            con.setAutoCommit(false);
            String sql = "select id, title, pic, price, category, release_time, update_time, user_id from commodity where ";
            String totSql = "select count(*) as cnt from commodity where ";
            int bias = 0;
            if (category != null) {
                sql += "category=?";
                totSql += "category=\"" + category + "\"";
                bias++;
            }
            if (userId != null) {
                if (category != null) {
                    sql += " AND ";
                    totSql += " AND ";
                }
                sql += "user_id=?";
                totSql += "user_id=" + userId;
                bias++;
            }
            if (bias == 0) {
                totSql += " 1=1";
                sql += " 1=1 ";
            }
            sql += " order by " + order + " limit ?, ?";
            PreparedStatement statement = con.prepareStatement(sql);
            Statement statement1 = con.createStatement();
            ResultSet totSet = statement1.executeQuery(totSql);
            totSet.next();
            int allNum = totSet.getInt(1);
            if (category != null) {
                statement.setString(1, category);
            }
            if (userId != null) {
                if (category != null) statement.setInt(2, userId);
                else statement.setInt(1, userId);
            }
            statement.setInt(bias + 1, (page - 1) * pageSize);
            statement.setInt(bias + 2, pageSize);
            ResultSet rs = statement.executeQuery();
            HashMap<String, Object> map = new HashMap<>();
            int num = 0;
            map.put("page", page);
            int pageNum = allNum / pageSize;
            if (allNum % pageSize != 0) pageNum++;
            map.put("pageNum", pageNum);
            List<HashMap<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                num++;
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String category1 = rs.getString("category");
                String releaseTime = rs.getString("release_time");
                String updateTime = rs.getString("update_time");
                String picUri = rs.getString("pic");
                Double price = rs.getDouble("price");
                int userId1 = rs.getInt("user_id");
                HashMap<String, Object> thisMap = new HashMap<>();
                thisMap.put("id", id);
                thisMap.put("category", category1);
                thisMap.put("release_time", releaseTime);
                thisMap.put("update_time", updateTime);
                thisMap.put("title", title);
                thisMap.put("pic", picUri);
                thisMap.put("price", price);
                thisMap.put("user_id", userId1);
                list.add(thisMap);
            }
            map.put("num", num);
            map.put("commodities", list);
            con.commit();
            return map;
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
        } finally {
            C3P0Util.close(con);
        }
        return null;
    }
}
