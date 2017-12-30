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