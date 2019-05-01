package sample.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteConnection {

    private static SqliteConnection instanse = new SqliteConnection();
    public static Connection connection;


    private SqliteConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection1 = DriverManager.getConnection("jdbc:sqlite:image_manager.sqlite");
            connection =connection1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SqliteConnection getInstance(){
        return instanse;
    }

    public static  boolean isDbConected(){
        try {
            if( connection!=null)
            return !connection.isClosed();
            else return false;
        } catch (SQLException e) {
           return false;
        }
    }

}
