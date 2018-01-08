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
import com.stasbar.taxledger.Misc
import com.stasbar.taxledger.Misc.donateMap
import com.stasbar.taxledger.getString
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.options.TransactionsOptions
import com.stasbar.taxledger.translations.Text
import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.asciitable.CWC_FixedWidth
import de.vandermeer.asciithemes.TA_GridThemes
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole

object ConsoleWriter : OutputWriter() {
    fun printIntro() {
        AnsiConsole.out.println(Misc.taxledger)
    }

    fun printTransactions(transactions: Collection<Transaction>, options: TransactionsOptions) {
        val filteredTransactions = transactions
                .filter { options.showNonFiat || it.isFiatTransaction() }
                .filter { options.showNonEssential || it.operationType in essentialOperation }
        if (filteredTransactions.isEmpty()) {
            Logger.err(getString(Text.NO_OPERATIONS))
            return
        }

        val at = AsciiTable()

        at.addRule()

        at.addRow(getString(Text.EXCHANGE), getString(Text.TYPE), getString(Text.DATE), null, getString(Text.RATE), null, getString(Text.GET), null, getString(Text.PAID))
                .setTextAlignment(TextAlignment.CENTER)
        at.addRule()

        filteredTransactions.forEach { it.addRowTo(at) }
        at.addRule()
        at.renderer.cwc = CWC_FixedWidth().add(10).add(4).add(19).add(14).add(4).add(18).add(4).add(18).add(4)
        at.context.setGridTheme(TA_GridThemes.HORIZONTAL)
        AnsiConsole.out.println(at.render())

        AnsiConsole.out.println(getString(Text.TOTAL_OPERATIONS).format(transactions.size))
    }

    private fun Transaction.addRowTo(at: AsciiTable) {
        val row = at.addRow(toList())

        row.cells[3].context.textAlignment = TextAlignment.RIGHT
        row.cells[5].context.textAlignment = TextAlignment.RIGHT
        row.cells[7].context.textAlignment = TextAlignment.RIGHT
    }


    fun printSummary(transactions: Collection<Transaction>) {
        val grossIncome = getGrossIncome(transactions)
        val expense = getExpense(transactions)
        val fees = getFees(transactions)
        val expenseWithFees = expense + fees
        val netIncome = grossIncome - expenseWithFees
        val atSummary = AsciiTable()

        atSummary.addRule()
        atSummary.addRow(null, null, getString(Text.Summary.SUMMARY)).setTextAlignment(TextAlignment.CENTER)
        atSummary.addRule()
        atSummary.addRow(getString(Text.Summary.GROSS_INCOME), getString(Text.Summary.EXPENSE_WITH_FEE), getString(Text.Summary.NET_INCOME))
        atSummary.addRule()
        atSummary.addRow(grossIncome.stripTrailingZeros().toPlainString()
                , "%s (%s)".format(expenseWithFees.stripTrailingZeros().toPlainString(), fees.stripTrailingZeros().toPlainString())
                , netIncome.stripTrailingZeros().toPlainString())
        atSummary.addRule()
        atSummary.setPaddingLeftRight(1)
        AnsiConsole.out.println(atSummary.render())

        printDepositsAndWithdraws(transactions)

    }

    private fun printDepositsAndWithdraws(transactions: Collection<Transaction>) {
        val withdraws = getWithdraws(transactions)
        val deposits = getDeposits(transactions)
        val at = AsciiTable()
        at.addRule()
        at.addRow(getString(Text.DEPOSIT), getString(Text.WITHDRAW))
        at.addRule()
        at.addRow(deposits.stripTrailingZeros().toPlainString()
                , withdraws.stripTrailingZeros().toPlainString())
        at.addRule()
        at.setPaddingLeftRight(1)
        AnsiConsole.out.println(at.render())

    }


    fun printDonate() {
        donateMap.forEach { t, u -> AnsiConsole.out.println(Ansi.ansi().fg(u.first).bgDefault().a("$t -> ${u.second}").reset()) }
    }

    fun printExitMessage() {
        AnsiConsole.out.println(Misc.contact)
        Logger.info(getString(Text.DONATE))

        printDonate()
    }


}