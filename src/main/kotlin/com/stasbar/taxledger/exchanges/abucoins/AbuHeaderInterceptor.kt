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

package com.stasbar.taxledger.exchanges.abucoins

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

open class AbuHeaderInterceptor(private val accessKey: String, private val passphrase: String, private val secret: String,private val  baseUri: String) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val timestampInSeconds = System.currentTimeMillis() / 1000
        val message = buildMessage(chain.request(), timestampInSeconds)
        val newRequest = request.newBuilder()
                .addHeader("AC-ACCESS-KEY", accessKey)
                .addHeader("AC-ACCESS-TIMESTAMP", timestampInSeconds.toString())
                .addHeader("AC-ACCESS-PASSPHRASE", passphrase)
                .addHeader("AC-ACCESS-SIGN", hash256(message))
                .build()
        return chain.proceed(newRequest)
    }

    private fun buildMessage(request: Request, timestamp: Long): String {
        val method = request.method()
        val requestPath = request.url().toString().substring(baseUri.length)

        val body = if (request.body() != null) Gson().toJson(request.body()) else ""

        return "$timestamp$method$requestPath$body"
    }

    val ALGORITHM: String = "HmacSHA256"
    private fun hash256(baseString: String): String {

        val mac = Mac.getInstance(ALGORITHM)
        val secret = SecretKeySpec(Base64.getDecoder().decode(secret), ALGORITHM)
        mac.init(secret)
        return Base64.getEncoder().encodeToString(mac.doFinal(baseString.toByteArray()))
    }

}