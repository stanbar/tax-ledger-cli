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
        return Transaction(exchangeName = Abucoins.name
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

        return Transaction(exchangeName = Abucoins.name
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