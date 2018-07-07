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
import com.stasbar.taxledger.options.TransactionsOptions
import com.stasbar.taxledger.writers.ConsoleWriter
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.test.assertEquals


internal class TestFifo {
    private val outContent = ByteArrayOutputStream()
    private val errContent = ByteArrayOutputStream()

    @Before
    fun setUpStreams() {
        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))
    }

    @After
    fun restoreStreams() {
        System.setOut(System.out)
        System.setErr(System.err)
    }

    @Test
    fun testFifoNovemberOnlyCryptoFiat() {

        // Collect transactions like we always did
        val rawTransactions = ArrayList<Transaction>()
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(6),
                bought = 0.07901089.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 2047.2480103544.toBigDecimal(),
                paidCurrency = "PLN"))

        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(6),
                bought = 0.05801814.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 1503.5308151976.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(6),
                bought = 0.02873944.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 744.8828882456.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(6),
                bought = 0.08013902.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 2075.2864730416.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(6),
                bought = 0.04361441.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 1129.0519015433.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.37539022.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 10000.02007058.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(17),
                bought = 12735.119511.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.46393878.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(17),
                bought = 5490.2.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.2.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(19),
                bought = 556.93374233.toBigDecimal(),
                boughtCurrency = "LSK",
                paid = 18156.039999958.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(21),
                bought = 0.33688183.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 10000.00024172.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(22),
                bought = 2.67509999.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 5727.38907859.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(22),
                bought = 0.04112357.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 88.04556337.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(22),
                bought = 0.15839317.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 339.11977697.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(22),
                bought = 0.05284705.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 113.14553405.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(22),
                bought = 0.94046179.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 2013.52869239.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(22),
                bought = 1.13207443.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 2423.77135463.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(23),
                bought = 400.toBigDecimal(),
                boughtCurrency = "LSK",
                paid = 11880.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 0.64129306.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 1398.0188708.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 1.87535064.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 4088.2643952.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 0.14087162.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 307.1001316.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 0.13646674.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 297.4974932.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 0.00051135.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 1.114743.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 0.06292622.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 137.1791596.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(27),
                bought = 0.08526386.toBigDecimal(),
                boughtCurrency = "DASH",
                paid = 185.8752148.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(27),
                bought = 7440.68835.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.2221101.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(27),
                bought = 4900.00011.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.14626866.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(27),
                bought = 33500.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 1.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(27),
                bought = 450.00014.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.01343284.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(27),
                bought = 2845.600215.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.08494329.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(27),
                bought = 3417.658945.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.10201967.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(28),
                bought = 0.00134272.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 46.6448709248.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(28),
                bought = 0.02358727.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 819.4217598.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(28),
                bought = 0.11899639.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 4133.9333986361.toBigDecimal(),
                paidCurrency = "PLN"))

        //simple as y = f(x)
        val transactions = recalculateWithFifo(rawTransactions)

        ConsoleWriter(transactions).run {
            printTransactions(TransactionsOptions())
            printSummary()
        }

        printLeftCurrencies(rawTransactions)



        assertEquals(outContent.toString(),
                " ─────────────────────────────────────────────────────────────────────────────────────────────────────── \n" +
                        "   Giełda   Typ         Data                Kurs                 Kupiłem               Zapłaciłem        \n" +
                        " ─────────────────────────────────────────────────────────────────────────────────────────────────────── \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN          0.07901089 BTC     2047.2480103544 PLN  \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN          0.05801814 BTC     1503.5308151976 PLN  \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN          0.02873944 BTC      744.8828882456 PLN  \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN          0.08013902 BTC     2075.2864730416 PLN  \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN          0.04361441 BTC     1129.0519015433 PLN  \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN          0.17441688 BTC  4646.2912253572576 PLN  \n" +
                        "                                                                                                502      \n" +
                        " BitBay     Sell 2018-04-17 13:02:03              0 BTC        12735.119511 PLN          0.46393878 BTC  \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN                 0.2 BTC      10000.02007058 PLN  \n" +
                        " BitBay     Sell 2018-04-17 13:02:03              0 BTC              5490.2 PLN                 0.2 BTC  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN          0.00097334 BTC      10000.02007058 PLN  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN          0.22113676 BTC  6564.2235586704080 PLN  \n" +
                        "                                                                                                248      \n" +
                        " BitBay     Sell 2018-04-27 13:02:03              0 BTC          7440.68835 PLN           0.2221101 BTC  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN          0.11574507 BTC      10000.00024172 PLN  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN          0.00134272 BTC       46.6448709248 PLN  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN          0.02358727 BTC         819.4217598 PLN  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN           0.0055936 BTC  194.32161628498587 PLN  \n" +
                        "                                                                                               5567      \n" +
                        " BitBay     Sell 2018-04-27 13:02:03              0 BTC          4900.00011 PLN          0.14626866 BTC  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN          0.11340279 BTC     4133.9333986361 PLN  \n" +
                        " BitBay     Sell 2018-04-27 13:02:03              0 BTC               33500 PLN                   1 BTC  \n" +
                        " BitBay     Sell 2018-04-27 13:02:03              0 BTC           450.00014 PLN          0.01343284 BTC  \n" +
                        " BitBay     Sell 2018-04-27 13:02:03              0 BTC         2845.600215 PLN          0.08494329 BTC  \n" +
                        " BitBay     Sell 2018-04-27 13:02:03              0 BTC         3417.658945 PLN          0.10201967 BTC  \n" +
                        " ─────────────────────────────────────────────────────────────────────────────────────────────────────── \n" +
                        "Łącznie 22 operacji\n" +
                        "┌──────────────────────────────────────────────────────────────────────────────┐\n" +
                        "│                                 Podsumowanie                                 │\n" +
                        "├──────────────────────────┬─────────────────────────┬─────────────────────────┤\n" +
                        "│ Przychód (Brutto)        │ Koszty (w tym prowizje) │ Dochód (Netto)          │\n" +
                        "├──────────────────────────┼─────────────────────────┼─────────────────────────┤\n" +
                        "│ 70779.26                 │ 53904.87 (0)            │ 16874.39                │\n" +
                        "└──────────────────────────┴─────────────────────────┴─────────────────────────┘\n" +
                        "┌───────────────────────────────────────┬──────────────────────────────────────┐\n" +
                        "│ Wpłata                                │ Wypłata                              │\n" +
                        "├───────────────────────────────────────┼──────────────────────────────────────┤\n" +
                        "│ 0                                     │ 0                                    │\n" +
                        "└───────────────────────────────────────┴──────────────────────────────────────┘\n" +
                        "Left with:\n" +
                        "BTC -> 0E-8\n" +
                        "LSK -> 956.93374233\n" +
                        "DASH -> 7.94268349\n")
    }

    @Test
    fun testFifoJanuaryAllTypes() {

        // Collect transactions like we always did
        val rawTransactions = ArrayList<Transaction>()
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(10),
                bought = 13.4807062413.toBigDecimal(),
                boughtCurrency = "PLN",
                paid = 0.00271477.toBigDecimal(),
                paidCurrency = "ETH"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.00020833.toBigDecimal(),
                boughtCurrency = "ETH",
                paid = 0.9999964998.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.00020887.toBigDecimal(),
                boughtCurrency = "ETH",
                paid = 0.9999818346.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.00020887.toBigDecimal(),
                boughtCurrency = "ETH",
                paid = 0.9999818346.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.00020887.toBigDecimal(),
                boughtCurrency = "ETH",
                paid = 0.9999818346.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.00020887.toBigDecimal(),
                boughtCurrency = "ETH",
                paid = 0.9999818346.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.00020887.toBigDecimal(),
                boughtCurrency = "ETH",
                paid = 0.9999818346.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.00020887.toBigDecimal(),
                boughtCurrency = "ETH",
                paid = 0.9999818346.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.00020887.toBigDecimal(),
                boughtCurrency = "ETH",
                paid = 0.9999818346.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.00020887.toBigDecimal(),
                boughtCurrency = "ETH",
                paid = 0.9999818346.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.00020887.toBigDecimal(),
                boughtCurrency = "ETH",
                paid = 0.9999818346.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.00020887.toBigDecimal(),
                boughtCurrency = "ETH",
                paid = 0.9999818346.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.00020887.toBigDecimal(),
                boughtCurrency = "ETH",
                paid = 0.9999818346.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(10),
                bought = 0.00020887.toBigDecimal(),
                boughtCurrency = "ETH",
                paid = 0.9999818346.toBigDecimal(),
                paidCurrency = "PLN"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(3),
                bought = 0.00583711325.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 0.23348453.toBigDecimal(),
                paidCurrency = "XMR"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(3),
                bought = 0.03302415575.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 1.32096623.toBigDecimal(),
                paidCurrency = "XMR"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(3),
                bought = 0.01634368225.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 0.65374729.toBigDecimal(),
                paidCurrency = "XMR"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(3),
                bought = 0.0248876605.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 0.99550642.toBigDecimal(),
                paidCurrency = "XMR"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(3),
                bought = 0.0051278.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 0.205112.toBigDecimal(),
                paidCurrency = "XMR"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(3),
                bought = 0.00092047.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 0.0368188.toBigDecimal(),
                paidCurrency = "XMR"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(3),
                bought = 0.03576167.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 1.4304668.toBigDecimal(),
                paidCurrency = "XMR"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.SELL,
                time = day(3),
                bought = 0.00092936.toBigDecimal(),
                boughtCurrency = "BTC",
                paid = 0.0371744.toBigDecimal(),
                paidCurrency = "XMR"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(2),
                bought = 0.17196162.toBigDecimal(),
                boughtCurrency = "LTC",
                paid = 0.00309530916.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(2),
                bought = 4.974.toBigDecimal(),
                boughtCurrency = "LTC",
                paid = 0.089532.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(2),
                bought = 11.toBigDecimal(),
                boughtCurrency = "LTC",
                paid = 0.198.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(2),
                bought = 0.53013139.toBigDecimal(),
                boughtCurrency = "LTC",
                paid = 0.00954236502.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(2),
                bought = 0.25782559.toBigDecimal(),
                boughtCurrency = "LTC",
                paid = 0.00464086062.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(2),
                bought = 0.57517591.toBigDecimal(),
                boughtCurrency = "LTC",
                paid = 0.01035316638.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(2),
                bought = 0.03359605.toBigDecimal(),
                boughtCurrency = "LTC",
                paid = 0.0006047289.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(2),
                bought = 0.28.toBigDecimal(),
                boughtCurrency = "LTC",
                paid = 0.00504.toBigDecimal(),
                paidCurrency = "BTC"))
        rawTransactions.add(Transaction(BitBay.name,
                operationType = OperationType.BUY,
                time = day(2),
                bought = 0.2.toBigDecimal(),
                boughtCurrency = "LTC",
                paid = 0.0036.toBigDecimal(),
                paidCurrency = "BTC"))

        //simple as y = f(x)
        val transactions = recalculateWithFifo(rawTransactions)

        ConsoleWriter(transactions).run {
            printTransactions(TransactionsOptions())
            printSummary()
        }

        printLeftCurrencies(rawTransactions)



        assertEquals(outContent.toString(),
                " ─────────────────────────────────────────────────────────────────────────────────────────────────────── \n" +
                        "   Giełda   Typ         Data                Kurs                 Kupiłem               Zapłaciłem        \n" +
                        " ─────────────────────────────────────────────────────────────────────────────────────────────────────── \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN          0.07901089 BTC     2047.2480103544 PLN  \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN          0.05801814 BTC     1503.5308151976 PLN  \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN          0.02873944 BTC      744.8828882456 PLN  \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN          0.08013902 BTC     2075.2864730416 PLN  \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN          0.04361441 BTC     1129.0519015433 PLN  \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN          0.17441688 BTC  4646.2912253572576 PLN  \n" +
                        "                                                                                                502      \n" +
                        " BitBay     Sell 2018-04-17 13:02:03              0 BTC        12735.119511 PLN          0.46393878 BTC  \n" +
                        " BitBay     Buy  2018-04-17 13:02:03              0 PLN                 0.2 BTC      10000.02007058 PLN  \n" +
                        " BitBay     Sell 2018-04-17 13:02:03              0 BTC              5490.2 PLN                 0.2 BTC  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN          0.00097334 BTC      10000.02007058 PLN  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN          0.22113676 BTC  6564.2235586704080 PLN  \n" +
                        "                                                                                                248      \n" +
                        " BitBay     Sell 2018-04-27 13:02:03              0 BTC          7440.68835 PLN           0.2221101 BTC  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN          0.11574507 BTC      10000.00024172 PLN  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN          0.00134272 BTC       46.6448709248 PLN  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN          0.02358727 BTC         819.4217598 PLN  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN           0.0055936 BTC  194.32161628498587 PLN  \n" +
                        "                                                                                               5567      \n" +
                        " BitBay     Sell 2018-04-27 13:02:03              0 BTC          4900.00011 PLN          0.14626866 BTC  \n" +
                        " BitBay     Buy  2018-04-27 13:02:03              0 PLN          0.11340279 BTC     4133.9333986361 PLN  \n" +
                        " BitBay     Sell 2018-04-27 13:02:03              0 BTC               33500 PLN                   1 BTC  \n" +
                        " BitBay     Sell 2018-04-27 13:02:03              0 BTC           450.00014 PLN          0.01343284 BTC  \n" +
                        " BitBay     Sell 2018-04-27 13:02:03              0 BTC         2845.600215 PLN          0.08494329 BTC  \n" +
                        " BitBay     Sell 2018-04-27 13:02:03              0 BTC         3417.658945 PLN          0.10201967 BTC  \n" +
                        " ─────────────────────────────────────────────────────────────────────────────────────────────────────── \n" +
                        "Łącznie 22 operacji\n" +
                        "┌──────────────────────────────────────────────────────────────────────────────┐\n" +
                        "│                                 Podsumowanie                                 │\n" +
                        "├──────────────────────────┬─────────────────────────┬─────────────────────────┤\n" +
                        "│ Przychód (Brutto)        │ Koszty (w tym prowizje) │ Dochód (Netto)          │\n" +
                        "├──────────────────────────┼─────────────────────────┼─────────────────────────┤\n" +
                        "│ 70779.26                 │ 53904.87 (0)            │ 16874.39                │\n" +
                        "└──────────────────────────┴─────────────────────────┴─────────────────────────┘\n" +
                        "┌───────────────────────────────────────┬──────────────────────────────────────┐\n" +
                        "│ Wpłata                                │ Wypłata                              │\n" +
                        "├───────────────────────────────────────┼──────────────────────────────────────┤\n" +
                        "│ 0                                     │ 0                                    │\n" +
                        "└───────────────────────────────────────┴──────────────────────────────────────┘\n" +
                        "Left with:\n" +
                        "BTC -> 0E-8\n" +
                        "LSK -> 956.93374233\n" +
                        "DASH -> 7.94268349\n")
    }

    private fun recalculateWithFifo(rawTransactions: ArrayList<Transaction>)
            : ArrayList<Transaction> {
        rawTransactions.sortBy { it.time } // sort by time, just in case we mix our transactions from different exchanges
        val sells = toSellQueue(rawTransactions)
        val buys = toBuysMapQueue(rawTransactions)
        val transactions = ArrayList<Transaction>()
        while (sells.isNotEmpty()) {
            val income = sells.pop()
            val currency = income.paidCurrency
            var remainingBuyAmount = income.paid // Amount of sold currency, that we need to find out the buy source now

            while (remainingBuyAmount > BigDecimal.ZERO
                    && buys[currency] != null
                    && buys[currency]!!.isNotEmpty()) {
                // Searching from the oldest buys of this currency

                val buy = buys[currency]!!.pop() // Take the first (oldest) one

                // Calculate how much will I take from this transaction (may be all or part of, depends of what was smaller)
                val consumeAmount = remainingBuyAmount.min(buy.bought)
                // And enter this amount as expenses
                val paid = consumeAmount/buy.bought * buy.paid
                transactions.add(Transaction(exchangeName = buy.exchangeName,
                        id = buy.id, time = income.time,
                        operationType = buy.operationType,
                        bought = consumeAmount,
                        boughtCurrency = buy.boughtCurrency,
                        paid = paid,
                        paidCurrency = buy.paidCurrency,
                        rate = buy.rate))

                // Subtract remaining amount
                remainingBuyAmount -= consumeAmount
                // And amount that was took from this buy operation
                buy.bought -= consumeAmount

                // If that sell operation was less than the buy operation we push it back to the queue
                if (buy.bought > BigDecimal.ZERO) buys[currency]!!.addFirst(buy)

            }
            // After finding all the expenses we add the income operation (sell)
            transactions.add(income)
        }

        return transactions
    }

    private fun toBuysMapQueue(rawTransactions: ArrayList<Transaction>): HashMap<String, ArrayDeque<Transaction>> {
        val buys = HashMap<String, ArrayDeque<Transaction>>()

        //init buys map with empty queues for each currency
        rawTransactions
                .filter { it.operationType == OperationType.BUY } //Take only buy operations
                .distinctBy { it.boughtCurrency } // distinct by currency
                .forEach { buys[it.boughtCurrency] = ArrayDeque() } //init buys map to empty queue


        //Fill buys queue
        rawTransactions.filter { it.operationType == OperationType.BUY }
                .forEach { buys[it.boughtCurrency]!!.add(it) }
        return buys
    }

    private fun toSellQueue(rawTransactions: ArrayList<Transaction>): ArrayDeque<Transaction> {
        val sells = ArrayDeque<Transaction>()
        //Fill sells queue
        rawTransactions.filter { it.operationType == OperationType.SELL }
                .forEach { sells.add(it) }
        return sells
    }

    private fun printLeftCurrencies(rawTransactions: ArrayList<Transaction>) {
        val buys = toBuysMapQueue(rawTransactions)
        println("Left with:")
        buys.forEach {
            var left = BigDecimal.ZERO
            it.value.forEach {
                left += it.bought
            }
            println("${it.key} -> $left")
        }
    }


    private fun day(dayArg: Int) = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, dayArg)
        set(Calendar.HOUR, 1)
        set(Calendar.MINUTE, 2)
        set(Calendar.SECOND, 3)
    }.time!!
}
