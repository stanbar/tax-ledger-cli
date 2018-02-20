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

import com.stasbar.taxledger.Logger
import com.stasbar.taxledger.PreferencesManager
import com.stasbar.taxledger.getString
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.options.TransactionsOptions
import com.stasbar.taxledger.translations.Text
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.*

class CsvWriter(transactions: Collection<Transaction>) : OutputWriter(transactions) {

    fun saveToFile(options: TransactionsOptions) {
        try {
            val file = File(PreferencesManager.transactionsDir, fileName(options.fileName.toString()))
            FileWriter(file).use {
                transactions
                        .filter { options.showNonFiat || it.isFiatTransaction() }
                        .filter { options.showNonEssential || it.operationType in essentialOperation }

                val filteredTransactions = transactions
                        .filter { options.showNonFiat || it.isFiatTransaction() }
                        .filter { options.showNonEssential || it.operationType in essentialOperation }

                if (filteredTransactions.isEmpty()) {
                    Logger.err(getString(Text.NO_OPERATIONS))
                    return
                }
                writeLine(it, headRow)
                filteredTransactions.forEach { transaction -> writeLine(it, transaction.toList()) }
                it.flush()
                printSummary(it, transactions)
                it.flush()

                Logger.info(getString(Text.TRANSACTIONS_SAVED).format(file.absoluteFile))
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

    fun fileName(arguments: String) = "transactions${arguments}_${SimpleDateFormat("dd_MM_yyyy_hhmmss").format(Date(System.currentTimeMillis()))}.csv"


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
        val grossIncome = getGrossIncome()
        val expense = getExpense()
        val fees = getFees()
        val expenseWithFees = expense + fees
        val netIncome = getNetIncome("PLN")
        val withdraws = getWithdraws()
        val deposits = getDeposits()

        writeLine(writer, listOf(getString(Text.Summary.SUMMARY)))
        writeLine(writer, listOf(getString(Text.Summary.GROSS_INCOME), getString(Text.Summary.EXPENSE_WITH_FEE), getString(Text.Summary.NET_INCOME)))
        writeLine(writer, listOf(grossIncome.stripTrailingZeros().toPlainString()
                , expenseWithFees.stripTrailingZeros().toPlainString()
                , netIncome.stripTrailingZeros().toPlainString()))

        writeLine(writer, listOf(getString(Text.DEPOSIT), getString(Text.WITHDRAW)))
        writeLine(writer, listOf(deposits.stripTrailingZeros().toPlainString()
                , withdraws.stripTrailingZeros().toPlainString()))
    }


}
