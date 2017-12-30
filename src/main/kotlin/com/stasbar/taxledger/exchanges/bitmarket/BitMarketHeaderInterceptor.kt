package com.stasbar.taxledger.exchanges.bitmarket
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

class BitMarketHeaderInterceptor(private val publicKey: String, private val privateKey: String) : Interceptor {
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