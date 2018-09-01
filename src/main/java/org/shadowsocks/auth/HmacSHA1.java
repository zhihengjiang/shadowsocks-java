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
package org.shadowsocks.auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class HmacSHA1 extends SSAuth{

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    public static final int AUTH_LEN = 10;

    @Override

    public byte[] doAuth(byte[] key, byte [] data) throws AuthException
    {
        try{
            SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte [] original_result;
            byte [] result = new byte[AUTH_LEN];
            original_result = mac.doFinal(data);
            System.arraycopy(original_result, 0, result, 0, AUTH_LEN);
            return result;
        }catch(NoSuchAlgorithmException | InvalidKeyException e){
            throw new AuthException(e);
        }
    }

    @Override
    public boolean doAuth(byte[] key, byte [] data, byte [] expect) throws AuthException
    {
        return Arrays.equals(expect, doAuth(key, data));
    }
}
