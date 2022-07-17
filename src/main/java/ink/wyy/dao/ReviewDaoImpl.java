package ink.wyy.dao;

import ink.wyy.bean.Review;
import ink.wyy.util.C3P0Util;

import java.sql.*;
import java.util.HashMap;

public class ReviewDaoImpl implements ReviewDao {
    @Override
    public Review add(Review review) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "insert into tb_review (id, commodity_id, user_id, seller_id, star, content, update_time, order_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, now(), ?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, review.getId());
            statement.setInt(2, review.getCommodityId());
            statement.setInt(3, review.getUserId());
            statement.setInt(4, review.getSellerId());
            statement.setInt(5, review.getStar());
            statement.setString(6, review.getContent());
            statement.setInt(7, review.getOrderId());
            int num = statement.executeUpdate();
            if (num == 1) {
                Statement statement1 = con.createStatement();
                ResultSet rs = statement1.executeQuery("SELECT LAST_INSERT_ID()");
                rs.next();
                review.setId(rs.getInt(1));
            } else {
                review.setErrorMsg("插入失败");
            }
            return review;
        } catch (SQLException e) {
            System.out.println(e);
            review.setErrorMsg("插入失败");
            return review;
        } finally {
            C3P0Util.close(con);
        }
    }

    @Override
    public String delete(Integer id) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "delete from tb_review where id=?;";
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
    public String update(Review review) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "update tb_review set star=?, content=? where id=?;";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, review.getStar());
            statement.setString(2, review.getContent());
            statement.setInt(3, review.getId());
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
    public Review findById(Integer id) {
        return null;
    }

    public Review findByOrderId(Integer id) {
        return null;
    }

    @Override
    public HashMap<String, Object> getReviewList(int page, int pageSize, Integer orderId, Integer commodityId, String order) {
        return null;
    }
}
