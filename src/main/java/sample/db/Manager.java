package sample.db;




import java.sql.*;


public class Manager {

    SqliteConnection sqliteConnectionProvaider ;
    public  Manager(){
        sqliteConnectionProvaider  = SqliteConnection.getInstance();
    }


    public String getToken(){
        String result = "";
        Connection connection = SqliteConnection.connection;
        String query = "select * from pad";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
            result = resultSet.getString("token");}
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
        return result;
    }


    public String getName(){
        String result = "";
        Connection connection = SqliteConnection.connection;
        String query = "select * from pad";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                result = resultSet.getString("name");}
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
        return result;
    }
    public void setToken(String newToken){
        Connection connection = SqliteConnection.connection;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement
                    ("Insert into pad(token) Values(?)");
            statement.setString(1, newToken);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDirectionPath(String path){
        Connection connection = SqliteConnection.connection;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement
                    ("Insert into pad(direction) Values(?)");
            statement.setString(1, path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getDirectionPath(){
        String result = "";
        Connection connection = SqliteConnection.connection;
        String query = "select * from pad";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                result = resultSet.getString("direction");}
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
        return result;

    }

    public void setTokenAndName(String newToken,String newName){
        Connection connection = SqliteConnection.connection;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement
                    ("Insert into pad(token,name) Values(?,?)");
            statement.setString(1, newToken);
            statement.setString(2, newName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteToken(){
        Connection connection = SqliteConnection.connection;
        String query = "DELETE from pad";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        }catch (Exception e){
            System.out.println(e);
        }
    }


}
