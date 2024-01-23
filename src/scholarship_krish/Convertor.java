package scholarship_krish;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Convertor
{
    public static PublicKey stringToPublicKey(String str_key)
    {
        byte[] byte_pubkey  = Base64.getDecoder().decode(str_key);
        PublicKey public_key = null;
        try
        {
            KeyFactory factory = KeyFactory.getInstance("ECDSA", "BC");
            public_key = (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(byte_pubkey));
        }
        catch(NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException ex)
        {
            System.out.println("\nConvertor -> stringToPublicKey() -> Could not convert string to public key");
            ex.printStackTrace();
        }
        return public_key;
    }
    
    public static PublicKey byteToPublicKey(byte[] byte_pubkey)
    {
        PublicKey public_key = null;
        try
        {
            KeyFactory factory = KeyFactory.getInstance("ECDSA", "BC");
            public_key = (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(byte_pubkey));
        }
        catch(NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException ex)
        {
            System.out.println("\nConvertor -> byteToPublicKey() -> Could not convert byte to public key");
            ex.printStackTrace();
        }
        return public_key;
    }
    
    public static byte[] publicKeyToByte(PublicKey public_key)
    {
        byte[] byte_pubkey = public_key.getEncoded();
        return byte_pubkey;
    }
    
    public static String publicKeyToString(PublicKey public_key)
    {
        byte[] byte_pubkey = public_key.getEncoded();
        String str_key = Base64.getEncoder().encodeToString(byte_pubkey);
        return str_key;
    }
    
    public static Object stringToObject(String str_obj)
    {
        byte[] byte_obj  = Base64.getDecoder().decode(str_obj);
        ByteArrayInputStream byte_brray_input_stream = new ByteArrayInputStream(byte_obj);
        ObjectInputStream object_input_stream = null;
        Object object = null;
        try
        {
            object_input_stream = new ObjectInputStream(byte_brray_input_stream);
            object = object_input_stream.readObject();
        }
        catch(IOException | ClassNotFoundException ex)
        {
            System.out.println("\nConvertor -> stringToObject() -> Could not convert string to object");
            ex.printStackTrace();
        }
        return object;
    }
    
    public static Object byteToObject(byte[] byte_obj)
    {
        ByteArrayInputStream byte_brray_input_stream = new ByteArrayInputStream(byte_obj);
        ObjectInputStream object_input_stream = null;
        Object object = null;
        try
        {
            object_input_stream = new ObjectInputStream(byte_brray_input_stream);
            object = object_input_stream.readObject();
        }
        catch(IOException | ClassNotFoundException ex)
        {
            System.out.println("\nConvertor -> byteToObject() ->  Could not convert byte to object");
            ex.printStackTrace();
        }
        return object;
    }
    
    public static String objectToString(Object object)
    {
        ByteArrayOutputStream byte_array_output_stream = new ByteArrayOutputStream();
        ObjectOutputStream object_output_stream = null;
        try
        {
            object_output_stream = new ObjectOutputStream(byte_array_output_stream);
            object_output_stream.writeObject(object);
        }
        catch(IOException ex)
        {
            System.out.println("\nConvertor -> objectToString() -> Could not convert object to string");
            ex.printStackTrace();
        }
        byte[] byte_obj = byte_array_output_stream.toByteArray();
        String str_obj = Base64.getEncoder().encodeToString(byte_obj);
        return str_obj;
    }
    
    public static byte[] objectToByte(Object object)
    {
        ByteArrayOutputStream byte_array_output_stream = new ByteArrayOutputStream();
        ObjectOutputStream object_output_stream = null;
        try
        {
            object_output_stream = new ObjectOutputStream(byte_array_output_stream);
            object_output_stream.writeObject(object);
        }
        catch(IOException ex)
        {
            System.out.println("\nConvertor -> objectToByte() -> Could not convert object to byte");
            ex.printStackTrace();
        }
        byte[] byte_obj = byte_array_output_stream.toByteArray();
        return byte_obj;
    }
}
