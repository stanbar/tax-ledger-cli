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
import org.junit.Test
import java.math.BigDecimal
import java.util.*

class NonNullMap<K, out V>(private val map: Map<K, V>) : Map<K, V> by map {
    override operator fun get(key: K): V {
        return map[key]!!
    }
}

internal class TestFifo {

    @Test
    fun testFifo() {
        val sells = ArrayDeque<Transaction>()
        /**
         * Tworze kolejki dla kazdej waluty zakupowanej, ja tutaj wpisuje z palca, ale
         * mógłbym to zrobić uprzednio zliczając wszystkie wystąpienia walut kupowanych
         */
        val buys = NonNullMap<String, ArrayDeque<Transaction>>(
                mapOf("BTC" to ArrayDeque(),
                        "DASH" to ArrayDeque(),
                        "LSK" to ArrayDeque()))

        buys["BTC"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(6),
                bought = 0.07901089.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 2047.2480103544.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["BTC"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(6),
                bought = 0.05801814.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 1503.5308151976.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["BTC"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(6),
                bought = 0.02873944.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 744.8828882456.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["BTC"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(6),
                bought = 0.08013902.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 2075.2864730416.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["BTC"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(6),
                bought = 0.04361441.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 1129.0519015433.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["BTC"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.37539022.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 10000.02007058.toBigDecimal(),
                paidCurrency = "PLN"))
        sells.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(17),
                bought = 12735.119511.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.46393878.toBigDecimal(),
                paidCurrency = "BTC"))
        sells.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(17),
                bought = 5490.2.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.2.toBigDecimal(),
                paidCurrency = "BTC"))
        buys["LSK"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(19),
                bought = 556.93374233.toBigDecimal(),
                boughtCurrency = "LSK",
                paid = 18156.039999958.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["BTC"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(21),
                bought = 0.33688183.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 10000.00024172.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["DASH"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(22),
                bought = 2.67509999.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 5727.38907859.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["DASH"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(22),
                bought = 0.04112357.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 88.04556337.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["DASH"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(22),
                bought = 0.15839317.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 339.11977697.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["DASH"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(22),
                bought = 0.05284705.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 113.14553405.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["DASH"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(22),
                bought = 0.94046179.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 2013.52869239.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["DASH"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(22),
                bought = 1.13207443.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 2423.77135463.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["LSK"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(23),
                bought = 400.toBigDecimal(),
                boughtCurrency = "LSK",
                paid = 11880.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["DASH"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 0.64129306.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 1398.0188708.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["DASH"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 1.87535064.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 4088.2643952.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["DASH"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 0.14087162.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 307.1001316.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["DASH"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 0.13646674.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 297.4974932.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["DASH"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 0.00051135.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 1.114743.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["DASH"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 0.06292622.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 137.1791596.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["DASH"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 0.08526386.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 185.8752148.toBigDecimal(),
                paidCurrency = "PLN"))
        sells.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(27),
                bought = 7440.68835.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.2221101.toBigDecimal(),
                paidCurrency = "BTC"))
        sells.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(27),
                bought = 4900.00011.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.14626866.toBigDecimal(),
                paidCurrency = "BTC"))
        sells.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(27),
                bought = 33500.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 1.toBigDecimal(),
                paidCurrency = "BTC"))
        sells.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(27),
                bought = 450.00014.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.01343284.toBigDecimal(),
                paidCurrency = "BTC"))
        sells.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(27),
                bought = 2845.600215.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.08494329.toBigDecimal(),
                paidCurrency = "BTC"))
        sells.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(27),
                bought = 3417.658945.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.10201967.toBigDecimal(),
                paidCurrency = "BTC"))
        buys["BTC"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(28),
                bought = 0.00134272.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 46.6448709248.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["BTC"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(28),
                bought = 0.02358727.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 819.4217598.toBigDecimal(),
                paidCurrency = "PLN"))
        buys["BTC"].add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(28),
                bought = 0.11899639.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 4133.9333986361.toBigDecimal(),
                paidCurrency = "PLN"))


        val transactions = generateTransactions(sells, buys)
        val outputWriter = ConsoleWriter(transactions)
        outputWriter.printSummary()
        println("Oraz zosatło mi:")
        buys.forEach {
            var left = BigDecimal.ZERO
            it.value.forEach {
                left += it.bought
            }
            println("${it.key} -> $left")
        }
    }

    private fun generateTransactions(sells: ArrayDeque<Transaction>, buys: NonNullMap<String, ArrayDeque<Transaction>>): ArrayList<Transaction> {
        val transactions = ArrayList<Transaction>()
        while (sells.isNotEmpty()) {
            val income = sells.pop()
            val currency = income.paidCurrency
            var remainingBuyAmount = income.paid // Ilość sprzedanego krypto której musimy znaleść źródło zakupu

            while (remainingBuyAmount > BigDecimal.ZERO && buys[currency].isNotEmpty()) {
                //Szukamy od najstarszych dowodów zakupów tej kryptowaluty

                val buy = buys[currency].pop() // Bierzemy pierwszą znalezioną

                // Obliczam ile pobiore z tej transakcji
                val consumeAmount = remainingBuyAmount.min(buy.bought) //if (remainingBuyAmount > buy.bought) buy.bought else remainingBuyAmount
                // I wpisujemy ją w koszta zmniejszając licznik pozostałych potrzebnych źródeł tego zakupu
                transactions.add(Transaction(exchangeName = buy.exchangeName,
                        id = buy.id, time = income.time,
                        operationType = buy.operationType,
                        bought = consumeAmount,
                        boughtCurrency = buy.boughtCurrency,
                        paid = buy.paid,
                        paidCurrency = buy.paidCurrency,
                        rate = buy.rate))

                remainingBuyAmount -= consumeAmount
                buy.bought -= consumeAmount

                if (buy.bought > BigDecimal.ZERO) {
                    //Może zostać uzyte ponownie jako koszt wieć pushuje na początek kolejki
                    buys[currency].addFirst(buy)
                }


            }
            //Po znalezieniu wszystkich kosztów(kupnach) dodaje przychód(sprzedaż)
            transactions.add(income)
        }

        return transactions
    }

    private fun day(dayArg: Int) = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, dayArg) }.time!!
}

