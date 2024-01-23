package scholarship_krish;

import java.security.PublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Krish
 */
public class UpdateStudent
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
    
    public UpdateStudent()
    {
        database_helper = new DatabaseHelper();
        poll_db = Constants.DATABASE_TEMP;
        poll_username = Constants.DATABASE_MAIN_USERNAME;
        poll_password = Constants.DATABASE_MAIN_PASSWORD;
        main_db = Constants.DATABASE_MAIN;
        main_username = Constants.DATABASE_MAIN_USERNAME;
        main_password = Constants.DATABASE_MAIN_PASSWORD;
    }
    
    public void readStudentData()
    {
        try
        {
            connection = (Connection) database_helper.openDatabaseConnection(poll_db, poll_username, poll_password);
            String query = "SELECT COUNT(student_email) FROM STUDENTS;";
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
                query = "SELECT * FROM STUDENTS;";
                prepared = connection.prepareStatement(query);
                result_set = prepared.executeQuery();
                result_set.next();
                String stu_email = result_set.getString(1);
                PublicKey pub_key = Convertor.stringToPublicKey(result_set.getString(2));
                double need = result_set.getDouble(3);
                String stu_fname = result_set.getString(5);
                String stu_lname = result_set.getString(6);
                String course = result_set.getString(7);
                double dur = result_set.getDouble(8);
                query = "DELETE FROM STUDENTS WHERE student_email = ?";
                prepared = connection.prepareStatement(query);
                prepared.setString(1, stu_email);
                prepared.executeUpdate();
                connection.commit();
                database_helper.closeDatabaseConnection(connection);
                addStudent(stu_email, pub_key, need, stu_fname, stu_lname, course, dur);
            }
        }
        catch(Exception ex)
        {
            database_helper.closeDatabaseConnection(connection);
            System.out.println("\nUpdateStudent -> run() -> Student update failed");
            ex.printStackTrace();
        }
    }
    
    public void addStudent(String stu_email, PublicKey stu_pub_key, double need, String stu_fname, String stu_lname, String course, double dur)
    {
        String stu_key = Convertor.publicKeyToString(stu_pub_key);
        try
        {
            connection = (Connection) database_helper.openDatabaseConnection(main_db, main_username, main_password);
            String query = "INSERT INTO STUDENTS(student_email, student_pub_key, student_program_cost, student_collected, student_fname, " +
                    "student_lname, student_course, student_course_dur) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
            prepared = connection.prepareStatement(query);
            prepared.setString(1, stu_email);
            prepared.setString(2, stu_key);
            prepared.setDouble(3, need);
            prepared.setDouble(4, 0);
            prepared.setString(5, stu_fname);
            prepared.setString(6, stu_lname);
            prepared.setString(7, course);
            prepared.setDouble(8, dur);
            prepared.executeUpdate();
            connection.commit();
            database_helper.closeDatabaseConnection(connection);
            if(control)
            {
                System.out.println("\nUpdateStudent -> addStudent() -> New student added " + stu_email);
            }
        }
        catch(Exception ex)
        {
            database_helper.closeDatabaseConnection(connection);
            System.out.println("\nUpdateStudent -> addStudent() -> Could not add student to database");
            ex.printStackTrace();
        }
    }
}