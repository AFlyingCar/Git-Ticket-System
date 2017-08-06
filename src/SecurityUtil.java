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

public class SecurityUtil {
    private static Cipher enccipher = null;
    private static Cipher deccipher = null;
    private static SecretKey key = null;
    
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
