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

package com.stasbar.taxledger.exchanges.bitbay.models

import com.stasbar.taxledger.BitBay
import com.stasbar.taxledger.OperationType
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.models.Transactionable
import java.math.BigDecimal
import java.util.*

data class BitBayHistory(val amount: BigDecimal,
                         val operation_type: String,
                         val balance_after: BigDecimal,
                         val currency: String,
                         val time: Date,
                         val comment: String)
    : Transactionable {
    override fun operationType(): OperationType = when (operation_type) {
        "+currency_transaction" -> OperationType.SELL
        "-fee" -> OperationType.FEE
        "-pay_for_currency" -> OperationType.BUY
        "+income" -> OperationType.DEPOSIT
        "-withdraw" -> OperationType.WITHDRAW
        "+withdraw" -> OperationType.DEPOSIT //TODO potentially bug
        "affiliate_income" -> OperationType.AFFILIATE_INCOME
        else -> throw IllegalStateException("BitBay operation type $operation_type is not supported \n" +
                "amount: $amount \n" +
                "currency: $currency \n" +
                "comment: $comment")

    }


    override fun toTransaction(): Transaction {
        return when (operationType()) {
            OperationType.BUY -> toBuyTransaction()
            OperationType.SELL -> toSellTransaction()
            OperationType.FEE -> toBuyTransaction()
            OperationType.WITHDRAW -> toBuyTransaction()
            OperationType.DEPOSIT -> toSellTransaction()
            OperationType.AFFILIATE_INCOME -> toSellTransaction()
        }
    }

    private fun toBuyTransaction(): Transaction {
        return Transaction(exchange = BitBay
                , time = time
                , operationType = operationType()
                , bought = BigDecimal.ZERO
                , boughtCurrency = ""
                , paid = amount.abs()
                , paidCurrency = currency.toUpperCase()
                , rate = BigDecimal.ZERO)
    }

    private fun toSellTransaction(): Transaction {
        return Transaction(exchange = BitBay
                , time = time
                , operationType = operationType()
                , bought = amount.abs()
                , boughtCurrency = currency.toUpperCase()
                , paid = BigDecimal.ZERO
                , paidCurrency = ""
                , rate = BigDecimal.ZERO)
    }
}