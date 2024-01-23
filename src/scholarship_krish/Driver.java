package scholarship_krish;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import static scholarship_krish.Driver.database_access;
import static scholarship_krish.Driver.main_db;
import static scholarship_krish.Driver.main_password;
import static scholarship_krish.Driver.main_username;

public class Driver
{
    public static ArrayList<Block> block_chain = new ArrayList<Block>();
    public static DatabaseAccess database_access;
    public static String main_db = Constants.DATABASE_MAIN;
    public static String main_username = Constants.DATABASE_MAIN_USERNAME;
    public static String main_password = Constants.DATABASE_MAIN_PASSWORD;
    public static String temp_db = Constants.DATABASE_TEMP;
    public static String temp_username = Constants.DATABASE_MAIN_USERNAME;
    public static String temp_password = Constants.DATABASE_MAIN_PASSWORD;
    
    private static int stu_num = 20;
    private static int inv_num = 8;
    
    private static String[] stu_email = new String[stu_num];
    private static double[] stu_amount = new double[stu_num];
    private static String[] stu_fname = new String[stu_num];
    private static String[] stu_lname = new String[stu_num];
    private static String[] course = new String[stu_num];
    private static double[] course_duration = new double[stu_num];
    private static PublicKey [] stu_pub_key = new PublicKey[stu_num];
    
    private static double[] inv_amount = new double[inv_num];
    private static String[] inv_email = new String[inv_num];
    private static String[] inv_fname = new String[inv_num];
    private static String[] inv_lname = new String[inv_num];
    private static PublicKey [] inv_pub_key = new PublicKey[inv_num];
    
    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, SQLException, IOException, InterruptedException, ClassNotFoundException
    {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        database_access = new DatabaseAccess();
        
        /*readData();
        UpdateStudent us = new UpdateStudent();
        UpdateInvestor ui = new UpdateInvestor();
        for(int i = 0; i < stu_num; i++)
        {
            us.addStudent(stu_email[i], stu_pub_key[i], stu_amount[i], stu_fname[i], stu_lname[i], course[i], course_duration[i]);
        }
        
        for(int i = 0; i < inv_num; i++)
        {
            ui.addInvestor(inv_email[i], inv_pub_key[i], inv_fname[i], stu_lname[i]);
        }*/
        
        /*for(int i = 0; i < 30; i++)
        {
            int sr = (int)(Math.random()*stu_num);
            int ir = (int)(Math.random()*inv_num);
            double fr = Math.random()*20000 + 10000;
            double dur = 4;
            database_access.fundStudent(stu_email[sr], inv_email[ir], fr);
        }
        System.out.println("data updated");*/
        Thread stu_update = new Thread(new GetStudentUpdate());
        Thread inv_update = new Thread(new GetInvestorUpdate());
        Thread miner = new Thread(new MineStudents());
        Thread profile = new Thread(new DisplayProfile());
        Thread fund = new Thread(new Fund());
        stu_update.start();
        inv_update.start();
        miner.start();
        //profile.start();
        //fund.start();
    }
    
    private static void readData() throws FileNotFoundException, IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException
    {
        //read student keys
        String file = "stu_keys.txt";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String current_line;
        int index = 0;
        while((current_line = reader.readLine()) != null)
        {
            byte[] byte_key = Base64.getDecoder().decode(current_line);
            stu_pub_key[index] = Convertor.byteToPublicKey(byte_key);
            index++;
        }
        //read student details
        file = "stu_data.txt";
        reader = new BufferedReader(new FileReader(file));
        index = 0;
        while((current_line = reader.readLine()) != null)
        {
            String data[] = current_line.split("#");
            stu_email[index] = data[0];
            stu_amount[index] = Math.random()*50000 + 30000;
            stu_fname[index] = data[2];
            stu_lname[index] = data[3];
            course[index] = data[4];
            course_duration[index] = Double.parseDouble(data[5]);
            index++;
        }
        //read investor keys
        file = "inv_keys.txt";
        reader = new BufferedReader(new FileReader(file));
        index = 0;
        while((current_line = reader.readLine()) != null)
        {
            byte[] byte_key = Base64.getDecoder().decode(current_line);
            inv_pub_key[index] = Convertor.byteToPublicKey(byte_key);
            index++;
        }
        //read investor details
        file = "inv_data.txt";
        reader = new BufferedReader(new FileReader(file));
        index = 0;
        while((current_line = reader.readLine()) != null)
        {
            String data[] = current_line.split("#");
            inv_email[index] = data[0];
            inv_amount[index] = Math.random()*20000 + 1000;
            inv_fname[index] = data[2];
            inv_lname[index] = data[3];
            index++;
        }
    }
    
    private void generateKeys() throws IOException
    {
        String fileName = "stu_key_file.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        for(int i = 0; i < stu_num; i++)
        {
            stu_pub_key[i] = CryptographyHelper.ellipticCurveCrypto().getPublic();
            byte[] byte_key = Convertor.publicKeyToByte(stu_pub_key[i]);
            String str_key = Base64.getEncoder().encodeToString(byte_key);
            writer.append(str_key);
            writer.append("\n");
        }
        writer.close();
        fileName = "inv_key_file.txt";
        writer = new BufferedWriter(new FileWriter(fileName, true));
        for(int i = 0; i < inv_num; i++)
        {
            inv_pub_key[i] = CryptographyHelper.ellipticCurveCrypto().getPublic();
            byte[] byte_key = Convertor.publicKeyToByte(inv_pub_key[i]);
            String str_key = Base64.getEncoder().encodeToString(byte_key);
            writer.append(str_key);
            writer.append("\n");
        }
        writer.close();
    }
}

class MineStudents implements Runnable
{
    public void run()
    {
        while(true)
        {
            try
            {
                Thread.sleep(1000);
                Miner miner = new Miner();
                miner.mineStudent();
            }
            catch(InterruptedException | SQLException ex)
            {
                System.out.println("\nDriver -> MineStudents -> run() -> Mining failed");
                ex.printStackTrace();
            }
        }
    }
}

class GetStudentUpdate implements Runnable
{
    public void run()
    {
        while(true)
        {
            try
            {
                Thread.sleep(1000);
                UpdateStudent update_student = new UpdateStudent();
                update_student.readStudentData();
            }
            catch(InterruptedException ex)
            {
                System.out.println("\nDriver -> GetStudentUpdate -> run() -> Failed to get student update");
                ex.printStackTrace();
            }
        }
    }
}

class GetInvestorUpdate implements Runnable
{
    public void run()
    {
        while(true)
        {
            try
            {
                Thread.sleep(1000);
                UpdateInvestor update_investor = new UpdateInvestor();
                update_investor.readInvestorData();
            }
            catch(InterruptedException ex)
            {
                System.out.println("\nDriver -> GetInvestorUpdate -> run() -> Failed to get investor update");
                ex.printStackTrace();
            }
        }
    }
}

class DisplayProfile implements Runnable
{
    DatabaseHelper database_helper = new DatabaseHelper();
    Connection connection = null;
    
    public void run()
    {
        int last_count = -1;
        while(true)
        {
            try
            {
                Thread.sleep(1000);
                connection = (Connection) database_helper.openDatabaseConnection(main_db, main_username, main_password);
                Statement stmt = connection.createStatement();
                String query = "SELECT COUNT(student_email) FROM STUDENTS";
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                int count = rs.getInt(1);
                if(last_count != count)
                {
                    if(count == 0)
                    {
                        System.out.println("\nNo students have registered yet");
                    }
                    else
                    {
                        query = "SELECT * FROM STUDENTS";
                        rs = stmt.executeQuery(query);
                        String text = "Email\t\t\tFirst Name\tProgram Cost\tNeed";
                        while (rs.next())
                        {
                            text = text + "\n";
                            String email = rs.getString(1);
                            String fname = rs.getString(5);
                            String lname = rs.getString(6);
                            String prog = rs.getString(7);
                            double cost = rs.getDouble(3);
                            double collected = rs.getDouble(4);
                            double need = cost - collected;
                            double dur = rs.getDouble(8);
                            text = text + email + "\t" + fname + "\t" + cost + "\t" + need;
                        }
                        System.out.println(text);
                    }
                }
                last_count = count;
                connection.commit();
                database_helper.closeDatabaseConnection(connection);
            }
            catch(InterruptedException | SQLException ex)
            {
                database_helper.closeDatabaseConnection(connection);
                System.out.println("\nDriver -> DisplayProfile -> run() -> Could not get student profile");
                ex.printStackTrace();
            }
        }
    }
}

class Fund implements Runnable
{
    public void run()
    {
        while(true)
        {
            try
            {
                Thread.sleep(0);
                Scanner scan = new Scanner(System.in);
                String line = scan.nextLine();
                String details[] = line.split("\\s");
                String stu = details[0];
                String inv = details[1];
                double amnt = Double.parseDouble(details[2]);
                Driver.database_access.fundStudent(stu, inv, amnt);
            }
            catch(InterruptedException ex)
            {
                System.out.println("\nDriver -> Fund -> run() -> Could not receive fund");
                ex.printStackTrace();
            }
        }
    }
}