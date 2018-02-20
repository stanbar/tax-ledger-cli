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
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.stasbar.taxledger.DEBUG
import com.stasbar.taxledger.ExchangeApi
import com.stasbar.taxledger.Logger
import com.stasbar.taxledger.exchanges.bitfinex.models.BitfinexHistory
import com.stasbar.taxledger.exchanges.bitfinex.models.BitfinexTransaction
import com.stasbar.taxledger.exchanges.bitfinex.responses.BitfinexTradesReponse
import com.stasbar.taxledger.models.Credential
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

interface BitfinexService {

    @Headers("content-type: application/json")
    @POST("v2/auth/r/trades/t{SYMBOL}/hist")
    fun trades(@Path("SYMBOL") symbol: String, // REQUIRED Symbol (tBTCUSD, ...)
               @Query("start") start: Int? = null, //Millisecond start time
               @Query("end") end: Int? = null, // Millisecond end time
               @Query("limit") limit: Int? = null) //Number of records
            : Call<JsonElement>

    @Headers("content-type: application/json")
    @POST("v2/auth/r/trades/hist")
    fun trades(): Call<JsonElement>


}

class BitfinexApi(credentials: LinkedHashSet<Credential>, private val gson: Gson)
    : ExchangeApi<BitfinexTransaction, BitfinexHistory> {
    companion object {
        val URL = "https://api.bitfinex.com/"
    }

    private val publicKey: String = credentials.first { it.name == "key" }.value
    private val privateKey: String = credentials.first { it.name == "secretKey" }.value
    override val URL: String = BitfinexApi.URL

    override val service: Lazy<BitfinexService> = lazy {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = if (DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val httpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(BitfinexHeaderInterceptor(publicKey, privateKey))
                .addNetworkInterceptor(logInterceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(URL)
                .build()
        retrofit.create(BitfinexService::class.java)
    }

    override fun transactions(): List<BitfinexTransaction> {
        val transactions = arrayListOf<BitfinexTransaction>()
        val response = service.value.trades("BTCUSD").execute()
        if (response.isSuccessful) {
            val responseBody = response.body()
            println(responseBody?.asJsonObject?.asJsonArray)
            try {
                val transactionsResponse: BitfinexTradesReponse? = gson.fromJson(responseBody, object : TypeToken<BitfinexTradesReponse>() {}.type)
                if (transactionsResponse != null) {
                    transactions.addAll(transactionsResponse.items)
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


