package ink.wyy.dao.impl;

import ink.wyy.bean.Star;
import ink.wyy.dao.StarDao;
import ink.wyy.util.C3P0Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StarDaoImpl implements StarDao {

    @Override
    public Star add(Star star) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "insert into tb_star (commodity_id, user_id) VALUES (?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, star.getCommodityId());
            statement.setInt(2, star.getUserId());
            int num = statement.executeUpdate();
            if (num == 1) {
                Statement statement1 = con.createStatement();
                ResultSet rs = statement1.executeQuery("SELECT LAST_INSERT_ID()");
                rs.next();
                star.setId(rs.getInt(1));
            } else {
                star.setErrMsg("添加失败");
            }
            return star;
        } catch (SQLException e) {
            System.out.println(e);
            star.setErrMsg("添加失败");
            return star;
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public String delete(Integer id) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "delete from tb_star where id=?";
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
    public Integer getNumByCommodityId(Integer commodityId) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "select count(*) from tb_star where commodity_id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, commodityId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -1;
            }
        } catch (SQLException e) {
            System.out.println(e);
            return -1;
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public HashMap<String, Object> getList(Integer page, Integer pageSize, Integer commodityId, Integer userId, String order, Boolean desc) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "select * from tb_star as a inner join tb_commodity tc on a.commodity_id = tc.id where a.user_id=? ";
            String totSql = "select count(*) from tb_star where user_id=? ";
            int idx = 1;
            if (commodityId != null) {
                sql += " and tc.id=? ";
                totSql += " and commodityId=? ";
                idx++;
            }
            sql += " order by a." + order;
            if (desc) sql += " desc ";
            sql += " limit ?, ?";
            PreparedStatement statement = con.prepareStatement(sql);
            PreparedStatement statement1 = con.prepareStatement(totSql);
            statement1.setInt(1, userId);
            statement.setInt(1, userId);
            if (idx != 1) {
                statement1.setInt(idx, commodityId);
                statement.setInt(idx, commodityId);
            }
            idx++;
            statement.setInt(idx, (page - 1) * pageSize);
            statement.setInt(idx + 1, pageSize);
            ResultSet resultSet = statement1.executeQuery();
            resultSet.next();
            int allNum = resultSet.getInt(1);
            int pageNum = allNum / pageSize;
            if (allNum % pageSize != 0) pageNum++;
            ResultSet rs = statement.executeQuery();

            HashMap<String, Object> res = new HashMap<>();

            res.put("page", page);
            res.put("page_num", pageNum);

            int num = 0;
            List<HashMap<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                num++;
                HashMap<String, Object> map = new HashMap<>();
                map.put("title", rs.getString("tc.title"));
                map.put("id", rs.getInt("a.id"));
                map.put("commodity_id", rs.getInt("tc.id"));
                map.put("seller_id", rs.getInt("tc.user_id"));
                map.put("pic", rs.getString("tc.pic"));
                map.put("price", rs.getDouble("tc.price"));
                map.put("stock", rs.getInt("tc.stock"));
                map.put("sales", rs.getInt("tc.sales"));
                list.add(map);
            }
            res.put("stars", list);
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
    public Star findById(Integer commodityId, Integer userId) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "select * from tb_star where user_id=? and commodity_id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, commodityId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Star star = new Star();
                star.setId(rs.getInt("id"));
                star.setCommodityId(rs.getInt("commodity_id"));
                star.setUserId(rs.getInt("user_id"));
                return star;
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
