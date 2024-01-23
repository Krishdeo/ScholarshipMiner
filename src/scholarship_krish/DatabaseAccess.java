package scholarship_krish;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Krish
 */
public class DatabaseAccess extends DatabaseHelper
{
    private String main_db;
    private String main_username;
    private String main_password;
    private DatabaseHelper database_helper;
    public static Connection connection;
    private Statement statement;
    private PreparedStatement prepared;
    private int DBKPLEN = 124;
    private int NAMELEN = 48;
    private int HASHLEN = 64;
    private boolean control = true;
    
    public DatabaseAccess()
    {
        main_db = Constants.DATABASE_MAIN;
        main_username = Constants.DATABASE_MAIN_USERNAME;
        main_password = Constants.DATABASE_MAIN_PASSWORD;
        database_helper = new DatabaseHelper();
        connection =  (Connection) database_helper.openDatabaseConnection(main_db, main_username, main_password);
        try
        {
            statement = connection.createStatement();
            String table = "CREATE TABLE IF NOT EXISTS STUDENTS(" +
                    "student_email VARCHAR(" + NAMELEN + "), " +
                    "student_pub_key VARCHAR(" + DBKPLEN + "), " +
                    "student_program_cost DOUBLE PRECISION(10,2), " +
                    "student_collected DOUBLE PRECISION(10,2), " +
                    "student_fname VARCHAR(" + NAMELEN + "), " +
                    "student_lname VARCHAR(" + NAMELEN + "), " +
                    "student_course VARCHAR(64), " +
                    "student_course_dur DOUBLE PRECISION(4,2), " +
                    "PRIMARY KEY(student_email)" + 
                    ")ENGINE=INNODB;";
            prepared = connection.prepareStatement(table);
            prepared.executeUpdate();
            table = "CREATE TABLE IF NOT EXISTS INVESTORS(" +
                    "investor_email VARCHAR(" + NAMELEN + "), " +
                    "investor_pub_key VARCHAR(" + DBKPLEN + "), " +
                    "investor_fname VARCHAR(" + NAMELEN + "), " +
                    "investor_lname VARCHAR(" + NAMELEN + "), " +
                    "PRIMARY KEY(investor_email)" +
                    ")ENGINE=INNODB;";
            prepared = connection.prepareStatement(table);
            prepared.executeUpdate();
            table = "CREATE TABLE IF NOT EXISTS FUNDS(" +
                    "student_email VARCHAR(" + NAMELEN + "), " +
                    "investor_email VARCHAR(" + NAMELEN + "), " +
                    "amount_given DOUBLE PRECISION(10,2), " +
                    "PRIMARY KEY(student_email, investor_email)" +
                    ")ENGINE=INNODB;";
            prepared = connection.prepareStatement(table);
            prepared.executeUpdate();
            table = "CREATE TABLE IF NOT EXISTS BROKER(" +
                    "broker_email VARCHAR(" + NAMELEN + "), " +
                    "broker_pub_key VARCHAR(" + DBKPLEN + "), " +
                    "broker_name VARCHAR(" + NAMELEN + "), " +
                    "broker_business_id VARCHAR(" + NAMELEN + "), " +
                    "broker_phone1 VARCHAR(16), " +
                    "broker_phone2 VARCHAR(16), " +
                    "broker_address VARCHAR(" + NAMELEN + "), " +
                    "PRIMARY KEY(broker_email) " +
                    ")ENGINE=INNODB;";
            prepared = connection.prepareStatement(table);
            prepared.executeUpdate();
            updateBlockchain();
            connection.commit();
            database_helper.closeDatabaseConnection(connection);
        }
        catch(SQLException ex)
        {
            database_helper.closeDatabaseConnection(connection);
            System.out.println("\nDatabaseAccess -> DatabaseAccess() -> Could not create database tables");
            ex.printStackTrace();
        }
    }
    
    public void fundStudent(String stu_email, String inv_email, double fund)
    {
        try
        {
            connection = (Connection) database_helper.openDatabaseConnection(main_db, main_username, main_password);
            statement = connection.createStatement();
            String query = "SELECT student_collected FROM STUDENTS WHERE student_email = '" + stu_email + "';";
            ResultSet result_set = statement.executeQuery(query);
            result_set.next();
            double amount = result_set.getDouble(1);
            amount = amount + fund;
            query = "UPDATE STUDENTS SET student_collected = '" + amount + "' " +
               "WHERE student_email = '" + stu_email + "';";
            statement.executeUpdate(query);
            query = "SELECT COUNT(student_email) FROM FUNDS WHERE student_email = '" + stu_email  +
                "' AND investor_email = '" + inv_email + "';";
            result_set = statement.executeQuery(query);
            result_set.next();
            int record_found = result_set.getInt(1);
            if(record_found == 0)  //check if a student with the same investor exists, create investor entry if no
            {
                query = "INSERT INTO FUNDS" +
                "(student_email, investor_email, amount_given) " +
                "VALUES('" + stu_email + "', '" + inv_email + "', '" + fund + "');";
                statement.executeUpdate(query);
            }
            if(record_found == 1)   //if yes, update the amount the investor gave previously
            {
                query = "SELECT amount_given FROM FUNDS WHERE student_email = '" + stu_email +
                    "' AND investor_email = '" + inv_email + "';";
                result_set = statement.executeQuery(query);
                result_set.next();
                amount = result_set.getDouble(1);
                amount = amount + fund;
                query = "UPDATE FUNDS SET amount_given = '" + amount + "' WHERE student_email " +
                    "= '" + stu_email + "' AND investor_email = '" + inv_email + "';";
                statement.executeUpdate(query);
            }
            connection.commit();
            database_helper.closeDatabaseConnection(connection);
            if(control)
            {
                System.out.println("\nDatabaseAccess -> fundStudent() -> New fund given");
            }
        }
        catch(SQLException ex)
        {
            try
            {
                connection.rollback();
                database_helper.closeDatabaseConnection(connection);
                System.out.println("\nDatabaseAccess -> fundStudent() -> Could not add student fund request");
                ex.printStackTrace();
            }
            catch(SQLException ex1)
            {
                System.out.println("\nDatabaseAccess -> fundStudent() -> Rollback failed");
            }
        }
    }
    
    public void updateBlockchain()
    {
        try
        {
            String query = "DROP TABLE IF EXISTS BLOCKCHAIN;";
            prepared = connection.prepareStatement(query);
            prepared.executeUpdate();
            query = "CREATE TABLE IF NOT EXISTS BLOCKCHAIN(" +
                "block_id CHAR(32), " +
                "block_data LONGTEXT, " +
                "block_hash CHAR(" + HASHLEN + "), " +
                "PRIMARY KEY(block_id)" +
                ")ENGINE=INNODB;";
            prepared = connection.prepareStatement(query);
            prepared.executeUpdate();
            String file = "blockchain.txt";
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String current_line;
            query = "INSERT INTO BLOCKCHAIN(block_id, block_data, block_hash) VALUES(?, ?, ?)";
            prepared = connection.prepareStatement(query);
            while((current_line = reader.readLine()) != null)
            {
                String str_block[] = current_line.split("###");
                prepared.setString(1, str_block[0]);
                prepared.setString(2, str_block[1]);
                prepared.setString(3, str_block[2]);
                prepared.executeUpdate();
                Block block = (Block) Convertor.stringToObject(str_block[1]);
                Driver.block_chain.add(block);
            }
            if(control)
            {
                System.out.println("\nDatabaseAccess -> updateBlockchain() -> Blockchain updated");
            }
        }
        catch(IOException | SQLException ex)
        {
            System.out.println("\nDatabaseAccess -> updateBlockchain() -> Could not update blockchain");
            ex.printStackTrace();
        }
    }
}
