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
                , getString(operationType.key)
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
            else -> Ansi.Color.DEFAULT
        }
        return ansi()
                .fg(exchange.color).a("%-11s".format("[${exchange.name}]")).reset()
                .fg(operationColor).a("%-6s".format("[${getString(operationType.key)}]")).reset()
                .a(" ${Constants.dateFormat.format(time)} ")
                .a("rate=%.8f%s".format(rate, paidCurrency)).reset()
                .fgBright(Ansi.Color.GREEN).a(" bought=$bought$boughtCurrency").reset()
                .fgBright(Ansi.Color.RED).a(" paid=$paid$paidCurrency").reset()
                .toString()
    }

}



