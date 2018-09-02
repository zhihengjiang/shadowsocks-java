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
    public void encyptTest() throws Exception {
        byte[] testCase = "hello world, this is pink floyd".getBytes(StandardCharsets.UTF_8);
        AESCrypto cryptoClient = new AESCrypto(CIPHER_AES_256_CFB, "abc123");
        AESCrypto cryptoServer = new AESCrypto(CIPHER_AES_256_CFB, "abc123");
        byte[] en = cryptoClient.encrypt(testCase, testCase.length);
        byte[] de = cryptoServer.decrypt(en, en.length);
        Assert.assertArrayEquals(de, testCase);
        for (int i = 0; i < 100; i++) {
            testCase = Utils.randomBytes(20);
            en = cryptoServer.encrypt(testCase, testCase.length);
            de = cryptoClient.decrypt(en, en.length);
            Assert.assertArrayEquals(de, testCase);

        }


    }
}