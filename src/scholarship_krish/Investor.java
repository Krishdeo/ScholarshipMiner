package scholarship_krish;

import java.io.Serializable;

public class Investor implements Serializable
{
    private double amount;
    private String inv_email;
    private String inv_fname;
    private String inv_lname;
    private Wallet wallet;
    
    public Investor()
    {
        wallet = new Wallet();
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public String getInvEmail()
    {
        return inv_email;
    }

    public void setInvEmail(String inv_email)
    {
        this.inv_email = inv_email;
    }

    public String getInvFname()
    {
        return inv_fname;
    }

    public void setInvFname(String inv_fname)
    {
        this.inv_fname = inv_fname;
    }

    public String getInvLname()
    {
        return inv_lname;
    }

    public void setInvLname(String inv_lname)
    {
        this.inv_lname = inv_lname;
    }
    
    public Wallet getWallet()
    {
        return wallet;
    }
}
