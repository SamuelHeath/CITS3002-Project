package core;

import java.io.BufferedReader;
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
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.bouncycastle.util.io.pem.PemReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
        
/**
 *
 * @author Samuel Heath
 */

public class KeyPairGen {
 
    private static PrivateKey miner_private_key;
    private static PublicKey coin_base_key;
    
    private static final String PUBLIC_KEY_NAME = "public.pem";
    private static final String PRIVATE_KEY_NAME = "private.pem";
    
    public static void readKeys() {
        try { 
            PemReader pR = new PemReader(new BufferedReader(new FileReader(PRIVATE_KEY_NAME)));
            PemObject pO = pR.readPemObject();
            PKCS8EncodedKeySpec key = new PKCS8EncodedKeySpec(pO.getContent());
            miner_private_key = KeyFactory.getInstance("RSA").generatePrivate(key);
            pR = new PemReader(new InputStreamReader(new FileInputStream(PRIVATE_KEY_NAME)));
            pO = pR.readPemObject();
            key = new PKCS8EncodedKeySpec(pO.getContent());
            coin_base_key = KeyFactory.getInstance("RSA").generatePublic(key);
        } catch (FileNotFoundException FNFE) {
            System.out.println("Failed");
            generateKeys();
        } catch (NoSuchAlgorithmException NSAE) {
            System.out.println("Failed");
        } catch (InvalidKeySpecException IKSE) {
            System.out.println("Failed");
            IKSE.printStackTrace();
        } catch (IOException IOE) {}
    }
    
    /** 
     */
    public static void generateKeys() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048, new SecureRandom());
            KeyPair kp = gen.generateKeyPair();
            coin_base_key = kp.getPublic();
            miner_private_key = kp.getPrivate();
            key2Pem(coin_base_key,PUBLIC_KEY_NAME, "RSA PUBLIC KEY");
            key2Pem(miner_private_key,PRIVATE_KEY_NAME, "RSA PRIVATE KEY");
        } catch (NoSuchAlgorithmException NSAE) {}
        
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
    private static void key2Pem(Key k, String fileName, String type) {
        try {
            PemObject pO = new PemObject(type,k.getEncoded());
            PemWriter pW = new PemWriter(new BufferedWriter(new FileWriter(fileName)));
            pW.writeObject(pO);
            pW.close();
        } catch (IOException IOE) {}
    }
    
}
