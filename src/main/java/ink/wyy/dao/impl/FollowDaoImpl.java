package ink.wyy.dao.impl;

import ink.wyy.dao.FollowDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FollowDaoImpl implements FollowDao {

    Connection con;

    public FollowDaoImpl(String url, String DBName, String user, String pwd) {
        this.con = GetDBConnection.connectDB(url, DBName, user, pwd);
        try {
            con.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public Boolean add(Integer userId, Integer followId) {
        try {
            String sql = "insert into tb_follower (user_id, follow_id) values (?, ?);";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, followId);
            int num = statement.executeUpdate();
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
            } catch (SQLException ee) {
                System.out.println(ee);
            }
        }
        return false;
    }

    @Override
    public Boolean del(Integer userId, Integer followId) {
        try {
            String sql = "delete from tb_follower where user_id=? and follow_id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, followId);
            int num = statement.executeUpdate();
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
            } catch (SQLException ee) {
                System.out.println(ee);
            }
        }
        return false;
    }

    @Override
    public HashMap<String, Object> findFans(Integer userId, Integer page, Integer pageSize) {
        try {
            String sql = "select user_id from tb_follower where follow_id=? order by user_id limit ?, ?";
            String totSql = "select count(*) from tb_follower where follow_id=" + userId;
            PreparedStatement statement = con.prepareStatement(sql);

            statement.setInt(1, userId);
            statement.setInt(2, (page - 1) * pageSize);
            statement.setInt(3, pageSize);
            ResultSet rs = statement.executeQuery();
            HashMap<String, Object> map = new HashMap<>();

            map.put("page", page);
            map.put("pageSize", pageSize);
            Statement statement1 = con.createStatement();
            ResultSet set1 = statement1.executeQuery(totSql);
            set1.next();
            int pageNum = set1.getInt(1);
            pageNum /= pageSize;
            if (set1.getInt(1) % pageSize != 0) pageNum++;
            map.put("pageNum", pageNum);
            int num = 0;
            List<Object> list = new ArrayList<>();
            while (rs.next()) {
                num++;
                Integer id = rs.getInt(1);
                String sql1 = "select username, avatar_uri, signature from users where id=" + id;
                ResultSet set = statement1.executeQuery(sql1);
                set.next();
                String username = set.getString(1);
                String avatar_uri = set.getString(2);
                String signature = set.getString(3);
                HashMap<String, Object> map1 = new HashMap<>();
                map1.put("id", id);
                map1.put("username", username);
                map1.put("avatar_uri", avatar_uri);
                map1.put("signature", signature);
                list.add(map1);
            }
            map.put("num", num);
            map.put("users", list);
            con.commit();
            return map;
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException ee) {
                System.out.println(ee);
            }
        }
        return null;
    }

    @Override
    public HashMap<String, Object> findFollowers(Integer userId, Integer page, Integer pageSize) {
        try {
            String sql = "select follow_id from tb_follower where user_id=? order by follow_id limit ?, ?";
            String totSql = "select count(*) from tb_follower where user_id=" + userId;
            PreparedStatement statement = con.prepareStatement(sql);

            statement.setInt(1, userId);
            statement.setInt(2, (page - 1) * pageSize);
            statement.setInt(3, pageSize);
            ResultSet rs = statement.executeQuery();
            HashMap<String, Object> map = new HashMap<>();

            map.put("page", page);
            map.put("pageSize", pageSize);
            Statement statement1 = con.createStatement();
            ResultSet set1 = statement1.executeQuery(totSql);
            set1.next();
            int pageNum = set1.getInt(1);
            pageNum /= pageSize;
            if (set1.getInt(1) % pageSize != 0) pageNum++;
            map.put("pageNum", pageNum);

            List<Object> list = new ArrayList<>();
            int num = 0;
            while (rs.next()) {
                num++;
                Integer id = rs.getInt(1);
                String sql1 = "select username, avatar_uri, signature from users where id=" + id;
                ResultSet set = statement1.executeQuery(sql1);
                set.next();
                String username = set.getString(1);
                String avatar_uri = set.getString(2);
                String signature = set.getString(3);
                HashMap<String, Object> map1 = new HashMap<>();
                map1.put("id", id);
                map1.put("username", username);
                map1.put("avatar_uri", avatar_uri);
                map1.put("signature", signature);
                list.add(map1);
            }
            map.put("num", num);
            map.put("users", list);
            con.commit();
            return map;
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException ee) {
                System.out.println(ee);
            }
        }
        return null;
    }

    @Override
    public HashMap<String, Object> findFriends(Integer userId, Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public Boolean exist(Integer userId, Integer followId) {
        try {
            String sql = "select count(*) from tb_follower where user_id=? and follow_id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, followId);
            ResultSet rs = statement.executeQuery();
            rs.next();
            int num = rs.getInt(1);
            con.commit();
            return num != 0;
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException ee) {
                System.out.println(ee);
            }
        }
        return null;
    }
}
