package scholarship_krish;

/**
 *
 * @author Krish
 */
public class Constants
{
    public static int DIFFICULTY = 5;
    public static int KEY_PAIR_SIZE = 256;    
    public static String GENESIS_PREV_BLOCK = "0000000000000000000000000000000000000000000000000000000000000000";
    public static String DATABASE_URL = "jdbc:mysql://localhost:3306/";
    public static String DATABASE_MAIN = "SCHOLARSHIP_BODY_RECORDS";
    public static String DATABASE_TEMP = "TESTDB";
    public static String DATABASE_MAIN_USERNAME = "root";
    public static String DATABASE_MAIN_PASSWORD = "";
    public static final boolean CONTROL = true;
    
    private Constants()
    {
        
    }

    public static void printConstants()
    {
        System.out.println("DIFFICULTY             = " + DIFFICULTY);
        System.out.println("KEY_PAIR_SIZE          = " + KEY_PAIR_SIZE);
        System.out.println("GENESIS_PREV_BLOCK     = " + GENESIS_PREV_BLOCK);
        System.out.println("DATABASE_URL           = " + DATABASE_URL);
        System.out.println("DATABASE_MAIN_USERNAME = " + DATABASE_MAIN_USERNAME);
        System.out.println("DATABASE_MAIN_PASSWORD = " + DATABASE_MAIN_PASSWORD);
        System.out.println("CONTROL                = " + CONTROL);
    }
}
