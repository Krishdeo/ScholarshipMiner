package scholarship_krish;

import java.io.Serializable;

public class Broker implements Serializable
{
    private String name;
    private String business_id;
    private String email;
    private String phone1;
    private String phone2;
    private String address;
    private Wallet wallet;
    
    public Broker()
    {
        wallet = new Wallet();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getBusinessId()
    {
        return business_id;
    }

    public void setBusinessId(String business_id)
    {
        this.business_id = business_id;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhone1()
    {
        return phone1;
    }

    public void setPhone1(String phone1)
    {
        this.phone1 = phone1;
    }
    
    public String getPhone2()
    {
        return phone2;
    }

    public void setPhone2(String phone2)
    {
        this.phone2 = phone2;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }
    
    public Wallet getWallet()
    {
        return wallet;
    }
}