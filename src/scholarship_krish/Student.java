package scholarship_krish;

import java.io.Serializable;

/**
 *
 * @author Krish
 */
public class Student implements Serializable
{
    private String stu_email;
    private double amount;
    private String stu_fname;
    private String stu_lname;
    private String course;
    private double course_duration;
    private Wallet wallet;
    
    public Student()
    {
        wallet = new Wallet();
    }

    public String getStuEmail()
    {
        return stu_email;
    }

    public void setStuEmail(String stu_email)
    {
        this.stu_email = stu_email;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public String getStuFname()
    {
        return stu_fname;
    }

    public void setStuFname(String stu_fname)
    {
        this.stu_fname = stu_fname;
    }

    public String getStuLname()
    {
        return stu_lname;
    }

    public void setStuLname(String stu_lname)
    {
        this.stu_lname = stu_lname;
    }

    public String getCourse()
    {
        return course;
    }

    public void setCourse(String course)
    {
        this.course = course;
    }

    public double getCourseDuration()
    {
        return course_duration;
    }

    public void setCourseDuration(double course_duration)
    {
        this.course_duration = course_duration;
    }
    
    public Wallet getWallet()
    {
        return wallet;
    }
}