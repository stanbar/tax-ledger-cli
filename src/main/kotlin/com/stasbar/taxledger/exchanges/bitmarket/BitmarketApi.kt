package com.stasbar.taxledger.exchanges.bitmarket

import com.google.gson.GsonBuilder
import com.stasbar.taxledger.Constants.DATE_FORMAT
import com.stasbar.taxledger.ExchangeApi
import com.stasbar.taxledger.exchanges.bitmarket.models.BitMarketTransaction
import com.stasbar.taxledger.exchanges.bitmarket.requests.TransactionsRequest
import com.stasbar.taxledger.models.Transaction
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BitmarketService {
    @FormUrlEncoded
    @POST()
    fun info(): Call<String>

    @FormUrlEncoded
    @POST("api2")
    fun transactions(@FieldMap(encoded = true) fields: Map<String, String>): Call<BitMarketTransaction>
}

class BitmarketApi(private val publicKey: String, private val privateKey: String) : ExchangeApi {

    private val URI = "https://www.bitmarket.pl/"

    private val service: Lazy<BitmarketService> = lazy {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = if (true) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val httpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(BitMarketHeaderInterceptor(publicKey, privateKey))
                .addNetworkInterceptor(logInterceptor)
                .build()

        val gson = GsonBuilder().setDateFormat(DATE_FORMAT).create()
        val retrofit = Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(URI)
                .build()
        retrofit.create(BitmarketService::class.java)
    }

    override fun transactions(): List<Transaction> {
        val request = TransactionsRequest()
        val transactionRequest = service.value.transactions(request.toMap())
        val response = transactionRequest.execute()
        println(response.raw())
        println(response.body().toString())
        return response.body()!!.results.map { it.toTransaction() } ?: ArrayList()
    }


}

