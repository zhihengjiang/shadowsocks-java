/*   
 *   Copyright 2016 Author:NU11 bestoapache@gmail.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.shadowsocks.crypto;

import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.io.ByteArrayOutputStream;

/**
 * AES Crypt implementation
 */
public class AESCrypto extends BaseCrypto {

    public final static String CIPHER_AES_128_CFB = "aes-128-cfb";
    public final static String CIPHER_AES_192_CFB = "aes-192-cfb";
    public final static String CIPHER_AES_256_CFB = "aes-256-cfb";
    public final static String CIPHER_AES_128_OFB = "aes-128-ofb";
    public final static String CIPHER_AES_192_OFB = "aes-192-ofb";
    public final static String CIPHER_AES_256_OFB = "aes-256-ofb";

    private final static int IV_LENGTH = 16;

    public AESCrypto(String name, String password) throws CryptoException {
        super(name, password);
    }

    @Override
    public int getIVLength() {
        return IV_LENGTH;
    }

    @Override
    public int getKeyLength() {
        if(mName.equals(CIPHER_AES_128_CFB) || mName.equals(CIPHER_AES_128_OFB)) {
            return 16;
        }
        else if (mName.equals(CIPHER_AES_192_CFB) || mName.equals(CIPHER_AES_192_OFB)) {
            return 24;
        }
        else if (mName.equals(CIPHER_AES_256_CFB) || mName.equals(CIPHER_AES_256_OFB)) {
            return 32;
        }
        return 0;
    }

    protected StreamBlockCipher getCipher(boolean isEncrypted) throws CryptoException
    {
        AESEngine engine = new AESEngine();
        StreamBlockCipher cipher;

        if (mName.equals(CIPHER_AES_128_CFB)) {
            cipher = new CFBBlockCipher(engine, getIVLength() * 8);
        }
        else if (mName.equals(CIPHER_AES_192_CFB)) {
            cipher = new CFBBlockCipher(engine, getIVLength() * 8);
        }
        else if (mName.equals(CIPHER_AES_256_CFB)) {
            cipher = new CFBBlockCipher(engine, getIVLength() * 8);
        }
        else if (mName.equals(CIPHER_AES_128_OFB)) {
            cipher = new OFBBlockCipher(engine, getIVLength() * 8);
        }
        else if (mName.equals(CIPHER_AES_192_OFB)) {
            cipher = new OFBBlockCipher(engine, getIVLength() * 8);
        }
        else if (mName.equals(CIPHER_AES_256_OFB)) {
            cipher = new OFBBlockCipher(engine, getIVLength() * 8);
        }
        else {
            throw new CryptoException("Invalid AlgorithmParameter: " + mName);
        }


        return cipher;
    }

    @Override
    protected StreamCipher createCipher(byte[] iv, boolean encrypt) throws CryptoException
    {
        StreamBlockCipher c = getCipher(encrypt);
        ParametersWithIV parameterIV = new ParametersWithIV(new KeyParameter(mKey), iv, 0, mIVLength);
        c.init(encrypt, parameterIV);
        return c;
    }

    @Override
    protected void process(byte[] in, ByteArrayOutputStream out, boolean encrypt){
        int size;
        byte[] buffer = new byte[in.length];
        StreamBlockCipher cipher;
        if (encrypt){
            cipher = (StreamBlockCipher)mEncryptCipher;
        }else{
            cipher = (StreamBlockCipher)mDecryptCipher;
        }
        size = cipher.processBytes(in, 0, in.length, buffer, 0);
        out.write(buffer, 0, size);
    }
}
