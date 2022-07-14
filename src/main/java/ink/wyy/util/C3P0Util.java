package ink.wyy.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class C3P0Util {

    private static final ComboPooledDataSource DATA_SOURCE = new ComboPooledDataSource("mysql");

    public static ComboPooledDataSource getDataSource() {
        return DATA_SOURCE;
    }

    public static Connection getConnection() {
        Connection conn;
        try {
            conn = DATA_SOURCE.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

    public static void close(AutoCloseable... autoCloseables) {
        try {
            for (AutoCloseable a : autoCloseables) {
                if (a != null) {
                    a.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
