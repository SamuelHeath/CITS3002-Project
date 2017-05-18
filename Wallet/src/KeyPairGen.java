

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

        
/**
 *
 * @author Samuel Heath
 */

public class KeyPairGen {
 
    private static PrivateKey miner_private_key;
    private static PublicKey coin_base_key;
    
    /**
     * @throws NoSuchAlgorithmException 
     */
    public static void generateKeys() throws NoSuchAlgorithmException {
        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048, new SecureRandom());
        KeyPair kp = gen.generateKeyPair();
        coin_base_key = kp.getPublic();
        miner_private_key = kp.getPrivate();
        key2Pem(coin_base_key);
        key2Pem(miner_private_key);
        
    }
    
    public static String getPublicKeyAddress() {
        byte[] address = new byte[20];
        try {
            byte[] pubKey = MessageDigest.getInstance("SHA-256").digest(coin_base_key.getEncoded());
            RIPEMD160Digest ripe = new RIPEMD160Digest();
            ripe.update(pubKey, 0, pubKey.length);
            ripe.doFinal(address, 0);
            return Base58Check.encode(address, true);
        } catch (NoSuchAlgorithmException NSAE) {}
        return "";
    }
    
    /**
     * @return                  The miners public key.
     */
    public static PublicKey getPublicKey() {
        return coin_base_key;
    }
    
    /**
     * @return                  The miner's private key.
     */
    public static PrivateKey getPrivateKey() {
        return miner_private_key;
    }
    
    /**
     * Saves generated keys in a .pem format
     * @param k 
     */
    private static void key2Pem(Key k) {
        
    }
    
}
