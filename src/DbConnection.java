import java.sql.*;

public class DbConnection {

    private String dbUrl="jdbc:mysql://localhost:3306/customer_processor";
    private String user="root";
    private String password="";
    private Connection connection;
    public Connection createConnection(){

        try{
            connection = DriverManager.getConnection(dbUrl,user,password);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return connection;
    }
}
