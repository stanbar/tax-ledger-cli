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

package com.stasbar.taxledger.exchanges.coinroom.models

import com.stasbar.taxledger.Coinroom
import com.stasbar.taxledger.OperationType
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.models.Transactionable
import java.math.BigDecimal
import java.util.*

data class CoinroomOrder(val id: Int,
                         val realCurrency: String,
                         val cryptoCurrency: String,
                         val amount: BigDecimal,
                         val amountLeft: BigDecimal,
                         val rate: BigDecimal,
                         val type: BigDecimal,
                         val status: String,
                         val instantClose: Int,
                         val creationTime: Date)
    : Transactionable {
    override fun operationType(): OperationType {
        return when (status) {
            "withdrawn" -> OperationType.WITHDRAW
            else -> OperationType.SELL //TODO fix
        }
    }

    override fun toTransaction(): Transaction {
        return Transaction(exchange = Coinroom
                , time = creationTime
                , operationType = operationType()
                , bought = BigDecimal.ZERO
                , boughtCurrency = ""
                , paid = amount.abs()
                , paidCurrency = cryptoCurrency.toUpperCase()
                , rate = BigDecimal.ZERO)
    }


}