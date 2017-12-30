package com.stasbar.taxledger.exchanges.bitbay.models

import com.google.gson.annotations.SerializedName
import com.stasbar.taxledger.BitBay
import com.stasbar.taxledger.OperationType
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.models.Transactionable
import java.math.BigDecimal
import java.util.*

data class BitBayTransaction(
        val date: Date, //date of transaction
        @SerializedName("type") val operationType: String, //ASK if you sold crypto, BID when you were buyer
        val market: String, //pair of currencies of transaction
        val amount: BigDecimal, //amount of crypto sold/bought
        val rate: BigDecimal, //crypto rate used in specified trasnaction
        val price: BigDecimal) // total price paid in transaction
    : Transactionable {
    override fun operationType(): OperationType = when (operationType) {
        "BID" -> OperationType.BUY
        "ASK" -> OperationType.SELL
        else -> throw IllegalStateException("Unknown operation type $operationType")

    }


    override fun toTransaction(): Transaction {
        return if(operationType() == OperationType.BUY) toBuyTransaction() else toSellTransaction()
    }

    private fun toBuyTransaction(): Transaction {
        return Transaction(exchange = BitBay
                , time = date
                , operationType = operationType()
                , bought = amount
                , boughtCurrency = market.split("-")[0]
                , paid = price
                , paidCurrency = market.split("-")[1]
                , rate = rate)
    }

    private fun toSellTransaction(): Transaction {
        return Transaction(exchange = BitBay
                , time = date
                , operationType = operationType()
                , bought = price
                , boughtCurrency = market.split("-")[1]
                , paid = amount
                , paidCurrency = market.split("-")[0]
                , rate = rate)
    }

}