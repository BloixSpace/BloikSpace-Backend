package ink.wyy.dao.impl;

import ink.wyy.bean.Notice;
import ink.wyy.dao.NoticeDao;
import ink.wyy.util.C3P0Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoticeDaoImpl implements NoticeDao {

    @Override
    public String add(Notice notice) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "insert into tb_notice (user_id, class, order_id, commodity_id, content, from_user, unread, time) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, now())";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, notice.getUserId());
            statement.setString(2, notice.getClassification());
            if (notice.getOrderId() == null) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, notice.getOrderId());
            }
            statement.setInt(4, notice.getCommodityId());
            statement.setString(5, notice.getContent());
            if (notice.getOrderId() == null) {
                statement.setNull(6, Types.INTEGER);
            } else {
                statement.setInt(6, notice.getFromUser());
            }
            statement.setInt(7, 1);
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
    public String read(Integer id) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "update tb_notice set unread=0 where id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            int num = statement.executeUpdate();
            if (num == 1) {
                return null;
            } else {
                return "更改失败";
            }
        } catch (SQLException e) {
            System.out.println(e);
            return "更改失败";
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public String delete(Integer id) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "delete from tb_notice where id=?";
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
    public Notice findById(Integer id) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "select * from tb_notice where id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Notice notice = new Notice();
                notice.setId(rs.getInt("id"));
                notice.setUserId(rs.getInt("user_id"));
                notice.setOrderId(rs.getInt("order_id"));
                notice.setCommodityId(rs.getInt("commodity_id"));
                notice.setClassification(rs.getString("class"));
                notice.setContent(rs.getString("content"));
                notice.setFromUser(rs.getInt("from_user"));
                notice.setUnread(rs.getInt("unread") == 1);
                notice.setTime(rs.getString("time"));
                return notice;
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
    public HashMap<String, Object> getList(Integer page, Integer pageSize, String order, Integer unread, Boolean desc, Integer userId) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "select * from tb_notice where user_id=? ";
            String totSql = "select count(*) from tb_notice where user_id=? ";
            if (unread != null) {
                sql += " and unread=? ";
                totSql += " and unread=? ";
            }
            sql += " order by " + order;
            if (desc) sql += " desc";
            sql += " limit ?, ?";
            PreparedStatement statement = con.prepareStatement(sql);
            PreparedStatement statement1 = con.prepareStatement(totSql);
            int x = 1;
            statement.setInt(x, userId);
            statement1.setInt(x, userId);
            x++;
            if (unread != null) {
                statement.setInt(x, unread);
                statement1.setInt(x, unread);
                x++;
            }
            statement.setInt(x, (page - 1) * pageSize);
            statement.setInt(x + 1, pageSize);
            ResultSet resultSet = statement1.executeQuery();
            resultSet.next();
            int allNum = resultSet.getInt(1);
            int pageNum = allNum / pageSize;
            if (allNum % pageSize != 0) pageNum++;

            ResultSet rs = statement.executeQuery();

            HashMap<String, Object> res = new HashMap<>();
            res.put("page", page);
            res.put("page_num", pageNum);

            List<Notice> list = new ArrayList<>();
            int num = 0;
            while (rs.next()) {
                num++;
                Notice notice = new Notice();
                notice.setId(rs.getInt("id"));
                notice.setUserId(rs.getInt("user_id"));
                notice.setOrderId(rs.getInt("order_id"));
                notice.setCommodityId(rs.getInt("commodity_id"));
                notice.setClassification(rs.getString("class"));
                notice.setContent(rs.getString("content"));
                notice.setFromUser(rs.getInt("from_user"));
                notice.setUnread(rs.getInt("unread") == 1);
                notice.setTime(rs.getString("time"));
                list.add(notice);
            }
            res.put("notices", list);
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
