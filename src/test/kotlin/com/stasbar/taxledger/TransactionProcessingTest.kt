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

import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.writers.ConsoleWriter
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.test.assertEquals

internal class TransactionProcessingTest {

    val transactions: MutableList<Transaction> = ArrayList(1000)
    val fiatCurrencies = arrayOf("PLN", "EUR", "USD")
    val cryptoCurrencies = arrayOf("BTC", "LTC", "BCC", "LSK")
    val currencies = fiatCurrencies + cryptoCurrencies

    var totalBought: MutableMap<String, BigDecimal> = HashMap<String, BigDecimal>().apply {
        for (currency in currencies) {
            put(currency, BigDecimal.ZERO)
        }
    }
    var totalSold: MutableMap<String, BigDecimal> = HashMap<String, BigDecimal>().apply {
        for (currency in currencies) {
            put(currency, BigDecimal.ZERO)
        }
    }
    var totalFees: MutableMap<String, BigDecimal> = HashMap<String, BigDecimal>().apply {
        for (currency in currencies) {
            put(currency, BigDecimal.ZERO)
        }
    }
    var totalDeposit: MutableMap<String, BigDecimal> = HashMap<String, BigDecimal>().apply {
        for (currency in currencies) {
            put(currency, BigDecimal.ZERO)
        }
    }
    var totalWithdraw: MutableMap<String, BigDecimal> = HashMap<String, BigDecimal>().apply {
        for (currency in currencies) {
            put(currency, BigDecimal.ZERO)
        }
    }

    lateinit var consoleWriter: ConsoleWriter

    @Before
    internal fun setUp() {
        val calendar = Calendar.getInstance()
        calendar.set(2016, 1, 1)
        val rand = ThreadLocalRandom.current()
        val operationTypes = arrayOf(OperationType.SELL, OperationType.BUY, OperationType.FEE,
                OperationType.WITHDRAW, OperationType.DEPOSIT)
        val exchanges = arrayOf(BitBay, Abucoins)

        repeat(1000) {

            calendar.add(Calendar.DAY_OF_YEAR, rand.nextInt(0, 3))
            val operationType = OperationType.values().random(operationTypes.size)
            val bought: Double = if (operationType == OperationType.FEE) 0.0 else rand.nextDouble(0.00001, 100.0)
            val transaction = Transaction(
                    exchangeName = exchanges.random().name,
                    time = (calendar.clone() as Calendar).time,
                    operationType = operationType,
                    bought = bought.toBigDecimal(),
                    boughtCurrency = currencies.random(),
                    paid = rand.nextDouble(0.00001, 100.0).toBigDecimal(),
                    paidCurrency = currencies.random(),
                    rate = rand.nextDouble(0.00001, 10000.0).toBigDecimal()
            )
            transactions.add(transaction)

            when (operationType) {
                OperationType.BUY -> totalBought[transaction.paidCurrency] = totalBought[transaction.paidCurrency]!!.add(transaction.paid)
                OperationType.SELL -> totalSold[transaction.boughtCurrency] = totalSold[transaction.boughtCurrency]!!.add(transaction.bought)
                OperationType.FEE -> totalFees[transaction.paidCurrency] = totalFees[transaction.paidCurrency]!!.add(transaction.paid)
                OperationType.WITHDRAW -> totalWithdraw[transaction.paidCurrency] = totalWithdraw[transaction.paidCurrency]!!.add(transaction.paid)
                OperationType.DEPOSIT -> totalDeposit[transaction.boughtCurrency] = totalDeposit[transaction.boughtCurrency]!!.add(transaction.bought)
                else -> {
                    throw IllegalStateException()
                }
            }
        }
        consoleWriter = ConsoleWriter(transactions)
    }

    @Test
    fun test_outputWriter() {
        assertEquals(consoleWriter.getFees("PLN"), totalFees["PLN"]!!.setScale(2, RoundingMode.DOWN))
        assertEquals(consoleWriter.getFees("EUR"), totalFees["EUR"]!!.setScale(2, RoundingMode.DOWN))
        assertEquals(consoleWriter.getFees("BTC"), totalFees["BTC"]!!.setScale(2, RoundingMode.DOWN))

        assertEquals(consoleWriter.getWithdraws("PLN"), totalWithdraw["PLN"]!!.setScale(2, RoundingMode.DOWN))
        assertEquals(consoleWriter.getWithdraws("EUR"), totalWithdraw["EUR"]!!.setScale(2, RoundingMode.DOWN))
        assertEquals(consoleWriter.getWithdraws("BTC"), totalWithdraw["BTC"]!!.setScale(2, RoundingMode.DOWN))

        assertEquals(consoleWriter.getDeposits("BTC"), totalDeposit["BTC"]!!.setScale(2, RoundingMode.DOWN))


        assertEquals(consoleWriter.getGrossIncome("PLN"), totalSold["PLN"]!!.setScale(2, RoundingMode.DOWN))
        assertEquals(consoleWriter.getExpense("PLN"), totalBought["PLN"]!!.setScale(2, RoundingMode.DOWN))

        assertEquals(consoleWriter.getNetIncome("PLN"),
                totalSold["PLN"]!!.setScale(2, RoundingMode.DOWN)
                        - totalBought["PLN"]!!.setScale(2, RoundingMode.DOWN)
                        - totalFees["PLN"]!!.setScale(2, RoundingMode.DOWN))

    }


    fun <T> Array<T>.random() = get(ThreadLocalRandom.current().nextInt(0, size))
    fun <T> Array<T>.random(bound: Int) = get(ThreadLocalRandom.current().nextInt(0, bound))
}