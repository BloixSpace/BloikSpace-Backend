package ink.wyy.dao;

import ink.wyy.bean.Review;
import ink.wyy.util.C3P0Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReviewDaoImpl implements ReviewDao {
    @Override
    public Review add(Review review) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "insert into tb_review (commodity_id, user_id, seller_id, star, content, update_time, order_id) " +
                    "VALUES (?, ?, ?, ?, ?, now(), ?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, review.getCommodityId());
            statement.setInt(2, review.getUserId());
            statement.setInt(3, review.getSellerId());
            statement.setInt(4, review.getStar());
            statement.setString(5, review.getContent());
            statement.setInt(6, review.getOrderId());
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
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "select * from tb_review where id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Integer commodityId = rs.getInt("commodity_id");
                Integer userId = rs.getInt("user_id");
                Integer sellerId = rs.getInt("seller_id");
                Integer star = rs.getInt("star");
                Integer orderId = rs.getInt("order_id");
                String content = rs.getString("content");
                String updateTime = rs.getString("update_time");

                Review review = new Review(orderId, star, content);
                review.setId(id);
                review.setCommodityId(commodityId);
                review.setUserId(userId);
                review.setSellerId(sellerId);
                review.setUpdateTime(updateTime);

                return review;
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

    public Review findByOrderId(Integer id) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "select * from tb_review where order_id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Integer reviewId = rs.getInt("id");
                Integer commodityId = rs.getInt("commodity_id");
                Integer userId = rs.getInt("user_id");
                Integer sellerId = rs.getInt("seller_id");
                Integer star = rs.getInt("star");
                Integer orderId = rs.getInt("order_id");
                String content = rs.getString("content");
                String updateTime = rs.getString("update_time");

                Review review = new Review(orderId, star, content);
                review.setCommodityId(commodityId);
                review.setId(reviewId);
                review.setUserId(userId);
                review.setSellerId(sellerId);
                review.setUpdateTime(updateTime);

                return review;
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
    public HashMap<String, Object> getReviewList(int page, int pageSize, Integer orderId, Integer commodityId, String order, Boolean desc) {
        Connection con = C3P0Util.getConnection();
        try {
            String sql = "select * from tb_review";
            String totSql = "select count(*) from tb_review";
            if (orderId != null || commodityId != null) {
                sql += " where ";
                totSql += " where ";
                if (orderId != null) {
                    sql += " order_id=? ";
                    totSql += "order_id=? ";
                }
                if (commodityId != null) {
                    if (orderId != null) {
                        sql += " and ";
                        totSql += " and ";
                    }
                    sql += " commodity_id=? ";
                    totSql += " commodity_id=? ";
                }
            }
            sql += " order by " + order;
            if (desc) sql += " desc ";
            sql += " limit ?, ? ";
            PreparedStatement statement = con.prepareStatement(sql);
            PreparedStatement statement1 = con.prepareStatement(totSql);
            int x = 1;
            if (orderId != null || commodityId != null) {
                if (orderId != null) {
                    statement.setInt(x, orderId);
                    statement1.setInt(x, orderId);
                    x++;
                }
                if (commodityId != null) {
                    statement.setInt(x, commodityId);
                    statement1.setInt(x, commodityId);
                    x++;
                }
            }
            ResultSet resultSet = statement1.executeQuery();
            resultSet.next();
            int allNum = resultSet.getInt(1);
            int pageNum = allNum / pageSize;
            if (allNum % pageSize != 0) pageNum++;

            HashMap<String, Object> res = new HashMap<>();
            List<Review> list = new ArrayList<>();

            statement.setInt(x, (page - 1) * pageSize);
            statement.setInt(x + 1, pageSize);
            ResultSet rs = statement.executeQuery();
            int num = 0;
            res.put("page", page);
            res.put("page_num", pageNum);
            while (rs.next()) {
                num++;
                Integer id = rs.getInt("id");
                Integer commodityId1 = rs.getInt("commodity_id");
                Integer userId = rs.getInt("user_id");
                Integer sellerId = rs.getInt("seller_id");
                Integer star = rs.getInt("star");
                Integer orderId1 = rs.getInt("order_id");
                String content = rs.getString("content");
                String updateTime = rs.getString("update_time");

                Review review = new Review(orderId1, star, content);
                review.setCommodityId(commodityId1);
                review.setId(id);
                review.setUserId(userId);
                review.setSellerId(sellerId);
                review.setUpdateTime(updateTime);
                list.add(review);
            }
            res.put("reviews", list);
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
