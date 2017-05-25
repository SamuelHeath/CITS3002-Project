package core;

import java.io.BufferedReader;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.bouncycastle.util.io.pem.PemReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
        
/**
 *
 * @author Samuel Heath
 */

public class KeyPairGen {
 
    private static RSAPrivateKey miner_private_key;
    private static RSAPublicKey coin_base_key;
    
    private static final String PUBLIC_KEY_NAME = "public.pem";
    private static final String PRIVATE_KEY_NAME = "private.pem";
    
    /**
     * Attempts to read the public and private keys currently stored in the
     * working directory, but if there are none, it then generates new ones.
     * NOTE: Public address for this miner will be used as the coinbase address
     * for transactions.
     */
    public static void readKeys() {
        java.security.Security.addProvider(new BouncyCastleProvider());
        File pubFile = new File(PUBLIC_KEY_NAME);
        File priFile = new File(PRIVATE_KEY_NAME);
        if (pubFile.exists() && priFile.exists()) {
            try { 
                PemReader priR = new PemReader(new BufferedReader(new FileReader(PRIVATE_KEY_NAME)));
                PemObject priO = priR.readPemObject();
                PKCS8EncodedKeySpec key = new PKCS8EncodedKeySpec(priO.getContent());
                miner_private_key = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(key);
                PemReader pubR  = new PemReader(new InputStreamReader(new FileInputStream(PUBLIC_KEY_NAME)));
                PemObject pubO = pubR.readPemObject();
                X509EncodedKeySpec pubkey = new X509EncodedKeySpec(pubO.getContent());
                coin_base_key = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(pubkey);
                priR.close();
                pubR.close();
            } catch (FileNotFoundException FNFE) {
            } catch (NoSuchAlgorithmException NSAE) {
            } catch (InvalidKeySpecException IKSE) {
                IKSE.printStackTrace();
            } catch (IOException IOE) {}
        } else {
            System.out.println("Couldn't Find Keys");
            generateKeys();
        }

    }
    
    /**
     * Generates new public/private key pair and then saves them as .pem in the
     * working dir.
     */
    public static void generateKeys() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048, new SecureRandom());
            KeyPair kp = gen.generateKeyPair();
            coin_base_key = (RSAPublicKey) kp.getPublic();
            miner_private_key = (RSAPrivateKey) kp.getPrivate();
            key2Pem(coin_base_key,PUBLIC_KEY_NAME, "RSA PUBLIC KEY");
            key2Pem(miner_private_key,PRIVATE_KEY_NAME, "RSA PRIVATE KEY");
        } catch (NoSuchAlgorithmException NSAE) {}
        
    }
    
    /**
     * @return                  Returns the public ChrisCoin address for the miner
     * based on how Bitcoin addresses are created.
     */
    public static String getPublicKeyAddress() {
        byte[] address = new byte[20];
        try {
            byte[] pubKey = MessageDigest.getInstance("SHA-256").digest(coin_base_key.getEncoded());
            RIPEMD160Digest ripe = new RIPEMD160Digest();
            ripe.update(pubKey, 0, pubKey.length);
            ripe.doFinal(address, 0);
            return Base58Check.encode(address, false);
        } catch (NoSuchAlgorithmException NSAE) {}
        return "";
    }
    
    public static PublicKey publicKeyFromString(String key) {
        PublicKey pk = getPublicKey(); //This is bad
        try {
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(Base58Check.decode(key,false));
            KeyFactory kf = KeyFactory.getInstance("RSA");

            pk = kf.generatePublic(X509publicKey);
        } catch (Exception E) {}
        return pk;
    }
    
    /**
     * @return                  The miners public key.
     */
    public static RSAPublicKey getPublicKey() {
        return coin_base_key;
    }
    
    /**
     * @return                  The miner's private key.
     */
    public static RSAPrivateKey getPrivateKey() {
        return miner_private_key;
    }
    
    /**
     * Saves generated keys in a .pem format
     * @param k 
     */
    private static void key2Pem(Key k, String fileName, String type) {
        try {
            PemObject pO = new PemObject(type,k.getEncoded());
            PemWriter pW = new PemWriter(new BufferedWriter(new FileWriter(fileName)));
            pW.writeObject(pO);
            pW.close();
        } catch (IOException IOE) {}
    }
    
}
