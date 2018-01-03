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

package com.stasbar.taxledger.models

import com.stasbar.taxledger.*
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.ansi
import java.math.BigDecimal
import java.util.*

interface Transactionable {
    fun toTransaction(): Transaction
    fun operationType(): OperationType
}

data class Transaction(val exchange: Exchange<ExchangeApi>,
                       var id: String = "",
                       var time: Date = Date(),
                       var operationType: OperationType,
                       var bought: BigDecimal = BigDecimal.ZERO,
                       var boughtCurrency: String = "",
                       var paid: BigDecimal = BigDecimal.ZERO,
                       var paidCurrency: String = "",
                       var rate: BigDecimal = BigDecimal.ZERO) {

    fun toList(): List<String> {
        return listOf(exchange.name
                , getString(operationType.key).substring(0, Math.min(getString(operationType.key).length, 4))
                , Constants.dateFormat.format(time)
                , rate.stripTrailingZeros().toPlainString()
                , paidCurrency
                , bought.stripTrailingZeros().toPlainString()
                , boughtCurrency
                , paid.stripTrailingZeros().toPlainString()
                , paidCurrency)
    }

    override fun toString(): String {

        val operationColor = when (operationType) {
            OperationType.BUY -> Ansi.Color.MAGENTA
            OperationType.SELL -> Ansi.Color.YELLOW
            OperationType.FEE -> Ansi.Color.RED
            OperationType.DEPOSIT -> Ansi.Color.WHITE
            OperationType.WITHDRAW -> Ansi.Color.WHITE
            else -> Ansi.Color.WHITE
        }
        return ansi()
                .fg(exchange.color).a("%-11s".format("[${exchange.name}]")).reset()
                .fg(operationColor).a("%-6s".format("[$operationType]")).reset()
                .a(" ${Constants.dateFormat.format(time)} ")
                .a("rate=%.8f%s".format(rate, paidCurrency)).reset()
                .fgBright(Ansi.Color.GREEN).a(" bought=$bought$boughtCurrency").reset()
                .fgBright(Ansi.Color.RED).a(" paid=$paid$paidCurrency").reset()
                .toString()
    }

    fun isFiatTransaction(): Boolean {
        return "PLN" in arrayOf(paidCurrency.toUpperCase(), boughtCurrency.toUpperCase())
    }

}



