package scholarship_krish;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 *
 * @author Krish
 */
public class CryptographyHelper
{
    public static String generateHash(String data)
    {
        StringBuffer hex_hash = null;
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] byte_hash = digest.digest(data.getBytes("UTF-8"));
            hex_hash = new StringBuffer();
            for(int i = 0; i < byte_hash.length; i++)
            {
                String temp_hexa = Integer.toHexString(0xff & byte_hash[i]);
                if(temp_hexa.length() == 1)
                {
                    hex_hash.append("0");
                }
                hex_hash.append(temp_hexa);
            }
        }
        catch(Exception ex)
        {
            System.out.println("\nCryptographyHelper -> generateHash() -> Hashing failed");
            ex.printStackTrace();
        }
        return hex_hash.toString();
    }

    public static KeyPair ellipticCurveCrypto()
    {
        try
        {
            KeyPairGenerator key_pair_generator = KeyPairGenerator.getInstance("ECDSA", "BC");
            key_pair_generator.initialize(Constants.KEY_PAIR_SIZE);
            KeyPair key_pair = key_pair_generator.generateKeyPair();
            return key_pair;
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException ex)
        {
            System.out.println("\nCryptographyHelper -> ellipticCurveCrypto() -> Could not generate keypairs");
            ex.printStackTrace();
        }
        return null;
    }
    
    public static byte[] applyECDSASignature(PrivateKey private_key, String input)
    {
        Signature sign;
        byte[] output = new byte[0];
        try
        {
            sign = Signature.getInstance("ECDSA", "BC");
            sign.initSign(private_key);
            byte[] str_byte = input.getBytes();
            sign.update(str_byte);
            byte[] signature = sign.sign();
            output = signature;
        }
        catch(Exception ex)
        {
            System.out.println("\nCryptographyHelper -> applyECDSASignature() -> Signing failed");
            ex.printStackTrace();
        }
        return output;
    }
    
    public static boolean verifyECDSASignature(PublicKey public_key, String input, byte[] sign)
    {
        try
        {
            Signature signature = Signature.getInstance("ECDSA", "BC");
            signature.initVerify(public_key);
            byte[] str_byte = input.getBytes();
            signature.update(str_byte);
            return signature.verify(sign);
        }
        catch(Exception ex)
        {
            System.out.println("\nCryptographyHelper -> verifyECDSASignature() -> Signature could not be verified");
            ex.printStackTrace();
        }
        return false;
    }
}
