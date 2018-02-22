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

package com.stasbar.taxledger.exchanges.bitmarket.models

import com.stasbar.taxledger.Abucoins
import com.stasbar.taxledger.OperationType
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.models.Transactionable
import java.math.BigDecimal
import java.util.*


data class BitmarketTransaction(val id: String,
                                val type: String,
                                val amountCrypto: BigDecimal,
                                val currencyCrypto: String,
                                val amountFiat: BigDecimal,
                                val currencyFiat: String,
                                val rate: BigDecimal,
                                val commission: BigDecimal?,
                                val time: Long) : Transactionable {

    override fun operationType() = when (type) {
        "buy" -> OperationType.BUY
        "sell" -> OperationType.SELL
        "deposit" -> OperationType.DEPOSIT
        "withdraw" -> OperationType.WITHDRAW
        else -> OperationType.UNSUPPORTED

    }

    override fun toTransaction(): Transaction {
        return if (operationType() == OperationType.BUY) toBuyTransaction() else toSellTransaction()
    }

    private fun toBuyTransaction(): Transaction {
        return Transaction(exchangeName = Abucoins.name
                , id = id
                , time = Date(time)
                , operationType = operationType()
                , bought = amountCrypto
                , boughtCurrency = currencyCrypto
                , paid = amountFiat
                , paidCurrency = currencyFiat
                , rate = rate)

    }

    private fun toSellTransaction(): Transaction {

        return Transaction(exchangeName = Abucoins.name
                , id = id
                , time = Date(time)
                , operationType = operationType()
                , bought = amountFiat
                , boughtCurrency = currencyFiat
                , paid = amountCrypto
                , paidCurrency = currencyCrypto
                , rate = rate)

    }
}