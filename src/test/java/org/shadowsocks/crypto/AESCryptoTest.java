package org.shadowsocks.crypto;

import org.junit.Assert;
import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static java.lang.System.arraycopy;
import static org.shadowsocks.crypto.AESCrypto.CIPHER_AES_256_CFB;

public class AESCryptoTest {

    @Test
    public void encryptAndDecrypt() throws Exception {
        AESCrypto crypto = new AESCrypto(CIPHER_AES_256_CFB, "abc123");
        byte[] bytes = "Hello World, my little baby, 2017".getBytes();
        byte[] encrypt = crypto.encrypt(bytes, bytes.length);
        byte[] data = Arrays.copyOfRange(encrypt,crypto.getIVLength(),encrypt.length);
        byte[] decrypt = crypto.decrypt(data, data.length);
        Assert.assertArrayEquals(bytes, decrypt);
    }

    @Test
    public void decrypt() throws Exception{
        AESCrypto cryptoClient = new AESCrypto(CIPHER_AES_256_CFB, "abc123");
        AESCrypto cryptoServer = new AESCrypto(CIPHER_AES_256_CFB, "abc123");
        byte[]  bytes = "Hedasjdkasjfl;ajsfa;lsjfk;asjfasfj;".getBytes(StandardCharsets.UTF_8);
        byte[] encrypt = cryptoClient.encrypt(bytes,bytes.length);
        byte[] decrypt = cryptoServer.decrypt(encrypt,encrypt.length);
        Assert.assertArrayEquals(cryptoClient.getIV(false),cryptoServer.getIV(true));
        Assert.assertArrayEquals(decrypt,bytes);
        for (int i = 0;i < 100;i++){
            System.out.println(i);
            byte[] testCase = Utils.randomBytes(20);
            byte[] en = cryptoClient.encrypt(testCase,testCase.length);
            byte[] de = cryptoServer.decrypt(en,en.length);
            Assert.assertArrayEquals(de,testCase);
            Assert.assertArrayEquals(cryptoClient.getIV(false),cryptoServer.getIV(true));
        }

    }

    @Test
    public void encyptedBytesCouldBeSliced() throws Exception {
        AESCrypto aesCrypto = new AESCrypto(AESCrypto.CIPHER_AES_256_CFB, "abc123");
        byte[] fullBytes = "12121211331313131,who am i,12345555".getBytes();
        byte[] encrypt = aesCrypto.encrypt(fullBytes, fullBytes.length);

        byte[] iv = Arrays.copyOfRange(encrypt,0,aesCrypto.getIVLength());//取出IV

        encrypt = Arrays.copyOfRange(encrypt,aesCrypto.getIVLength(),encrypt.length);//取出真正加密密文
        Assert.assertArrayEquals(iv,aesCrypto.getIV(false));

        int someLength = 18;
        byte[] halfEncryptOne = new byte[someLength];
        byte[] halfEncryptTwo = new byte[encrypt.length - someLength];
        arraycopy(encrypt, 0, halfEncryptOne, 0, halfEncryptOne.length);
        arraycopy(encrypt, someLength, halfEncryptTwo, 0, halfEncryptTwo.length);

        byte[] decryptOne = aesCrypto.decrypt(halfEncryptOne, halfEncryptOne.length);
        byte[] decryptTwo = aesCrypto.decrypt(halfEncryptTwo, halfEncryptTwo.length);
        byte[] joint = new byte[decryptOne.length + decryptTwo.length];
        arraycopy(decryptOne, 0, joint, 0, decryptOne.length);
        arraycopy(decryptTwo, 0, joint, decryptOne.length, decryptTwo.length);
        Assert.assertArrayEquals(fullBytes, joint);
    }

    @Test
    public void repeatDecrpt() throws Exception{
        byte[] testCase = "hello world, this is pink floyd".getBytes(StandardCharsets.UTF_8);
        AESCrypto cryptoClient = new AESCrypto(CIPHER_AES_256_CFB, "abc123");
        AESCrypto cryptoServer = new AESCrypto(CIPHER_AES_256_CFB, "abc123");
        byte[] en = cryptoClient.encrypt(testCase,testCase.length);
        byte[] de = cryptoServer.decrypt(en,en.length);

        for(int i = 0; i < 100; i++){
            en = cryptoClient.encrypt(testCase,testCase.length);

            Assert.assertArrayEquals(cryptoClient.getIV(false),cryptoServer.getIV(true));

            int someLength = new Random(i).nextInt(testCase.length);
            byte[] halfEncryptOne = new byte[someLength];
            byte[] halfEncryptTwo = new byte[en.length - someLength];
            arraycopy(en, 0, halfEncryptOne, 0, halfEncryptOne.length);
            arraycopy(en, someLength, halfEncryptTwo, 0, halfEncryptTwo.length);

            byte[] decryptOne = cryptoServer.decrypt(halfEncryptOne, halfEncryptOne.length);
            byte[] decryptTwo = cryptoServer.decrypt(halfEncryptTwo, halfEncryptTwo.length);

            byte[] joint = new byte[decryptOne.length + decryptTwo.length];
            arraycopy(decryptOne, 0, joint, 0, decryptOne.length);
            arraycopy(decryptTwo, 0, joint, decryptOne.length, decryptTwo.length);
            Assert.assertArrayEquals(testCase, joint);
        }
    }

    @Test
    public void encyptTest() throws Exception{
        byte[] testCase = "hello world, this is pink floyd".getBytes(StandardCharsets.UTF_8);
        AESCrypto cryptoClient = new AESCrypto(CIPHER_AES_256_CFB, "abc123");
        AESCrypto cryptoServer = new AESCrypto(CIPHER_AES_256_CFB, "abc123");
        byte[] en = cryptoClient.encrypt(testCase,testCase.length);
        byte[] de = cryptoServer.decrypt(en,en.length);
        Assert.assertArrayEquals(de,testCase);
        for(int i = 0; i < 100;i ++){
            testCase = Utils.randomBytes(20);
            en = cryptoServer.encrypt(testCase,testCase.length);
            de = cryptoClient.decrypt(en,en.length);
            Assert.assertArrayEquals(de,testCase);

        }
    }

    @Test
    public void duplexDecrypt() throws Exception{
        AESCrypto cryptoClient = new AESCrypto(CIPHER_AES_256_CFB, "abc123");
        AESCrypto cryptoServer = new AESCrypto(CIPHER_AES_256_CFB, "abc123");
        byte[]  bytes = "Hedasjdkasjfl;ajsfa;lsjfk;asjfasfj;".getBytes(StandardCharsets.UTF_8);
        byte[] encrypt = cryptoClient.encrypt(bytes,bytes.length);
        byte[] decrypt = cryptoServer.decrypt(encrypt,encrypt.length);
        Assert.assertArrayEquals(decrypt,bytes);
        encrypt = cryptoServer.encrypt(bytes,bytes.length);
        decrypt = cryptoClient.decrypt(encrypt,bytes.length);
        Assert.assertArrayEquals(decrypt,bytes);
        for(int i = 0; i < 100; i++){
            encrypt = cryptoClient.encrypt(bytes,bytes.length);
            decrypt = cryptoServer.decrypt(encrypt,encrypt.length);
            Assert.assertArrayEquals(decrypt,bytes);
            encrypt = cryptoServer.encrypt(bytes,bytes.length);
            decrypt = cryptoClient.decrypt(encrypt,bytes.length);
            Assert.assertArrayEquals(decrypt,bytes);
        }
    }



}