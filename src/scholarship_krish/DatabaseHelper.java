package scholarship_krish;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Krish
 */
public class DatabaseHelper
{
    private final String url;
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement prepared = null;
 
    public DatabaseHelper()
    {
        url = Constants.DATABASE_URL;
    }
    
    public Connection openDatabaseConnection(String database, String username, String password)
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url_temp = url + database;
            connection = DriverManager.getConnection(url_temp, username, password);
            connection.setAutoCommit(false);
        }
        catch(ClassNotFoundException | SQLException ex)
        {
            closeDatabaseConnection(connection);
            System.out.println("\nDatabaseHelper -> openDatabaseConnection() -> Connection to database failed");
            ex.printStackTrace();
        }
        return connection;
    }
    
    public void closeDatabaseConnection(Connection connection)
    {
        try
        {
            if(!(connection.isClosed()))
            {
                connection.close();
            }
        }
        catch(SQLException ex)
        {
            System.out.println("\nDatabaseHelper -> closeDatabaseConnection() -> Could not close database connection");
            ex.printStackTrace();
        }
    }
}
