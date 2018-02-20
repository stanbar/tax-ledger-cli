/*
 * Copyright (c) 2018 Stanislaw stasbar Baranski
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

package com.stasbar.taxledger.exchanges.bitfinex

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.GeneralSecurityException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class BitfinexHeaderInterceptor(val publicKey: String, val privateKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val nonce = System.currentTimeMillis()

        val newRequest = request.newBuilder()
                .addHeader("bfx-nonce", nonce.toString())
                .addHeader("bfx-apikey", publicKey)
                .addHeader("bfx-signature",
                        computeSignature(
                                buildMessage(request, nonce), privateKey))
                .build()

        return chain.proceed(newRequest)
    }

    private fun buildMessage(request: Request, nonce: Long): String {
        val apiPath = request.url().toString().substring(BitfinexApi.URL.length)
        val rawBody = Gson().toJson(JsonObject())
        val message = "/api/$apiPath$nonce$rawBody"
        return message
    }

    @Throws(GeneralSecurityException::class, UnsupportedEncodingException::class)
    private fun computeSignature(baseString: String, keyString: String): String {
        val ALGORITHM = "HmacSHA384"
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