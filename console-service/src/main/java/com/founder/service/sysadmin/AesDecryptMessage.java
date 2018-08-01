package com.founder.service.sysadmin;

import com.founder.contract.sysadmin.DecryptMessage;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;

public class AesDecryptMessage implements DecryptMessage {
    private static Key key;
    private static String KEY_STR="hisuntech";

    static{
        try
        {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom=SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(KEY_STR.getBytes());
            generator.init(secureRandom);
            key = generator.generateKey();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String getEncryptString(String str){
        BASE64Encoder base64Encoder = new BASE64Encoder();
        System.out.println(key);
        try
        {
            byte[] strBytes = str.getBytes("UTF-8");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptStrBytes = cipher.doFinal(strBytes);
            return base64Encoder.encode(encryptStrBytes);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }


    public static String getDecryptString(String str){
        BASE64Decoder base64Decoder = new BASE64Decoder();
        try
        {
            byte[] strBytes = base64Decoder.decodeBuffer(str);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptStrBytes = cipher.doFinal(strBytes);
            return new String(encryptStrBytes,"UTF-8");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }


    public static void main(String[] args)
    {
        String name ="fzdb";
        String password="fzdb";
        String url = "jdbc:oracle:thin:@192.168.1.100:1521:orcl";
        String encryname = getEncryptString(name);
        String encrypassword = getEncryptString(password);
        String encryurl = getEncryptString(url);
        System.out.println(encryname);
        System.out.println(encrypassword);
        System.out.println(encryurl);

        System.out.println(getDecryptString(encryname));
        System.out.println(getDecryptString(encrypassword));
        System.out.println(getDecryptString(encryurl));
    }

    @Override
    public String decrypt(String encryptStr) {
        return getDecryptString(encryptStr);
    }
}
