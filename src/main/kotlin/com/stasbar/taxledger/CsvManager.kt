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

package com.stasbar.taxledger

import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.translations.Text
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.Writer
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

object CsvManager {

    fun saveToFile(transactions: Collection<Transaction>, fileName: String = fileName()) {
        try {
            val file = File(fileName)
            file.parentFile.mkdirs()
            FileWriter(file).use {
                writeLine(it, Arrays.asList(getString(Text.EXCHANGE), getString(Text.TYPE), getString(Text.DATE), "", getString(Text.RATE), "", getString(Text.GET), "", getString(Text.PAID)))
                transactions.forEach { transaction -> writeLine(it, transaction.toList()) }
                it.flush()
                printSummary(it, transactions)
                it.flush()

                Logger.info(getString(Text.TRANSACTIONS_SAVED).format(file.absoluteFile))
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

    private fun fileName() = "transactions/transactions_${SimpleDateFormat("dd_MM_yyyy_hhmmss").format(Date(System.currentTimeMillis()))}.csv"
    fun fileName(arguments: String) = "transactions/transactions${arguments}_${SimpleDateFormat("dd_MM_yyyy_hhmmss").format(Date(System.currentTimeMillis()))}.csv"


    private val DEFAULT_SEPARATOR = ','

    @Throws(IOException::class)
    private fun writeLine(w: Writer, values: List<String>, separatorsParam: Char = DEFAULT_SEPARATOR, customQuote: Char = ' ') {
        var separator = separatorsParam

        if (separator == ' ') separator = DEFAULT_SEPARATOR

        val sb = StringBuilder()
        var first = true
        for (value in values) {
            if (!first) {
                sb.append(separator)
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value))
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote)
            }

            first = false
        }
        sb.append("\n")
        w.append(sb.toString())


    }

    //https://tools.ietf.org/html/rfc4180
    private fun followCVSformat(value: String): String {

        var result = value
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"")
        }
        return result

    }

    fun printSummary(writer: FileWriter, transactions: Collection<Transaction>) {
        var grossIncome: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.boughtCurrency.toUpperCase() == "PLN" }.forEach { grossIncome += it.bought }
        var expense: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.paidCurrency.toUpperCase() == "PLN" }.forEach { expense += it.paid }
        val netIncome = grossIncome - expense

        writeLine(writer, listOf(getString(Text.Summary.SUMMARY)))
        writeLine(writer, listOf(getString(Text.Summary.GROSS_INCOME), getString(Text.Summary.EXPENSE), getString(Text.Summary.NET_INCOME)))
        writeLine(writer, listOf(grossIncome.stripTrailingZeros().toPlainString()
                , expense.stripTrailingZeros().toPlainString()
                , netIncome.stripTrailingZeros().toPlainString()))
    }


}
