package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

        
/**
 *
 * @author Nerces Kahwajian – 215922645	& Samuel Heath – 21725083
 */

public class KeyPairGen {
 
    //Keys to be stored/loaded in by this class.
    private static PrivateKey miner_private_key;
    private static PublicKey coin_base_key;
    private static final String PUBLIC_KEY_NAME = "public.pem";
    private static final String PRIVATE_KEY_NAME = "private.pem";
    private static final String RECIEVER_ADDRESS_NAME = "receiver.pem";
    
    public static void readKeys() {
        java.security.Security.addProvider(new BouncyCastleProvider());
        File pubFile = new File(PUBLIC_KEY_NAME);
        File priFile = new File(PRIVATE_KEY_NAME);
        if (pubFile.exists() && priFile.exists()) {
            try {
                System.out.println("Reading Keys.");
                PemReader priR = new PemReader(new BufferedReader(new FileReader(priFile)));
                PemObject priO = priR.readPemObject();
                PKCS8EncodedKeySpec key = new PKCS8EncodedKeySpec(priO.getContent());
                miner_private_key = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(key);
                PemReader pubR  = new PemReader(new InputStreamReader(new FileInputStream(pubFile)));
                PemObject pubO = pubR.readPemObject();
                X509EncodedKeySpec pubkey = new X509EncodedKeySpec(pubO.getContent());
                coin_base_key = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(pubkey);
                System.out.println("Keys Read.");
                priR.close();
                pubR.close();
            } catch (FileNotFoundException FNFE) {
            } catch (NoSuchAlgorithmException NSAE) {
            } catch (InvalidKeySpecException IKSE) {
                IKSE.printStackTrace();
            } catch (IOException IOE) {}
        } else {
            System.out.println("Couldn't Find Keys.");
            generateKeys();
        }
    }
    
    public static String getReceiverAddress() {
        File f = new File(RECIEVER_ADDRESS_NAME);
        java.security.Security.addProvider(new BouncyCastleProvider());
        if (f.exists()) {
            System.out.println("\nReading receiver's key");
            String recAddress = "";
            try {
                PemReader addressR = new PemReader(new BufferedReader(new FileReader(f)));
                PemObject addressO = addressR.readPemObject();
                X509EncodedKeySpec address = new X509EncodedKeySpec(addressO.getContent());
                PublicKey recPK = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(address);
                recAddress = receiverKey2Address(recPK);
            } catch (FileNotFoundException FNFE) {
            } catch (NoSuchAlgorithmException NSAE) {
            } catch (InvalidKeySpecException IKSE) {
            } catch (IOException IOE) {}
            return recAddress;
        } else {
            System.out.println("\nNo receiving address given.\n");
            System.exit(-1);
            return "";
        }
    }
    
    /**
     * Creates new keys for this wallet software if it cannot find any in working directory.
     */
    private static void generateKeys() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048, new SecureRandom());
            KeyPair kp = gen.generateKeyPair();
            coin_base_key = kp.getPublic();
            miner_private_key = kp.getPrivate();
            key2Pem(coin_base_key,PUBLIC_KEY_NAME,"RSA PUBLIC KEY");
            key2Pem(miner_private_key,PRIVATE_KEY_NAME,"RSA PRIVATE KEY");
        } catch (NoSuchAlgorithmException NSAE) {
            
        }
        
    }
    
    private static String receiverKey2Address(PublicKey pk) {
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
    
    public static String publicKey2String(PublicKey pub_key) {
        return Base58Check.encode(pub_key.getEncoded(),false);
    }
    
    public static PublicKey publicKeyFromString(String key) {
        PublicKey pk = getPublicKey(); //This is bad
        try {
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(key.getBytes(StandardCharsets.US_ASCII));
            KeyFactory kf = KeyFactory.getInstance("RSA");

            pk = kf.generatePublic(X509publicKey);
        } catch (Exception E) {}
        return pk;
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
