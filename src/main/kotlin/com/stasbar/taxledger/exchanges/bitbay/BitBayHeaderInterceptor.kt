/*
 * Copyright (c) 2017 Stanislaw stasbar Baranski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *          __             __
 *    _____/ /_____ ______/ /_  ____ ______
 *   / ___/ __/ __ `/ ___/ __ \/ __ `/ ___/
 *  (__  ) /_/ /_/ (__  ) /_/ / /_/ / /
 * /____/\__/\__,_/____/_.___/\__,_/_/
 *            taxledger@stasbar.com
 */

package com.stasbar.taxledger.exchanges.bitbay

import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.GeneralSecurityException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

open class BitBayHeaderInterceptor(private val publicKey: String, private val privateKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val postBodyString = bodyToString(request.body())
        val newRequest = request.newBuilder()
                .addHeader("API-Key", publicKey)
                .addHeader("API-Hash", computeSignature(postBodyString, privateKey))
                .build()
        return chain.proceed(newRequest)
    }
    private fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else
                return ""
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        }

    }

    @Throws(GeneralSecurityException::class, UnsupportedEncodingException::class)
    private fun computeSignature(baseString: String, keyString: String): String {
        val ALGORITHM = "HmacSHA512"
        val mac = Mac.getInstance(ALGORITHM)
        val secret = SecretKeySpec(keyString.toByteArray(), ALGORITHM)
        mac.init(secret)
        val digest = mac.doFinal(baseString.toByteArray())
        val hash = BigInteger(1, digest)
        var hmac = hash.toString(16)

        if (hmac.length % 2 != 0) {
            hmac = "0" + hmac
        }

        return hmac
    }

}