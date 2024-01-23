package scholarship_krish;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Krish
 */
public class Block implements Serializable
{
    private String id;
    private String prev_block_hash;
    private String hash;
    private String contract_hash;
    private byte[] signature;
    private Student student;
    private List<Investor> investors;
    private Broker broker;
    
    public Block(String id, String prev_block_hash, Student student, List<Investor> investors, Broker broker)
    {
        this.student = new Student();
        this.investors = new ArrayList<>();
        this.broker = new Broker();
        this.id = id;
        this.prev_block_hash = prev_block_hash;
        this.student = student;
        this.broker = broker;
        this.investors = investors;
        this.contract_hash = hashContract();
        generateHash();
    }
    
    private String hashContract()
    {   //implement this to generate doc and calculate hash
        try
        {
            String file = CryptographyHelper.generateHash(student.getStuEmail());
            file = file.substring(32, 63);
            file = file + ".txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            String stu_name = student.getStuFname() + " " + student.getStuLname();
            String course = student.getCourse();
            double dur = student.getCourseDuration();
            double cost = student.getAmount();
            writer.append(stu_name + " " + "has successfully been sponsered to study " + course + " \n");
            writer.append("for a duration of " + dur + " years with a total program cost of $" + cost + ".\n");
            writer.append("\nThe student has been sponsered by following investor(s):\n");
            for(Investor inv : investors)
            {
                String inv_name = inv.getInvFname() + " " + inv.getInvLname();
                double amnt = inv.getAmount();
                writer.append(inv_name + " has contibuted $" + amnt + "\n");
            }
            writer.append("\nThe student is bound to pay the investors the amount they have contributed for\n");
            writer.append("the study with 10% interest after the student has secured a job\n");
            writer.close();
        }
        catch(IOException ex)
        {
            System.out.println("\nBlock - > hashContract() -> Could not create contract document");
            ex.printStackTrace();
        }
        String hash = CryptographyHelper.generateHash("contract");
        return hash;
    }
    
    public void signBlock(PrivateKey pri_key)
    {
        signature = CryptographyHelper.applyECDSASignature(pri_key, hash);
    }
    
    private void generateHash()
    {
        String hash_data = id + prev_block_hash + contract_hash + student.getWallet().getPublicKey().toString();
        hash_data = hash_data + student.getStuFname() + student.getStuLname() + student.getStuEmail() + student.getCourse();
        hash_data = hash_data + Double.toString(student.getCourseDuration()) + Double.toString(student.getAmount());
        for (int i = 0; i < investors.size(); i++)
        {
            hash_data = hash_data + investors.get(i).getWallet().getPublicKey().toString();
            hash_data = hash_data + investors.get(i).getInvFname() + investors.get(i).getInvLname();
            hash_data = hash_data + investors.get(i).getInvEmail() + Double.toString(investors.get(i).getAmount());
        }
        hash_data = hash_data + broker.getWallet().getPublicKey() + broker.getName() + broker.getEmail();
        hash_data = hash_data + broker.getAddress() + broker.getBusinessId() + broker.getPhone1() + broker.getPhone2();
        hash_data = hash_data.replace(" ", "");
        hash_data = hash_data.replace("\r\n", "");
        hash_data = CryptographyHelper.generateHash(hash_data);
        this.hash = hash_data;
    }
    
    public String getId()
    {
        return id;
    }

    public String getPrevBlockHash()
    {
        return prev_block_hash;
    }

    public String getHash()
    {
        return hash;
    }

    public String getContractHash()
    {
        return contract_hash;
    }

    public Student getStudent()
    {
        return student;
    }

    public List<Investor> getInvestors()
    {
        return investors;
    }

    public Broker getBroker()
    {
        return broker;
    }
    
    public byte[] getSignature()
    {
        return signature;
    }
}