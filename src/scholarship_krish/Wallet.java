package scholarship_krish;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.Serializable;

/**
 *
 * @author Krish
 */
public class Wallet implements Serializable
{
    private PrivateKey private_key;
    private PublicKey public_key;

    public Wallet()
    {
        KeyPair key_pair = CryptographyHelper.ellipticCurveCrypto();
        this.private_key = key_pair.getPrivate();
        this.public_key = key_pair.getPublic();
    }

    public void setPublic_key(PublicKey public_key)
    {
        this.public_key = public_key;
    }

    public PrivateKey getPrivateKey()
    {
        return this.private_key;
    }

    public PublicKey getPublicKey()
    {
        return this.public_key;
    }

    public void setPrivateKey(PrivateKey private_key)
    {
        this.private_key = private_key;
    }

    public void setPublicKey(PublicKey public_key)
    {
        this.public_key = public_key;
    }
}
