package scholarship_krish;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

/**
 *
 * @author Krish
 */
public class Miner
{
    private String main_db;
    private String main_username;
    private String main_password;
    private final DatabaseHelper database_helper;
    private Connection connection;
    private PreparedStatement prepared;
    private DatabaseAccess database_access;
    private boolean control = true;

    public Miner() throws SQLException
    {
        main_db = Constants.DATABASE_MAIN;
        main_username = Constants.DATABASE_MAIN_USERNAME;
        main_password = Constants.DATABASE_MAIN_PASSWORD;
        database_helper = new DatabaseHelper();
        connection = DatabaseAccess.connection;
    }
    
    public void mineStudent()
    {   //select the need and collected to check
        try
        {
            String query = "SELECT student_email, student_program_cost, student_collected FROM STUDENTS;";
            connection = (Connection) database_helper.openDatabaseConnection(main_db, main_username, main_password);
            prepared = connection.prepareStatement(query);
            ResultSet stu_result_set = prepared.executeQuery();
            while(stu_result_set.next())
            {
                String stu_email = stu_result_set.getString(1);
                double need = stu_result_set.getDouble(2);
                double collected = stu_result_set.getDouble(3);
                if(collected >= need)///make the con ==
                {
                    //notifyStudentIsMined(stu_pub_key);
                    Student student = getStudentDetails(stu_email);
                    List<Investor> investors = getInvestorsDetail(stu_email);
                    query = "DELETE FROM STUDENTS WHERE student_email = ?";//delete student
                    prepared = connection.prepareStatement(query);
                    prepared.setString(1, stu_email);
                    prepared.executeUpdate();
                    query = "DELETE FROM FUNDS WHERE student_email = ?";//delete investors from funds
                    prepared = connection.prepareStatement(query);
                    prepared.setString(1, stu_email);
                    prepared.executeUpdate();
                    Broker broker = getBrokerDetails();
                    String id = getBlockId();
                    query = "SELECT block_hash FROM BLOCKCHAIN;";
                    prepared = connection.prepareStatement(query);
                    ResultSet block_result_set = prepared.executeQuery();
                    block_result_set.last();
                    String prev_hash = block_result_set.getString(1);
                    Block block = new Block(id, prev_hash, student, investors, broker);
                    block.signBlock(broker.getWallet().getPrivateKey());
                    Driver.block_chain.add(block);
                    String str_data = Convertor.objectToString(block);
                    String hash = block.getHash();
                    query = "INSERT INTO BLOCKCHAIN(block_id, block_data, block_hash) VALUES(?, ?, ?);";
                    prepared = connection.prepareStatement(query);
                    prepared.setString(1, id);
                    prepared.setString(2, str_data);
                    prepared.setString(3, hash);
                    prepared.executeUpdate();
                    updateBlockChainFile(id, str_data, hash);
                    connection.commit();
                    if(control)
                    {
                        System.out.println("\nMiner - > mineStudent() -> New contract added to blockchain");
                    }
                    break;
                }
            }
            database_helper.closeDatabaseConnection(connection);
        }
        catch(NoSuchAlgorithmException | SQLException | NoSuchProviderException | InvalidKeySpecException | IOException ex)
        {
            try
            {
                connection.rollback();
                database_helper.closeDatabaseConnection(connection);
                System.out.println("\nMiner - > mineStudent() -> Mining failed");
                ex.printStackTrace();
            }
            catch(SQLException ex1)
            {
                System.out.println("\nMiner - > mineStudent() -> Rollback failed");
                ex1.printStackTrace();
            }
        }
    }
    
    public String getBlockId()
    {
        int min = 10000000;
        int max = 99999999;
        LocalDateTime time_stamp = LocalDateTime.now();
        int rand = (int)(Math.random() * ((max - min) + 1)) + min;
        String id = time_stamp.toString() + ":" + Integer.toString(rand);
        return id;
    }
    
    private Broker getBrokerDetails() throws FileNotFoundException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException
    {
        String file = "keyfile.txt";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String current_line;
        int index = 0;
        String [] keys = new String[2];
        while((current_line = reader.readLine()) != null)
        {
            keys[index] = current_line;
            index++;
        }
        PublicKey pub_key = Convertor.stringToPublicKey(keys[0]);
        PrivateKey pri_key;// = database_access.stringToPrivateKey(keys[1]);
        pri_key = CryptographyHelper.ellipticCurveCrypto().getPrivate();///////remove this
        Broker broker = new Broker();
        broker.getWallet().setPublicKey(pub_key);
        broker.getWallet().setPrivateKey(pri_key);
        broker.setName("TSLB");
        broker.setBusinessId("B12345");
        broker.setEmail("broker email");
        broker.setPhone1("+679121212");
        broker.setPhone2("+679124578");
        broker.setAddress("suva, fiji");
        return broker;
    }
    
    private Student getStudentDetails(String stu_email)
    {
        Student student = new Student();
        try
        {
            String query = "SELECT * FROM STUDENTS WHERE student_email = ?";
            prepared = connection.prepareStatement(query);
            prepared.setString(1, stu_email);
            ResultSet result_set = prepared.executeQuery();
            result_set.next();
            student.setStuEmail(stu_email);
            student.getWallet().setPublicKey(Convertor.stringToPublicKey(result_set.getString(2)));
            student.getWallet().setPrivateKey(null);
            student.setAmount(result_set.getDouble(3));
            student.setStuFname(result_set.getString(5));
            student.setStuLname(result_set.getString(6));
            student.setCourse(result_set.getString(7));
            student.setCourseDuration(result_set.getDouble(8));
        }
        catch(SQLException ex)
        {
            System.out.println("\nMiner - > getStudentDetails() -> Could not get student details from database");
            ex.printStackTrace();
        }
        return student;
    }
    
    private List<Investor> getInvestorsDetail(String stu_email)
    {
        List<Investor> investors = new ArrayList<>();
        try
        {
            String query = "SELECT * FROM FUNDS WHERE student_email = ?";
            prepared = connection.prepareStatement(query);
            prepared.setString(1, stu_email);
            ResultSet result_set_funds = prepared.executeQuery();
            while(result_set_funds.next())
            {
                Investor inv = new Investor();
                inv.setInvEmail(result_set_funds.getString(2));
                inv.setAmount(result_set_funds.getDouble(3));
                query = "SELECT * FROM INVESTORS WHERE investor_email = ?";
                prepared = connection.prepareStatement(query);
                prepared.setString(1, result_set_funds.getString(2));
                ResultSet result_set_inv = prepared.executeQuery();
                result_set_inv.next();
                inv.getWallet().setPublicKey(Convertor.stringToPublicKey(result_set_inv.getString(2)));
                inv.getWallet().setPrivateKey(null);
                inv.setInvFname(result_set_inv.getString(3));
                inv.setInvLname(result_set_inv.getString(4));
                investors.add(inv);
            }
        }
        catch(SQLException ex)
        {
            System.out.println("\nMiner - > getInvestorsDetail() -> Could not get investors details from database");
            ex.printStackTrace();
        }
        return investors;
    }
    
    private void updateBlockChainFile(String id, String str_data, String hash)
    {
        try
        {
            String file = "blockchain.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            String data = id + "###" + str_data + "###" + hash;
            writer.append(data);
            writer.append("\n");
            writer.close();
        }
        catch(IOException ex)
        {
            System.out.println("\nMiner - > updateBlockChainFile() -> Could not write to blockchain file");
            ex.printStackTrace();
        }
    }
    
    private void notifyStudentIsMined(String stu_pub_key)
    {
        ;
    }
}
