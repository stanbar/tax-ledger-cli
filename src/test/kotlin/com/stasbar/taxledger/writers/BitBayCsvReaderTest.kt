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

package com.stasbar.taxledger.writers

import com.stasbar.taxledger.BitBay
import com.stasbar.taxledger.Constants.dateFormat
import com.stasbar.taxledger.OperationType
import com.stasbar.taxledger.models.Transaction
import org.junit.Before
import org.junit.Test
import java.io.File
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class BitBayCsvReaderTest {

    val oldBbHistoryCsvFile = File("build/libs/report.csv")
    // transactions -oldbb build/libs/report.csv -after 7.2017 -before 11.2017
    @Before
    fun setUp() {
    }

    @Test
    fun test_readCsv() {
        val result = BitBayCsvReader.readCsv(oldBbHistoryCsvFile)
        assertTrue { result.isNotEmpty() }

    }


    @Test
    fun test_isFiatLine() {
        assertTrue { BitBayCsvReader.isFiatLine("2017-09-19 07:58:59;2017-09-19 07:58:59;Zapłata za zakup waluty;-2,450.31 PLN;0.00 PLN") }
        assertFalse { BitBayCsvReader.isFiatLine("2017-09-19 07:58:59;2017-09-19 07:58:59;Zapłata za zakup waluty;-2,450.31 LTC;0.00 LTC") }
        assertFalse { BitBayCsvReader.isFiatLine("2017-09-19 07:58:59;2017-09-19 07:58:59;Zapłata za zakup waluty;-2,450.31 DASH;0.00 DASH") }
    }

    @Test
    fun test_parseOperationType() {
        assertEquals(parseOperationType("Zakup waluty"), OperationType.SELL)
        assertEquals(parseOperationType("Prowizja od transakcji"), OperationType.FEE)
        assertEquals(parseOperationType("Zapłata za zakup waluty"), OperationType.BUY)
        assertEquals(parseOperationType("Otrzymanie środków"), OperationType.DEPOSIT)
        assertEquals(parseOperationType("Wypłata środków na konto"), OperationType.WITHDRAW)
    }


    @Test
    fun toTransaction() {
        val operationLine = "2017-09-19 07:58:59;2017-09-19 07:58:59;Zapłata za zakup waluty;-2,450.31 PLN;0.00 PLN"
        val correntTransaction = Transaction(exchange = BitBay,
                time = dateFormat.parse("2017-09-19 07:58:59"),
                operationType = OperationType.BUY,
                bought = BigDecimal.ZERO,
                boughtCurrency = "",
                paid = BigDecimal("2450.31"),
                paidCurrency = "PLN")

        assertEquals(operationLine.toTransaction(), correntTransaction)
    }

}