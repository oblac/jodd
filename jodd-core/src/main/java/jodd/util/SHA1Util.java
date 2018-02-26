// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.
package jodd.util;

import jodd.exception.SignatureCannotBeNullException;

import java.util.Arrays;

/**
 * SHA1Util.
 * @author zhangxin
 */
public class SHA1Util {

    private SHA1Util() {
    }

    /**
     * SHA1 encrypts function, which determines whether the incoming parameters are sorted in the dictionary order
     * according to the incoming values, and then encrypted using the SHA1 encryption algorithm.
     *
     * @param str the content will be encrypt
     * @param flag the content need sort by dictionary order or not
     * @return the encrypt result
     */
    public static String encode(String[] str, boolean flag) {
        if (flag) {
            // 字典序排序
            Arrays.sort(str);
        }
        StringBuilder bigStr = new StringBuilder();
        for (String message:str) {
            bigStr.append(message);
        }
        // SHA1加密
        return SignUtil.encode("SHA1", bigStr.toString()).toLowerCase();
    }

    /**
     * SHA1 encryption will determine whether the incoming parameters are sorted in the dictionary order or not,
     * and then encrypted using the SHA1 encryption algorithm. And then compare with signature, if the signature is null
     * or empty string, this method will throws SignatureCannotBeNullException.
     *
     * @param str the character string will be encrypt
     * @param flag the content need sort by dictionary order or not
     * @param signature the signature message
     * @return compare result
     */
    public static boolean encode(String[] str, boolean flag, String signature) throws SignatureCannotBeNullException {
        if (signature == null||"".equals(signature)) {
            throw new SignatureCannotBeNullException("The signature mustn't be null or none string");
        }
        return encode(str, flag).equals(signature);
    }


}
