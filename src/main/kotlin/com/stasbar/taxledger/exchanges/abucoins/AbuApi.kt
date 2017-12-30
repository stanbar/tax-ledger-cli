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

import com.google.gson.GsonBuilder
import com.stasbar.taxledger.DEBUG
import com.stasbar.taxledger.ExchangeApi
import com.stasbar.taxledger.exchanges.abucoins.models.*
import com.stasbar.taxledger.models.Transaction
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface AbuService {
    /**
     * Return current server time.
     */
    @GET("/time")
    fun time(): Call<TimeResponse>


    /**
     * Get a list of trading accounts.
     */
    @GET("/accounts")
    fun accounts(): Call<List<Account>>

    /**
     * Information for a single account. Use this endpoint when you know the account_id.
     */
    @GET("/accounts/{id}")
    fun account(@Path("id") id: String): Call<Account>


    /**
     * Get a list of your payment methods.
     */
    @GET("/payment-methods")
    fun paymentMethods(): Call<List<PaymentMethod>>

    /**
     * List your current orders.
     * @param status [open, done]	Limit list of orders to these statuses
     * @param product_id ex. [ETH-BTC]	Get orders for the specified market
     */
    @GET("/orders")
    fun orders(@Query("status") status: String?, @Query("product_id") product_id: String?): Call<List<Order>>

    /**
     * Get a list of recent fills.
     * @param trade_id	identifier of the last trade
     * @param product_id	product identifier
     * @param price	trade price
     * @param size:	trade size
     * @param order_id	Identifier of order
     * @param created_at	time in UTC
     * @param liquidity	indicates if the fill was the result of a liquidity provider or liquidity taker. M indicates Maker and T indicates Taker.
     * @param side	user side(buy or sell)
     */
    @GET("/fills")
    fun fills(): Call<List<Fill>>



}

class AbuApi(private val accessKey: String, private val secretKey: String, private val passphrase: String) : ExchangeApi {
    companion object {
        const val URI = "https://api.abucoins.com"
        val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    }


    val service: Lazy<AbuService> = lazy {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = if (DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val httpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(AbuHeaderInterceptor(accessKey, passphrase, secretKey, URI))
                .addNetworkInterceptor(logInterceptor)
                .build()

        val gson = GsonBuilder().setDateFormat(DATE_FORMAT).create()
        val retrofit = Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(URI)
                .build()
        retrofit.create(AbuService::class.java)
    }

    override fun transactions(): List<Transaction> {
        val response = service.value.fills().execute()
        return response.body()
                ?.map { it.toTransaction() }
                ?.toList() ?: ArrayList()
    }



}

