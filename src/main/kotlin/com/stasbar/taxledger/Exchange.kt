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

package com.stasbar.taxledger

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.stasbar.taxledger.exceptions.ApiNotSetException
import com.stasbar.taxledger.exceptions.TooManyCredentialsException
import com.stasbar.taxledger.exchanges.abucoins.AbuApi
import com.stasbar.taxledger.exchanges.bitbaynew.BitBayApi
import com.stasbar.taxledger.exchanges.bitmarket.BitmarketApi
import com.stasbar.taxledger.exchanges.coinroom.CoinroomApi
import com.stasbar.taxledger.models.Credential
import com.stasbar.taxledger.models.Transactionable
import org.fusesource.jansi.Ansi
import java.io.PrintWriter

object BitBay : Exchange<BitBayApi>(BitBayApi::class.java, "BitBay", "bb",
        linkedSetOf(Credential("publicKey", 36), Credential("privateKey", 36)), Ansi.Color.BLUE,
        setOf("--onlybb", "--bbonly"))


object Abucoins : Exchange<AbuApi>(AbuApi::class.java, "Abucoins", "abu",
        linkedSetOf(Credential("passphrase", 8),
                Credential("key", 41),
                Credential("secret", 64))
        , Ansi.Color.GREEN, setOf("--onlyabu", "--abuonly"))

object BitMarket : Exchange<BitmarketApi>(BitmarketApi::class.java, "Bitmarket", "bm",
        linkedSetOf(Credential("publicKey", 32), Credential("privateKey", 32)), Ansi.Color.GREEN,
        setOf("--onlybitmarket", "--bitmarketonly", "--onlybm", "--bmonly"))


object Coinroom : Exchange<CoinroomApi>(CoinroomApi::class.java, "Coinroom", "cr",
        linkedSetOf(Credential("publicKey", 36), Credential("privateKey", 36)), Ansi.Color.YELLOW,
        setOf("--onlycoinroom", "--coinroomonly", "--onlycr", "--cronly"))


abstract class Exchange<ApiType : ExchangeApi<Transactionable, Transactionable>>(private val klass: Class<ApiType>,
                                                                                 val name: String,
                                                                                 private val shortcut: String,
                                                                                 val credentials: LinkedHashSet<Credential>,
                                                                                 val color: Ansi.Color, val filters: Set<String>) {

    val gson = GsonBuilder().setDateFormat(Constants.DATE_FORMAT).create()
    var apiHolder: ApiType? = null

    @Throws(ApiNotSetException::class)
    fun getApi(): ApiType {
        if (isNotSet())
            throw ApiNotSetException(name)
        else if (apiHolder == null)
            apiHolder = klass.getConstructor(LinkedHashSet::class.java, Gson::class.java).newInstance(credentials, gson)
        return apiHolder!!
    }

    fun addCredential(newCredential: Credential) {
        credentials.add(newCredential)
    }


    @Throws(TooManyCredentialsException::class)
    fun enforceNotFull() {
        if (credentials.none { it.value.isEmpty() })
            throw TooManyCredentialsException(name)
    }


    fun isSet() = credentials.all { it.value.isNotBlank() }

    private fun isNotSet() = !isSet()

    fun isNameOf(candidate: String) = candidate.toLowerCase() in arrayOf(name.toLowerCase(), shortcut.toLowerCase())

    fun printCredentials(writer: PrintWriter) {
        writer.println(name.toLowerCase())
        credentials.forEach { writer.println(it.value) }
        writer.flush()
    }


}