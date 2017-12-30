package com.stasbar.taxledger.exchanges.bitmarket.models

import com.stasbar.taxledger.Abucoins
import com.stasbar.taxledger.OperationType
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.models.Transactionable
import java.math.BigDecimal
import java.util.*

class BitMarketTransaction(val total : Int,
                           val start : Int,
                           val count : Int,
                           val results : List<Trade>) {

}


data class Trade(val id:String,
                 val type : String,
                 val amountCrypto : BigDecimal,
                 val currencyCrypto : String,
                 val amountFiat : BigDecimal,
                 val currencyFiat : String,
                 val rate : BigDecimal,
                 val time : Date) : Transactionable  {

    override fun operationType(): OperationType {
        return if (type == "buy") OperationType.BUY else OperationType.SELL
    }

    override fun toTransaction(): Transaction {
        return if(operationType() == OperationType.BUY) toBuyTransaction() else toSellTransaction()
    }

    private fun toBuyTransaction(): Transaction {
        return Transaction(exchange = Abucoins
                , id = id
                , time = time
                , operationType = operationType()
                , bought = amountCrypto
                , boughtCurrency = currencyCrypto
                , paid = amountFiat
                , paidCurrency = currencyFiat
                , rate = rate)

    }

    private fun toSellTransaction(): Transaction {

        return Transaction(exchange = Abucoins
                , id = id
                , time = time
                , operationType = operationType()
                , bought = amountFiat
                , boughtCurrency = currencyFiat
                , paid = amountCrypto
                , paidCurrency = currencyCrypto
                , rate = rate)

    }
}