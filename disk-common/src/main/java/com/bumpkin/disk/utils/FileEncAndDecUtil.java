package com.bumpkin.disk.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.security.Key;
import java.util.Random;

/**
 * @Author: linzhiquan
 * @CreateTime: 2021/04/22 21:13
 */
@Slf4j
public class FileEncAndDecUtil {

    private static String ALGORITHM_PBE = "PBEWITHMD5andDES";
//    private static String password="sacdfsbgdbg";

    /**
     * 文件加密
     * @param srcFile
     * @param encFile
     * @throws Exception
     */
    public static void encFile(File srcFile, File encFile, Key key, byte[] salt) throws Exception {
        //文件长度
        long fileLength = srcFile.length();
        //文件内容
        byte[] fileContent = new byte[(int) fileLength];

        if (!srcFile.exists()) {
            log.warn("source file not exist");
            return;
        }
        if (!encFile.exists()) {
            log.warn("encrypt file created");
            encFile.createNewFile();
        }
        InputStream fis = new FileInputStream(srcFile);
        OutputStream fos = new FileOutputStream(encFile);

        fis.read(fileContent);

//		fos.write(EncoderByBase64(filecontent));//Base64

//		fos.write(EncoderByDES(filecontent));//DES

        fos.write(encoderByPBE(fileContent,key, salt));

        fis.close();
        fos.flush();
        fos.close();
    }

    /**
     * 文件解密
     * @param encFile
     * @param decFile
     * @throws Exception
     */
    public static void decFile(File encFile, File decFile, Key key, byte[] salt) throws Exception {
        //文件长度
        long fileLength = encFile.length();
        //文件内容
        byte[] fileContent = new byte[(int) fileLength];

        if(!encFile.exists()){
            System.out.println("encrypt file not exixt");
            return;
        }
        if(!decFile.exists()){
            System.out.println("decrypt file created");
            decFile.createNewFile();
        }

        InputStream fis  = new FileInputStream(encFile);
        OutputStream fos = new FileOutputStream(decFile);

        fis.read(fileContent);

        fos.write(decoderByPBE(fileContent,key, salt));

        fis.close();
        fos.flush();
        fos.close();
    }

    /**
     * PBE salt初始化
     * @return
     * @throws Exception
     */
    public static String initSalt() throws Exception {
        Random random = new Random();
        int i = random.nextInt(100000000);
        return Integer.toString(i);
    }
    //    public static byte[] initSalt() throws Exception {
//        byte[] salt = new byte[8];
//        Random random = new Random();
//        random.nextBytes(salt);
//        return salt;
//    }

    /**
     * 转换密钥
     * @param password
     * @return
     * @throws Exception
     */
    public static Key toKey(String password) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_PBE);
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        return secretKey;
    }

    /**
     * PBE加密算法
     * @param pbeByte
     * @param key
     * @param salt
     * @return
     * @throws Exception
     */
    private static byte[] encoderByPBE(byte[] pbeByte, Key key, byte[] salt) throws Exception{
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
        Cipher cipher = Cipher.getInstance(ALGORITHM_PBE);
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        return cipher.doFinal(pbeByte);
    }

    /**
     * PBE解密算法
     * @param pbeByte
     * @param key
     * @param salt
     * @return
     * @throws Exception
     */
    private static byte[] decoderByPBE(byte[] pbeByte, Key key, byte[] salt) throws Exception{
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
        Cipher cipher = Cipher.getInstance(ALGORITHM_PBE);
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        return cipher.doFinal(pbeByte);
    }
}
