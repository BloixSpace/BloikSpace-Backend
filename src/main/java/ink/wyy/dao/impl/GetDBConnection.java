package ink.wyy.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GetDBConnection {
    public static Connection connectDB(String url, String DBName, String user, String pw) {
        Connection con = null;
        String uri =
                "jdbc:mysql://" + url + "/" +
                        DBName + "?useSSL=false&characterEncoding=utf-8";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (Exception e) {
            System.out.println(e);
        }
        try {
            con = DriverManager.getConnection(uri, user, pw);
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return con;
    }
}
