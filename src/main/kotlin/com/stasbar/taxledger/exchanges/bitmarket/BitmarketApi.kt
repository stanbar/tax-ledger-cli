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

package com.stasbar.taxledger.exchanges.bitmarket

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.stasbar.taxledger.DEBUG
import com.stasbar.taxledger.ExchangeApi
import com.stasbar.taxledger.Logger
import com.stasbar.taxledger.exchanges.bitmarket.models.BitmarketTransaction
import com.stasbar.taxledger.exchanges.bitmarket.requests.BitmarketHistoryRequest
import com.stasbar.taxledger.exchanges.bitmarket.requests.TransactionsRequest
import com.stasbar.taxledger.exchanges.bitmarket.responses.BitmarketResponse
import com.stasbar.taxledger.models.Transactionable
import com.stasbar.taxledger.silentTry
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * https://github.com/bitmarket-net/api
 */
interface BitmarketService {
    @FormUrlEncoded
    @POST()
    fun info(): Call<String>

    @FormUrlEncoded
    @POST("api2/")
    fun transactions(@FieldMap(encoded = true) fields: Map<String, String>): Call<JsonElement>

    @FormUrlEncoded
    @POST("api2/")
    fun history(@FieldMap(encoded = true) fields: Map<String, String>): Call<JsonElement>
}

class BitmarketApi(private val publicKey: String, private val privateKey: String, val gson: Gson) : ExchangeApi<Transactionable, Transactionable> {
    override val URL = "https://www.bitmarket.pl/"

    override val service: Lazy<BitmarketService> = lazy {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = if (DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val httpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(BitMarketHeaderInterceptor(publicKey, privateKey))
                .addNetworkInterceptor(logInterceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(URL)
                .build()
        retrofit.create(BitmarketService::class.java)
    }

    override fun transactions(): List<Transactionable> {
        //TODO pagination

        val request = TransactionsRequest("LTCPLN")
        val transactions = arrayListOf<BitmarketTransaction>()
        val response = service.value.transactions(request.toMap()).execute()
        if (response.isSuccessful) {
            val responseBody = response.body()
            try {
                silentTry {
                    println(responseBody?.asJsonObject)
                    println(responseBody?.asJsonArray)
                }

                val transactionsResponse: BitmarketResponse? = gson.fromJson(responseBody, object : TypeToken<BitmarketResponse>() {}.type)
                if (transactionsResponse != null) {
                    transactions.addAll(transactionsResponse.data.results)
                }
            } catch (e: JsonSyntaxException) {
                Logger.err(e.message)
                Logger.err(responseBody.toString())
                return emptyList()
            }
        } else {
            Logger.err("Unsuccessfully fetched transactions error code: ${response.code()} body: ${response.errorBody()?.charStream()?.readText()} ")
            return emptyList()
        }
        return transactions

    }

    override fun depositsAndWithdraws(): List<Transactionable> {

        val request = BitmarketHistoryRequest("PLN")
        val transactions = arrayListOf<BitmarketTransaction>()
        val response = service.value.transactions(request.toMap()).execute()
        if (response.isSuccessful) {
            val responseBody = response.body()
            try {
                silentTry {
                    println(responseBody?.asJsonObject)
                    println(responseBody?.asJsonArray)
                }

                val transactionsResponse: BitmarketResponse? = gson.fromJson(responseBody, object : TypeToken<BitmarketResponse>() {}.type)
                if (transactionsResponse != null) {
                    transactions.addAll(transactionsResponse.data.results)
                }
            } catch (e: JsonSyntaxException) {
                Logger.err(e.message)
                Logger.err(responseBody.toString())
                return emptyList()
            }
        } else {
            Logger.err("Unsuccessfully fetched transactions error code: ${response.code()} body: ${response.errorBody()?.charStream()?.readText()} ")
            return emptyList()
        }
        return transactions
    }

}





