package ink.wyy.dao.impl;

import ink.wyy.bean.User;
import ink.wyy.dao.UserDao;
import ink.wyy.util.C3P0Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public String insert(User user) {
        Connection con = C3P0Util.getConnection();
        try {
            con.setAutoCommit(false);
            String sql = "insert into tb_user (username, password, create_date, signature, avatar_uri, level)" +
                    " values (?,?,now(),?,?,?);";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getSignature());
            statement.setString(4, user.getAvatarUri());
            statement.setInt(5, user.getLevel());
            int num = statement.executeUpdate();
            statement.close();
            if (num == 1) {
                con.commit();
                return null;
            } else {
                con.rollback();
                return "添加错误";
            }
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
            return "添加错误";
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public String delete(int id) {
        Connection con = C3P0Util.getConnection();
        try {
            con.setAutoCommit(false);
            String sql = "delete from tb_user where id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            int num = statement.executeUpdate();
            statement.close();
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
            } catch (SQLException e1) {
                System.out.println(e1);
            }
            return "删除失败";
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public String update(Integer id, User user) {
        Connection con = C3P0Util.getConnection();
        String sql = "update tb_user set username=?, password=?, signature=?, avatar_uri=?, level=?, phone=? where id=?";
        try {
            con.setAutoCommit(false);
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getSignature());
            statement.setString(4, user.getAvatarUri());
            statement.setInt(5, user.getLevel());
            statement.setString(6, user.getPhone());
            statement.setInt(7, user.getId());
            int num = statement.executeUpdate();
            statement.close();
            if (num == 1) {
                con.commit();
                return null;
            } else {
                con.rollback();
                return "更改失败";
            }
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
            return "更改失败";
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public String updateUserInfo(Integer id, User user) {
        Connection con = C3P0Util.getConnection();
        String sql = "update tb_user set signature=?, avatar_uri=?, level=?, phone=? where id=?";
        try {
            con.setAutoCommit(false);
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, user.getSignature());
            statement.setString(2, user.getAvatarUri());
            statement.setInt(3, user.getLevel());
            statement.setString(4, user.getPhone());
            statement.setInt(5, id);
            int num = statement.executeUpdate();
            statement.close();
            if (num == 1) {
                con.commit();
                return null;
            } else {
                con.rollback();
                return "更改失败";
            }
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
            return "更改失败";
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public User findByUsername(String username) {
        Connection con = C3P0Util.getConnection();
        String sql = "select * from tb_user where username=?";
        try {
            con.setAutoCommit(false);
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            User user = null;
            while (rs.next()) {
                user = new User(rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("id"));
                user.setAvatarUri(rs.getString("avatar_uri"));
                user.setCreateDate(rs.getDate("create_date"));
                user.setSignature(rs.getString("signature"));
                user.setPhone(rs.getString("phone"));
                user.setLevel(rs.getInt("level"));
                break;
            }
            con.commit();
            rs.close();
            statement.close();
            return user;
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
            return null;
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public User findById(int id) {
        Connection con = C3P0Util.getConnection();
        String sql = "select * from tb_user where id=?";
        try {
            con.setAutoCommit(false);
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            User user = null;
            while (rs.next()) {
                user = new User(rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("id"));
                user.setAvatarUri(rs.getString("avatar_uri"));
                user.setCreateDate(rs.getDate("create_date"));
                user.setSignature(rs.getString("signature"));
                user.setPhone(rs.getString("phone"));
                user.setLevel(rs.getInt("level"));
                break;
            }
            con.commit();
            rs.close();
            statement.close();
            return user;
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
            return null;
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public HashMap<String, Object> getUserList(int page, int pageSize, String order, boolean desc) {
        Connection con = C3P0Util.getConnection();
        try {
            con.setAutoCommit(false);
            String sql = "select * from tb_user order by " + order + " limit ?, ?";
            if (desc) sql = "select * from tb_user order by " + order + " desc limit ?, ?";
            String totSql = "select count(*) from tb_user";
            Statement statement1 = con.createStatement();

            ResultSet resultSet = statement1.executeQuery(totSql);
            resultSet.next();
            int allNum = resultSet.getInt(1);
            int pageNum = allNum / pageSize;
            if (allNum % pageSize != 0) pageNum++;

            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, (page - 1) * pageSize);
            statement.setInt(2, pageSize);

            HashMap<String, Object> res = new HashMap<>();
            int num = 0;
            res.put("page", page);
            res.put("page_num", pageNum);
            List<HashMap<String, Object>> list = new ArrayList<>();
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                num++;
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("username", rs.getString("username"));
                map.put("avatar_uri", rs.getString("avatar_uri"));
                map.put("signature", rs.getString("signature"));
                map.put("level", rs.getInt("level"));
                map.put("phone", rs.getString("phone"));
                map.put("create_date", rs.getString("create_date"));
                list.add(map);
            }
            res.put("num", num);
            res.put("users", list);
            con.commit();
            return res;
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException ee) {
                System.out.println(ee);
            }
        } finally {
            C3P0Util.close(con);
        }
        return null;
    }
}
