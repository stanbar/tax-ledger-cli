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
import de.vandermeer.asciitable.AsciiTable
import de.vandermeer.asciitable.CWC_FixedWidth
import de.vandermeer.asciithemes.TA_GridThemes
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment
import org.fusesource.jansi.Ansi
import java.math.BigDecimal
import java.util.*

object Printer {
    fun printIntro() {
        println(Misc.taxledger)
    }
    fun printTransactions(transactions: Collection<Transaction>) {
        val at = AsciiTable()
        at.addRule()

        at.addRow(getString(Text.EXCHANGE), getString(Text.TYPE), getString(Text.DATE), null, getString(Text.RATE), null, getString(Text.GET), null, getString(Text.PAID))
                .setTextAlignment(TextAlignment.CENTER)
        at.addRule()
        transactions.forEach { it.addRowTo(at)}
        at.addRule()
        at.renderer.cwc = CWC_FixedWidth().add(10).add(4).add(19).add(14).add(4).add(18).add(4).add(18).add(4)
        at.context.setGridTheme(TA_GridThemes.HORIZONTAL)
        println(at.render())

        println("Total: ${transactions.size}")
    }

    private fun Transaction.addRowTo(at : AsciiTable) {
        val row = at.addRow(toList())

        row.cells[3].context.textAlignment = TextAlignment.RIGHT
        row.cells[5].context.textAlignment = TextAlignment.RIGHT
        row.cells[7].context.textAlignment = TextAlignment.RIGHT
    }



    fun printSummary(transactions: Collection<Transaction>) {
        var grossIncome: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.boughtCurrency.toUpperCase() == "PLN" }.forEach { grossIncome += it.bought }
        var expense: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.paidCurrency.toUpperCase() == "PLN" }.forEach { expense += it.paid }
        val netIncome = grossIncome - expense


        val at = AsciiTable()

        at.addRule()
        at.addRow(null, null, getString(Text.Summary.SUMMARY)).setTextAlignment(TextAlignment.CENTER)
        at.addRule()
        at.addRow(getString(Text.Summary.GROSS_INCOME),getString(Text.Summary.EXPENSE),getString(Text.Summary.NET_INCOME))
        at.addRule()
        at.addRow(grossIncome.stripTrailingZeros().toPlainString()
                , expense.stripTrailingZeros().toPlainString()
                , netIncome.stripTrailingZeros().toPlainString())
        at.addRule()
        at.setPaddingLeftRight(1)
        println(at.render())
    }



    fun printDonate() {
        val donateMap = LinkedHashMap<String, Pair<Ansi.Color, String>>()
        donateMap.put("Bitcoin", Pair(Ansi.Color.YELLOW, "3QgRku1UkFbyVyBbdLFxfxKmgFwDvT5feP"))
        donateMap.put("IOTA", Pair(Ansi.Color.BLUE, "LCWOKWWQWJZJJJQXRASFVTFMHJRSCGYFCPSZ9ZEEYQFJXADLKCD9AZ9XVYQBBFDJALKQESHP9EATSPYNALOHVVBSH9"))
        donateMap.put("Etherium", Pair(Ansi.Color.BLUE, "0x03Ba2f3907fcA09867C7A1F4f218D7B5eA052997"))
        donateMap.put("Cardano", Pair(Ansi.Color.YELLOW, "DdzFFzCqrhtAWA4ftZxRvE8ixhybRcdo77hhVDTkphtSAvmbJwCwozGYg89ADTaGgNpRZDzV21t3d94Uq9M6vj9GUZp2cvBpgKsXWQWY"))
        donateMap.put("Bitcoin Cash", Pair(Ansi.Color.YELLOW, "1LTfvN44Lmf6eqHn9B1bAnJV4rbLQKpduK"))
        donateMap.put("Litecoin", Pair(Ansi.Color.WHITE, "M88GxayRm4YQ78KuLJ9tYuAYESrd9uPEAg"))
        donateMap.put("Dash", Pair(Ansi.Color.BLUE, "XmnmWK2iN73X9qqcE4NMX4VTVVC2YjrCMS"))
        donateMap.put("NEO", Pair(Ansi.Color.GREEN, "ANSHr2MMLM4YDFCpriRiE3WjnxMYcXteX3"))
        donateMap.forEach { t, u -> println(Ansi.ansi().fg(u.first).bgDefault().a("$t -> ${u.second}").reset()) }
    }

    fun printExitMessage() {
        println(Misc.contact)
        Logger.info(getString(Text.DONATE))

        printDonate()
    }



}