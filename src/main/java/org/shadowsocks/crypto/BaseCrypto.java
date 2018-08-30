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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Crypt base class implementation
 */
public abstract class BaseCrypto implements SSCrypto
{

    protected abstract StreamCipher createCipher(byte[] iv, boolean encrypt) throws CryptoException;
    protected abstract void process(byte[] in, ByteArrayOutputStream out, boolean encrypt);

    protected final String mName;
    protected final byte[] mKey;
    protected final int mIVLength;
    protected final int mKeyLength;

    protected StreamCipher mEncryptCipher = null;
    protected StreamCipher mDecryptCipher = null;

    private byte[] IV;

    // One SSCrypto could only do one decrypt/encrypt at the same time.
    protected ByteArrayOutputStream mData;

    private final byte [] mLock = new byte[0];

    public BaseCrypto(String name, String password) throws CryptoException
    {
        mName = name.toLowerCase();
        mIVLength = getIVLength();
        mKeyLength = getKeyLength();
        if (mKeyLength == 0) {
            throw new CryptoException("Unsupport method: " + mName);
        }
        mKey = Utils.getKey(password, mKeyLength, mIVLength);
        mData = new ByteArrayOutputStream();
    }

    public byte [] getKey(){
        return mKey;
    }

    public byte [] getIV(boolean encrypt){
        if(IV == null){
            IV = Utils.randomBytes(mIVLength);
        }
        return IV;
    }

    private byte [] encryptLocked(byte[] in) throws CryptoException
    {
        mData.reset();
        if (mEncryptCipher == null) {
            IV = getIV(true);
            mEncryptCipher = createCipher(IV, true);
            mDecryptCipher = createCipher(IV, false);
            try {
                //如果第一次加或解密，在密文前加上IV(Initial Vector)
                mData.write(IV);
            } catch (IOException e) {
                throw new CryptoException(e);
            }
        }
        process(in, mData, true);
        return mData.toByteArray();
    }

    @Override
    public byte [] encrypt(byte[] in, int length) throws CryptoException
    {
        synchronized(mLock) {
            if (length != in.length){
                byte[] data = new byte[length];
                System.arraycopy(in, 0, data, 0, length);
                return encryptLocked(data);
            }else{
                return encryptLocked(in);
            }
        }
    }

    private byte[] decryptLocked(byte[] in) throws CryptoException
    {
        byte[] data;
        mData.reset();
        //如果首次加解密，从密文的前mIVLength个 bytes取出IV
        if (mDecryptCipher == null) {
            IV = new byte[mIVLength];
            data = new byte[in.length - mIVLength];
            System.arraycopy(in, 0, IV, 0, mIVLength);

            System.arraycopy(in, mIVLength, data, 0, in.length - mIVLength);
            mDecryptCipher = createCipher(IV, false);
            mEncryptCipher = createCipher(IV, true);
        } else {
            data = in;
        }
        process(data, mData, false);
        return mData.toByteArray();
    }

    @Override
    public byte [] decrypt(byte[] in, int length) throws CryptoException
    {
        synchronized(mLock) {
            if (length != in.length) {
                byte[] data = new byte[length];
                System.arraycopy(in, 0, data, 0, length);
                return decryptLocked(data);
            }else{
                return decryptLocked(in);
            }
        }
    }
}
