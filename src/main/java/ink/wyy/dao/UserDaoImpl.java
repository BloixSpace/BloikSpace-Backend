package ink.wyy.dao;

import ink.wyy.bean.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImpl implements UserDao{

    private final Connection con;

    public UserDaoImpl(String url, String DBName, String user, String pwd) {
        this.con = GetDBConnection.connectDB(url, DBName, user, pwd);
        try {
            con.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public String insert(User user) {
        try {
            String sql = "insert into users (username, password, create_date, signature, avatarUri, level)" +
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
        }
    }

    @Override
    public String delete(int id) {
        try {
            String sql = "delete from users where id=?";
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
        }
    }

    @Override
    public String update(Integer id, User user) {
        String sql = "update users set username=?, password=?, signature=?, avatarUri=?, level=? where id=?";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getSignature());
            statement.setString(4, user.getAvatarUri());
            statement.setInt(5, user.getLevel());
            statement.setInt(6, user.getId());
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
        }
    }

    @Override
    public String updateUserInfo(Integer id, User user) {
        String sql = "update users set signature=?, avatarUri=? where id=?";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, user.getSignature());
            statement.setString(2, user.getAvatarUri());
            statement.setInt(3, id);
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
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "select * from users where username=?";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            User user = null;
            while (rs.next()) {
                user = new User(rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("id"));
                user.setAvatarUri(rs.getString("avatarUri"));
                user.setCreateDate(rs.getDate("create_date"));
                user.setSignature(rs.getString("signature"));
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
        }
    }

    @Override
    public User findById(int id) {
        String sql = "select * from users where id=?";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            User user = null;
            while (rs.next()) {
                user = new User(rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("id"));
                user.setAvatarUri(rs.getString("avatarUri"));
                user.setCreateDate(rs.getDate("create_date"));
                user.setSignature(rs.getString("signature"));
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
        }
    }

    @Override
    public void destroy() {
        try {
            con.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
