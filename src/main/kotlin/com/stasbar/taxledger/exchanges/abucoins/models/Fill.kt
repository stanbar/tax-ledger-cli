package com.stasbar.taxledger.exchanges.abucoins.models

import com.stasbar.taxledger.Abucoins
import com.stasbar.taxledger.OperationType
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.models.Transactionable
import java.math.BigDecimal
import java.util.*

data class Fill(val trade_id: Int,
                val product_id: String,
                val price: BigDecimal,
                val size: BigDecimal,
                val order_id: String,
                val created_at: Date,
                val liquidity: String,
                val side: String) : Transactionable {
    override fun operationType(): OperationType {
        return if (side == "buy") OperationType.BUY else OperationType.SELL
    }

    override fun toTransaction(): Transaction {
        return if(operationType() == OperationType.BUY) toBuyTransaction() else toSellTransaction()
    }

    private fun toBuyTransaction(): Transaction {
        return Transaction(exchange = Abucoins
                , id = order_id
                , time = created_at
                , operationType = operationType()
                , bought = size
                , boughtCurrency = product_id.split("-")[0]
                , paid = price * size
                , paidCurrency = product_id.split("-")[1]
                , rate = price)

    }

    private fun toSellTransaction(): Transaction {

        return Transaction(exchange = Abucoins
                , id = order_id
                , time = created_at
                , operationType = operationType()
                , bought = size * price
                , boughtCurrency = product_id.split("-")[1]
                , paid = size
                , paidCurrency = product_id.split("-")[0]
                , rate = price)

    }

}