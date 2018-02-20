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

package com.stasbar.taxledger.exchanges.bitbaynew

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.stasbar.taxledger.DEBUG
import com.stasbar.taxledger.ExchangeApi
import com.stasbar.taxledger.Logger
import com.stasbar.taxledger.exchanges.bitbaynew.models.BitBayHistory
import com.stasbar.taxledger.exchanges.bitbaynew.models.BitBayHistoryType
import com.stasbar.taxledger.exchanges.bitbaynew.models.BitBayTransaction
import com.stasbar.taxledger.exchanges.bitbaynew.responses.BitBayHistories
import com.stasbar.taxledger.exchanges.bitbaynew.responses.BitBayTransactions
import com.stasbar.taxledger.models.Credential
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.net.URLEncoder
import java.util.*

interface BitbayService {
    /**
     * history of account transactions
     *
     * @param query - zapytanie zawierające parametry filtorwania przesłane jako obiekt JSON z następującymi polami:
     * markets - Tablica marketów
     * rateFrom - Minimalna wartość kursu
     * rateTo - Maksymalna wartość kursu
     * userAction - Typ transakcji (Sell / Buy)
     */
    @GET("trading/history/transactions")
    fun transactions(@retrofit2.http.Query("query", encoded = true) query: String?): Call<JsonElement>


    @GET("balances/BITBAY/history")
    fun history(@retrofit2.http.Query("query", encoded = true) query: String?): Call<JsonElement>
}


class BitBayError(val errorsJsonArray: JsonArray) : Exception() {
    val errorToDescription = mapOf(
            Pair("PERMISSIONS_NOT_SUFFICIENT", "Uprawnienia nadane kluczowi API nie są wystarczające do wykonania akcji"),
            Pair("INVALID_HASH_SIGNATURE", "Wygenerowany podpis zapytania (API-Hash) jest niepoprawny"),
            Pair("ACTION_BLOCKED", "Akcja jest zablokowana na koncie użytkownika"),
            Pair("ACTION_LIMIT_EXCEEDED", "Limit wywołań akcji został wykorzystany, należy odczekać kilka minut przed kolejnym zapytaniem"),
            Pair("USER_OFFER_COUNT_LIMIT_EXCEEDED", "Limit ofert wystawionych do marketu dla danego rynku został wyczerpany"),
            Pair("MALFORMED_REQUEST", "JSON przesłany w zapytaniu jest uszkodzony"),
            Pair("INVALID_REQUEST", "Zapytanie zostało skonstruowane nieprawidłowo"),
            Pair("MARKET_CODE_CANNOT_BE_EMPTY", "Nie podano kodu marketu")
    )

    override val message: String?
        get() = toString()

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        errorsJsonArray.forEach { stringBuilder.appendln("${it.asString} : ${errorToDescription[it.asString]}") }
        return stringBuilder.toString()

    }

}

class BitBayApi(credentials: LinkedHashSet<Credential>, private val gson: Gson) : ExchangeApi<BitBayTransaction, BitBayHistory> {
    private val publicKey: String = credentials.first { it.name == "publicKey" }.value
    private val privateKey: String = credentials.first { it.name == "privateKey" }.value
    override val URL = "https://api.bitbay.net/rest/"

    override val service: Lazy<BitbayService> = lazy {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = if (DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val httpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(BitBayHeaderInterceptor(publicKey, privateKey))
                .addNetworkInterceptor(logInterceptor)
                .build()

        val retrofit = Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(URL)
                .build()
        retrofit.create(BitbayService::class.java)
    }

    override fun transactions(): List<BitBayTransaction> {
        val limit = 100
        val transactions = ArrayList<BitBayTransaction>()
        var nextPageCursor = "start"
        var previousPageCursor = "start"
        do {
            val queryMap = HashMap<String, Any?>()
            queryMap["limit"] = limit.toString()
            //queryMap["offset"] = offset.toString()
            queryMap["nextPageCursor"] = nextPageCursor
            queryMap["fromTime"] = null //(System.currentTimeMillis() / 1000).toString()
            queryMap["toTime"] = null
            queryMap["markets"] = emptyArray<String>()

            val query = gson.toJson(queryMap)

            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val response = service.value.transactions(encodedQuery).execute()

            if (response.isSuccessful) {
                val responseBody = response.body()
                try {
                    val transactionsResponse: BitBayTransactions? = gson.fromJson(responseBody, object : TypeToken<BitBayTransactions>() {}.type)
                    transactionsResponse?.let {
                        if (it.status == "Fail")
                            throw BitBayError(responseBody?.asJsonObject?.getAsJsonArray("errors")!!)

                        transactions.addAll(transactionsResponse.items)

                        previousPageCursor = nextPageCursor
                        nextPageCursor = transactionsResponse.nextPageCursor

                    }
                } catch (e: JsonSyntaxException) {
                    Logger.err(e.message)
                    Logger.err(responseBody.toString())
                    return emptyList()
                } catch (e: BitBayError) {
                    e.printStackTrace()
                    Logger.err(e.message)
                    return emptyList()
                }
            } else {
                Logger.err("Unsuccessfully fetched transactions error code: ${response.code()} body: ${response.errorBody()?.charStream()?.readText()} ")
                return emptyList()
            }
        } while (nextPageCursor != previousPageCursor)

        return transactions
    }


    override fun fees(): List<BitBayHistory> {
        return getHistory(listOf(
                BitBayHistoryType.TRANSACTION_COMMISSION_OUTCOME))
    }


    override fun depositsAndWithdraws(): List<BitBayHistory> {
        return getHistory(listOf(
                BitBayHistoryType.FUNDS_MIGRATION,
                BitBayHistoryType.ADD_FUNDS,
                BitBayHistoryType.WITHDRAWAL_SUBTRACT_FUNDS))
    }

    fun getHistory(types: List<BitBayHistoryType>): List<BitBayHistory> {
        var limit = 200
        var offset: Int? = null

        val transactions = ArrayList<BitBayHistory>()
        var newTransactions: List<BitBayHistory>
        var hasNextPages = false
        do {
            val queryMap = HashMap<String, Any?>()
            queryMap["limit"] = limit
            queryMap["offset"] = offset
            queryMap["types"] = types
            //Currently only PLN supported
            queryMap["balanceCurrencies"] = arrayOf("PLN")

            val query = gson.toJson(queryMap)

            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val response = service.value.history(encodedQuery).execute()
            if (response.isSuccessful) {
                val responseBody = response.body()
                try {
                    val transactionsResponse: BitBayHistories? = gson.fromJson(responseBody, object : TypeToken<BitBayHistories>() {}.type)
                    if (transactionsResponse != null) {

                        if (transactionsResponse.status == "Fail")
                            throw BitBayError(responseBody?.asJsonObject?.getAsJsonArray("errors")!!)
                        newTransactions = transactionsResponse.items
                                //Sorry BitBay, but I don't trust you
                                .filter { it.type in types.map { it.name } }
                        transactions.addAll(newTransactions)

                        hasNextPages = transactionsResponse.hasNextPage
                        limit = transactionsResponse.fetchedRows
                        offset = transactionsResponse.offset + transactionsResponse.fetchedRows
                    }

                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                    Logger.err(e.message)
                    Logger.err(responseBody.toString())

                } catch (e: BitBayError) {
                    e.printStackTrace()
                    Logger.err(e.message)

                }
            } else {
                Logger.err("Unsuccessfully fetched transactions error code: ${response.code()} body: ${response.errorBody()?.charStream()?.readText()} ")

            }
        } while (hasNextPages)

        return transactions
    }

}


