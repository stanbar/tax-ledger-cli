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

import com.google.gson.GsonBuilder
import com.stasbar.taxledger.Constants.DATE_FORMAT
import com.stasbar.taxledger.DEBUG
import com.stasbar.taxledger.ExchangeApi
import com.stasbar.taxledger.exchanges.bitbay.models.BitBayTransaction
import com.stasbar.taxledger.exchanges.bitbay.requests.TransactionsRequest
import com.stasbar.taxledger.models.Transaction
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface PrivateService {
    @FormUrlEncoded
    @POST("tradingApi.php")
    fun info(): Call<String>

    /**
     * history of account transactions
     *
     * @param market (optional): (e.g. BTC-USD) if set the result list would be limited to transactions with given currencies.
     * Correct format is cryptoCurrencyShortcut-priceCurrencyShortcut.
     * If parameter not set method returns all transactions ordered by date in descending order.
     */
    @FormUrlEncoded
    @POST("tradingApi.php")
    fun transactions(@FieldMap(encoded = true) fields: Map<String, String>): Call<List<BitBayTransaction>>
}

class BitBayApi(private val publicKey: String, private val privateKey: String) : ExchangeApi {

    private val URI = "https://bitbay.net/API/Trading/"

    private val service: Lazy<PrivateService> = lazy {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = if (DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val httpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(BitBayHeaderInterceptor(publicKey, privateKey))
                .addNetworkInterceptor(logInterceptor)
                .build()

        val gson = GsonBuilder().setDateFormat(DATE_FORMAT).create()
        val retrofit = Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(URI)
                .build()
        retrofit.create(PrivateService::class.java)
    }

    override fun transactions(): List<Transaction> {
        val request = TransactionsRequest(limit = "60")
        val transactionRequest = service.value.transactions(request.toMap())
        val response = transactionRequest.execute()
        return response.body()?.map { it.toTransaction() }?.toList() ?: ArrayList()
    }


}