package scholarship_krish;

import java.security.PublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Krish
 */
public class UpdateInvestor
{
    private String poll_db;
    private String poll_username;
    private String poll_password;
    private String main_db;
    private String main_username;
    private String main_password;
    private DatabaseHelper database_helper;
    private Connection connection = null;
    private PreparedStatement prepared = null;
    private boolean control = true;
    
    public UpdateInvestor()
    {
        database_helper = new DatabaseHelper();
        poll_db = Constants.DATABASE_TEMP;
        poll_username = Constants.DATABASE_MAIN_USERNAME;
        poll_password = Constants.DATABASE_MAIN_PASSWORD;
        main_db = Constants.DATABASE_MAIN;
        main_username = Constants.DATABASE_MAIN_USERNAME;
        main_password = Constants.DATABASE_MAIN_PASSWORD;
    }
    
    public void readInvestorData()
    {
        try
        {
            connection = (Connection) database_helper.openDatabaseConnection(poll_db, poll_username, poll_password);
            String query = "SELECT COUNT(investor_email) FROM INVESTORS;";
            prepared = connection.prepareStatement(query);
            ResultSet result_set = prepared.executeQuery();
            result_set.next();
            int count = result_set.getInt(1);
            if(count == 0)
            {
                database_helper.closeDatabaseConnection(connection);
            }
            if(count >= 1)
            {
                query = "SELECT * FROM INVESTORS;";
                prepared = connection.prepareStatement(query);
                result_set = prepared.executeQuery();
                result_set.next();
                String inv_email = result_set.getString(1);
                PublicKey pub_key = Convertor.stringToPublicKey(result_set.getString(2));
                String inv_fname = result_set.getString(3);
                String inv_lname = result_set.getString(4);
                query = "DELETE FROM INVESTORS WHERE investor_email = ?";
                prepared = connection.prepareStatement(query);
                prepared.setString(1, inv_email);
                prepared.executeUpdate();
                connection.commit();
                database_helper.closeDatabaseConnection(connection);
                addInvestor(inv_email, pub_key, inv_fname, inv_lname);
            }
        }
        catch(Exception ex)
        {
            database_helper.closeDatabaseConnection(connection);
            System.out.println("\nUpdateInvestor -> run() -> Update failed");
            ex.printStackTrace();
        }
    }
    
    public void addInvestor(String inv_email, PublicKey inv_pub_key, String inv_fname, String inv_lname)
    {
        String inv_key = Convertor.publicKeyToString(inv_pub_key);
        try
        {
            connection = (Connection) database_helper.openDatabaseConnection(main_db, main_username, main_password);
            String query = "INSERT INTO INVESTORS(investor_email, investor_pub_key, investor_fname, investor_lname) " +
                    "VALUES(?, ?, ?, ?)";
            prepared = connection.prepareStatement(query);
            prepared.setString(1, inv_email);
            prepared.setString(2, inv_key);
            prepared.setString(3, inv_fname);
            prepared.setString(4, inv_lname);
            prepared.executeUpdate();
            connection.commit();
            database_helper.closeDatabaseConnection(connection);
            if(control)
            {
                System.out.println("\nUpdateInvestor -> addInvestor() -> New investor added " + inv_email);
            }
        }
        catch(Exception ex)
        {
            database_helper.closeDatabaseConnection(connection);
            System.out.println("\nUpdateInvestor -> addInvestor() -> Could not add investor to database");
            ex.printStackTrace();
        }
    }
}