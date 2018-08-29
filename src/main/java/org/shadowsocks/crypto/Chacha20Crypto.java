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

import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.ChaCha7539Engine;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.io.ByteArrayOutputStream;

/**
 * Chacha20 Crypt implementation
 */
public class Chacha20Crypto extends BaseCrypto {

    private final static String CIPHER_CHACHA20 = "chacha20";
    private final static String CIPHER_CHACHA20_IETF = "chacha20-ietf";

    private final static int IV_LENGTH = 8;
    private final static int IV_IETF_LENGTH = 12;

    private final static int KEY_LENGTH = 32;

    public Chacha20Crypto(String name, String password) throws CryptoException {
        super(name, password);
    }

    @Override
    public int getIVLength() {
        if (mName.equals(CIPHER_CHACHA20_IETF)) {
            return IV_IETF_LENGTH;
        } else {
            return IV_LENGTH;
        }
    }

    @Override
    public int getKeyLength() {
        if (mName.equals(CIPHER_CHACHA20) || mName.equals(CIPHER_CHACHA20_IETF)) {
            return KEY_LENGTH;
        }
        return 0;
    }

    @Override
    protected StreamCipher createCipher(byte[] iv, boolean encrypt) throws CryptoException
    {
        StreamCipher c;
        if (mName.equals(CIPHER_CHACHA20_IETF)) {
            c = new ChaCha7539Engine();
        } else {
            c = new ChaChaEngine();
        }
        ParametersWithIV parameterIV = new ParametersWithIV(new KeyParameter(mKey), iv, 0, mIVLength);
        c.init(encrypt, parameterIV);
        return c;
    }

    @Override
    protected void process(byte[] in, ByteArrayOutputStream out, boolean encrypt){
        int size;
        byte[] buffer = new byte[in.length];
        StreamCipher cipher;
        if (encrypt){
            cipher = mEncryptCipher;
        }else{
            cipher = mDecryptCipher;
        }
        size = cipher.processBytes(in, 0, in.length, buffer, 0);
        out.write(buffer, 0, size);
    }
}
