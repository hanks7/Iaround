/************************************
 *
 * Copyright 2010 u6 to iAround
 *
 * @Description:该类提供加解密操作，含有AES加密/解密，MD5、SHA-1数据签名、生成密钥种子、密钥；其中AES加密采用默认填充模式。
 * @author linyg
 * @Date:2010-07-30 11:48:38
 * @Version v1.0
 */

package net.iaround.tools;


import android.text.TextUtils;


import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import javax.crypto.Cipher;


public class CryptoUtil {
    /**
     * 指定加密算法为RSA
     */
    private static String ALGORITHM = "RSA";

    private static final String HASH_ALGORITHM = "MD5";

    private static final int RADIX = 10 + 26; // 10 digits + 26 letters

    public final static String SZF_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCPSU/43cp8Vk8Obie0zHpkN8wtAq5yK7KuT1Sq+l0zc5zmGaFuX6HK9DFvbRHkqpM8fxAekKpJQWwT/7/8lAHYoNRefZ8mMzCMBUBJcvk9iavxZVVYCFGsDETMXv4a9mpJ/4U351DVfoEndijjuBl6vYOvDNFRis3dJecvd4DhkwIDAQAB";

    public final static String VERIFY_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALyzLyl++XfLYAHCHRxDvlvpyrbFFpgpCPHad2iYeYt6lLmPh89rBkQF1U+UzVM4BQaJR8cBdOe6WV0XOnOKUNeV1sJackHwuAIySGdaeN8EHyeUsP8s013f9RLfh+meAoazb0psVdy0uQi/y5FB8CfRbOTGMLiR5qDklbx5HWc5AgMBAAECgYAYHqtDmZhXqKZP8zj3WO1nID4qGI0ny+75wxwKQZsfqf6CrYcFiz+UyE4YywNnxLxXtop3NId160rq7EIrbJ2iiIc172dprJW5mVvkpA5hjXZrsmNG7a4qbimwsQ+UL7XUpUPdbUpsTptGBWqPWYAM5Fj4tntGWGxAx19iuV5nxQJBAO22l0GGkPODuhpf5mYTM8gtd9HAatpUpXv5vA9UpJtyI+S9Co156g5uiSLO/1O7qrLvVV61ISx4EFur3g0iCvsCQQDLN1lgc1/xwzwsJ4Snaoo0JvKSOmcA1g6XA1mpt5YTldhmGtWzaFonqery7woHZ5p5l/ZJE3qt3vz5AIqSsoBbAkEAwR71NVPY0S10dVbx/H2fNQLTdEHJzYS1SMo0IZRXgr4xMPTv7M5d+8mLeg8HWgR+Ao70IVQwFkuYppEzfiHpHwJADTWX1bEqN2jdbHFVKhy+xw0RD3hEYcWIRUCuf12zn6pJ2rk70uGhmpps70WifUC6xdSe6bkebFbmUrk6SLbZqQJBANGicPrgoDJIqRdN3OZSBCZjhzncEOdHllgt7A8JIRsqWaKPXnvj+EhFxvqTNIBkg3p4sHi6K1ePTRqvcP7k11c=";

    public final static String VERIFY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/lXqzEXMv1Db8QBiGnIugMoApdYvl5VkGEctr8cobZnLck5JwU+gxTU1gcydjFS/ttVumZdCMCtfxDQq1+wedPHa6vfqC13RUM9ZR0pcEd/3QfO0xnz+J7JKA5AAWNlhRGnxTUIW3qkBouukvjhvDWyKJDjUsjfsaZBnF1NIJ1wIDAQAB";

    public final static String SERVER_PUBLIC_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL+VerMRcy/UNvxAGIaci6AygCl1i+XlWQYRy2vxyhtmctyTknBT6DFNTWBzJ2MVL+21W6Zl0IwK1/ENCrX7B508drq9+oLXdFQz1lHSlwR3/dB87TGfP4nskoDkABY2WFEafFNQhbeqQGi66S+OG8NbIokONSyN+xpkGcXU0gnXAgMBAAECgYEAqvUHTZ1HrSlwe33ypMQysBwAU36p/NbArvLYFBXppLOx0XT6GrQ1ITF615NKVVdFspfAFecKxryyX06k/Sjpa+8kR23Cp1sVWh2P4ACcvPVupVBc3DWDfuoDpjRy2mdIJ3XipjL11gm+Q+cL3m/Cn8fQPxLvoRGTxJ9Ug6kvoUkCQQD3hs2YV6q+YlLXyqtHNnokZh5UkJ/TnMrOh4Q7nczaGniUjKM03OXNwQS6Wqty0DCPIsjMB8T0rz+x9SJZQDObAkEAxiRsUBxMDe5PdW9bytNZxCuSslSgUX3uE6uQwa5isBlF4scqRbobQrKS5HniAEORqmJYgJ+hgfpmxU/BxZicdQJAeJ54EYSm7596Py3DyTAC1TnuDcdsGvKM4ZeoGGUNLjs4ByuBXIFIertUUntckNZi011f/AzNLW842r9ZvaRLgQJBAL+wvyTlq2KS2kaHnGtl+2NAsGRcYNlWFnzrL1lhR1KMsMLL4gahHPxy3I+zpsSQmh0Xlf2h8dNYmBJtrc5nbX0CQFXsBWJ4isSKoBIPiJK0ULJ6AeZhZ32fE9gw0xZlCNWu2h8pm/QFrc2ceaAS4kDZLIrjksKMo+hv4WZQcKap5TA=";

    /**
     * 使用MD5产生消息摘要
     * <p>
     * soures 待产生消息摘要的字符串
     *
     * @return 返回十六进制字符串
     */
    public static String md5(String data) {
        if (TextUtils.isEmpty(data)) {
            return "";
        }

        String s = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            byte[] tmp = md.digest();
            s = HEX.toHex(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 使用SHA-1产生消息摘要
     * <p>
     * 待产生消息摘要的字符串
     *
     * @return 返回十六进制字符串
     */
    public static String SHA1(String data) {
        String s = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(data.getBytes("UTF-8"));
            byte[] tmp = md.digest();
            s = HEX.toHex(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 和ImageLoader中生成文件夹的方法相同，
     *
     * @param imageUri
     * @return
     * @See {@link Md5FileNameGenerator}
     */
    public static String generate(String imageUri) {
        byte[] md5 = getMD5(imageUri.getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        return bi.toString(RADIX);
    }

    private static byte[] getMD5(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(data);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {

        }
        return hash;
    }

    /**
     * 使用SHA-1产生消息摘要
     * <p>
     * 待产生消息摘要的字符串
     *
     * @return 返回十六进制字符串
     */
    public static String SHA1_base64(String data) {
        String s = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(data.getBytes("UTF-8"));
            byte[] tmp = md.digest();
            s = Base64.encodeBytes(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 随机生成100-100000的整数
     *
     * @return String
     */
    public static String getRandom() {
        Random rand = new Random(System.currentTimeMillis());
        return Integer.toString(1000 + rand.nextInt(10000000));
    }

    /**
     * 得到公钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PublicKey getRSAPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64Helper.decode(key);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getRSAPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64Helper.decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 加密方法 source： 源数据
     */
    public static String rsaEncrypt(String source, String publicKeyStr) throws Exception {
        PublicKey publicKey = getRSAPublicKey(publicKeyStr);
        /** 得到Cipher对象来实现对源数据的RSA加密 */
        // Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
        // Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] b = source.getBytes();
        /** 执行加密操作 */
        byte[] b1 = cipher.doFinal(b);
        return Base64Helper.encode(b1);
    }

    /**
     * 解密算法 cryptograph:密文
     */
    public static String rsaDecrypt(String cryptograph, String privateKeyStr)
            throws Exception {
        PrivateKey privateKey = getRSAPrivateKey(privateKeyStr);
        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        // Cipher cipher = Cipher.getInstance(ALGORITHM);
        // Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] b1 = Base64Helper.decode(cryptograph);
        /** 执行解密操作 */
        byte[] b = cipher.doFinal(b1);
        return new String(b);
    }

    public static String Base64Encoder(byte[] srcData, int length) {
        return Base64.encodeBytes(srcData, 0, length);
    }

    public static byte[] Base64Decoder(String strData) {
        return Base64.decode(strData);
    }
}
