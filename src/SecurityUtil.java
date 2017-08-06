import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/** 
 * @author Tyler Robbins
 * @version 1.0
 * @since 1.1
 */
public class SecurityUtil {
    /**
     * The encryption cipher
     */
    private static Cipher enccipher = null;
    
    /**
     * The decryption cipher
     */
    private static Cipher deccipher = null;
    
    /**
     * The secret key to use for encrypting and decrypting
     */
    private static SecretKey key = null;
    
    /**
     * Writes an origin string to a file called .origin in the project ticket directory.
     * @param origin The origin to write.
     */
    public static void writeOrigin(String origin) {
        try {
            File file = new File(FileUtil.getProjectTicketDir(), ".origin");
            if(!file.exists()) file.createNewFile();
            
            if(file.exists()) {
                CipherOutputStream out = new CipherOutputStream(new FileOutputStream(file), getEncryptionCipher());
                // System.out.println(origin);
                out.write(origin.getBytes("UTF8"));
                
                out.close();
            } else {
                System.err.println("Failed to make .origin file.");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Reads an origin string from a file called .origin in the project ticket directory.
     * @return The origin string read or null if the read was unsuccessful.
     */
    public static String readOrigin() {
        try {
            File file = new File(FileUtil.getProjectTicketDir(), ".origin");            
            if(file.exists()) {
                DataInputStream in = new DataInputStream(new CipherInputStream(new FileInputStream(file), getDecryptionCipher()));
                
                byte[] buffer = new byte[8192];
                try {
                    in.readFully(buffer);
                } catch(EOFException e) {
                    // Ignore this exception.
                }
                
                // System.out.println(new String(buffer, "UTF8"));
                
                in.close();
                
                return new String(buffer, "UTF8");
            } else {
                System.err.println("Origin file does not exist!");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets the secret key to be used for encrypting and decrypting. If it exists, then a file in the project ticket directory named
     *  .key will be read for the key. Otherwise, a new key is generated and written to the .key file.
     * @return The SecretKey to be used, null if reading a key and generating a key were failures.
     */
    protected static SecretKey getSecretKey() {
        if(key == null) {
            try {
                File keyFile = new File(FileUtil.getProjectTicketDir(), ".key");
                
                if(keyFile.exists()) {
                    ObjectInputStream in = new ObjectInputStream(new FileInputStream(keyFile));
                    
                    try {
                        key = (SecretKey)in.readObject();
                    } catch(ClassNotFoundException e) {
                        e.printStackTrace();
                        key = null;
                    }
                    
                    in.close();
                } else {
                    try {
                        keyFile.createNewFile();
                        key = KeyGenerator.getInstance("DES").generateKey();
                        
                        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(keyFile));
                        out.writeObject(key);
                        out.close();
                    } catch(NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        key = null;
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
                key = null;
            }
        }
        
        return key;
    }
    
    /**
     * Gets the encryption cipher to use when encrypting data, or generates one if it hasn't been generated yet.
     * @return The Cipher for encrypting data.
     */
    protected static Cipher getEncryptionCipher() {
        if(enccipher == null) {
            try {
                if(getSecretKey() != null) {
                    enccipher = Cipher.getInstance("DES");
                    enccipher.init(Cipher.ENCRYPT_MODE, key);
                }
            } catch(NoSuchAlgorithmException e) {
                e.printStackTrace();
                enccipher = null;
            } catch (InvalidKeyException e) {
                e.printStackTrace();
                enccipher = null;
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
                enccipher = null;
            }
        }
        
        return enccipher;
    }
    
    /**
     * Gets the decryption cipher to use when decrypting data, or generates one if it hasn't been generated yet.
     * @return The Cipher for decrypting data.
     */
    protected static Cipher getDecryptionCipher() {
        if(deccipher == null) {
            try {
                if(getSecretKey() != null) {
                    deccipher = Cipher.getInstance("DES");
                    deccipher.init(Cipher.DECRYPT_MODE, key);
                }
            } catch(NoSuchAlgorithmException e) {
                e.printStackTrace();
                deccipher = null;
            } catch (InvalidKeyException e) {
                e.printStackTrace();
                deccipher = null;
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
                deccipher = null;
            }
        }
        
        return deccipher;
    }
    
    /**
     * Encrypts a string.
     * @param str The string to encrypt.
     * @return The encrypted data as a byte[], or null if an error occurred.
     */
    public static byte[] encrypt(String str) {
        try {
            if(getEncryptionCipher() != null) {
                byte[] enc = enccipher.doFinal(str.getBytes("UTF8"));
                return enc;
            }
        } catch(javax.crypto.BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Decrypts a string.
     * @param str The data to decrypt
     * @return The decrypted data as a UTF-8 string or null if an error occurred.
     */
    public static String decrypt(byte[] str) {
        try {
            if(getDecryptionCipher() != null) {
                byte[] utf8 = deccipher.doFinal(str);
                
                return new String(utf8, "UTF8");
            }
        } catch(javax.crypto.BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
