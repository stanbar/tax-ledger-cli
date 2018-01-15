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
import com.stasbar.taxledger.Logger
import com.stasbar.taxledger.OperationType
import com.stasbar.taxledger.models.Transaction
import org.jline.utils.InputStreamReader
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.math.BigDecimal

fun String.toTransaction(): Transaction {
    val values = split(";")

    val operationName = values[2]

    val operationType = parseOperationType(operationName)

    return when (operationType) {
        OperationType.BUY -> toBuyTransaction()
        OperationType.SELL -> toSellTransaction()
        OperationType.FEE -> toBuyTransaction()
        OperationType.WITHDRAW -> toBuyTransaction()
        OperationType.DEPOSIT -> toSellTransaction()
        else -> throw IllegalStateException("BitBay operation type $operationType is not supported \n whole line: $this")
    }
}

fun parseOperationType(string: String) = when (string) {
    "Zakup waluty" -> OperationType.SELL
    "Prowizja od transakcji" -> OperationType.FEE
    "Zapłata za zakup waluty" -> OperationType.BUY
    "Otrzymanie środków" -> OperationType.DEPOSIT
    "Wypłata środków na konto" -> OperationType.WITHDRAW
    else -> throw IllegalArgumentException("Could not map $string to OperationType")
}

private fun String.toBuyTransaction(): Transaction {
    val values = split(";")
    val operationDate = dateFormat.parse(values[0])

    val operationType = parseOperationType(values[2])

    val amount = values[3]
            .replace(",", "") // Remove kilo separator
            .split(" ")[0] // split amount from currency
            .toBigDecimal()
    val currency = values[3].split(" ")[1]

    return Transaction(exchange = BitBay
            , time = operationDate
            , operationType = operationType
            , bought = BigDecimal.ZERO
            , boughtCurrency = ""
            , paid = amount.abs()
            , paidCurrency = currency.toUpperCase()
            , rate = BigDecimal.ZERO)
}

private fun String.toSellTransaction(): Transaction {
    val values = split(";")
    val operationDate = dateFormat.parse(values[0])
    val operationType = parseOperationType(values[2])
    val amount = values[3]
            .replace(",", "") // Remove kilo separator
            .split(" ")[0] // split amount from currency
            .toBigDecimal()
    val currency = values[3].split(" ")[1]
    return Transaction(exchange = BitBay
            , time = operationDate
            , operationType = operationType
            , bought = amount.abs()
            , boughtCurrency = currency.toUpperCase()
            , paid = BigDecimal.ZERO
            , paidCurrency = ""
            , rate = BigDecimal.ZERO)
}

object BitBayCsvReader {

    fun readCsv(file: File): Collection<Transaction> {
        var transactions: List<Transaction> = emptyList()
        try {
            BufferedReader(InputStreamReader(file.inputStream(), Charsets.UTF_8)).use {
                val lines = it.readLines()
                if (lines.isEmpty())
                    Logger.err("The file: $file is empty")
                else
                    transactions = lines
                            .filterIndexed { index, line ->
                                index != 0 //Skip the header
                                        && line.isNotBlank()
                                        && isFiatLine(line)
                            }
                            .map { it.toTransaction() }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Logger.err(e.message)
        }
        Logger.info("Parsed ${transactions.size} from old.bitbay.net .csv history file")
        return transactions
    }

    fun isFiatLine(line: String) = line.contains("PLN")
}