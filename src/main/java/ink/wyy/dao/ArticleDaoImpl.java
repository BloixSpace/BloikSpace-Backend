package ink.wyy.dao;

import ink.wyy.bean.Article;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArticleDaoImpl implements ArticleDao{
    private final Connection con;

    public ArticleDaoImpl(String url, String DBName, String user, String pwd) {
        this.con = GetDBConnection.connectDB(url, DBName, user, pwd);
        try {
            con.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public Article add(Article article) {
        String sql = "insert into articles (title, content, user_id, category, release_time, update_time) VALUES " +
                "(?, ?, ?, ?, now(), now())";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, article.getTitle());
            statement.setString(2, article.getContent());
            statement.setInt(3, article.getUserId());
            statement.setString(4, article.getCategory());
            Integer num = statement.executeUpdate();
            if (num == 1) {
                Statement statement1 = con.createStatement();
                ResultSet rs = statement1.executeQuery("SELECT LAST_INSERT_ID()");
                rs.next();
                article.setId(rs.getInt(1));
                con.commit();
            } else {
                article.setErrorMsg("插入失败");
                con.rollback();
            }
            return article;
        } catch (SQLException e) {
            System.out.println(e);
            article.setErrorMsg("插入失败");
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
            return article;
        }
    }

    @Override
    public String update(Article article) {
        String sql = "update articles set title=?, content=?, category=?, update_time=now() where id=?";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, article.getTitle());
            statement.setString(2, article.getContent());
            statement.setString(3, article.getCategory());
            statement.setInt(4, article.getId());
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
        }
    }

    @Override
    public Boolean delete(Integer id) {
        String sql = "delete from articles where id=" + id.toString();
        try {
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
        }
    }

    @Override
    public Article findById(Integer id) {
        String sql = "select * from articles where id=" + id.toString();
        try {
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
            Integer userId = rs.getInt("user_id");
            Article article = new Article(title, content, category);
            article.setId(id);
            article.setUserId(userId);
            article.setCreateDate(createDate);
            article.setUpdateDate(updateDate);
            con.commit();
            return article;
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
        }
        return null;
    }

    @Override
    public HashMap<String, Object> getList(int page, int pageSize, String order, String category, Integer userId) {
        try {
            String sql = "select id, title, category, release_time, update_time, user_id from articles where ";
            String totSql = "select count(*) as cnt from articles where ";
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
            sql += " order by ? limit ?, ?";
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
            statement.setString(bias + 1, order);
            statement.setInt(bias + 2, (page - 1) * pageSize);
            statement.setInt(bias + 3, pageSize);
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
                int userId1 = rs.getInt("user_id");
                HashMap<String, Object> thisMap = new HashMap<>();
                thisMap.put("id", id);
                thisMap.put("category", category1);
                thisMap.put("release_time", releaseTime);
                thisMap.put("update_time", updateTime);
                thisMap.put("title", title);
                thisMap.put("user_id", userId1);
                list.add(thisMap);
            }
            map.put("num", num);
            map.put("articles", list);
            con.commit();
            return map;
        } catch (SQLException e) {
            System.out.println(e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                System.out.println(e1);
            }
        }
        return null;
    }
}
