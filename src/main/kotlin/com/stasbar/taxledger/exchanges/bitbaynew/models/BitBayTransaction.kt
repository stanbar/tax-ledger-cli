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

package com.stasbar.taxledger.exchanges.bitbaynew.models

import com.stasbar.taxledger.BitBay
import com.stasbar.taxledger.OperationType
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.models.Transactionable
import java.math.BigDecimal
import java.util.*

class BitBayTransaction(
        var id: UUID,
        var market: String,
        var time: Long,
        var amount: BigDecimal,
        var rate: BigDecimal,
        var initializedBy: String,
        var wasTaker: Boolean,
        var userAction: String
) : Transactionable {

    override fun operationType(): OperationType {
        return if (userAction.toLowerCase() == "buy") OperationType.BUY
        else if (userAction.toLowerCase() == "sell") OperationType.SELL
        else throw IllegalStateException("Unsupported userAction: $userAction")
    }

    override fun toTransaction(): Transaction =
            if (operationType() == OperationType.BUY) toTransaction(bought = amount,
                    boughtCurrency = market.split("-")[0],
                    paid = rate * amount,
                    paidCurrency = market.split("-")[1])
            else toTransaction(bought = rate * amount,
                    boughtCurrency = market.split("-")[1],
                    paid = amount,
                    paidCurrency = market.split("-")[0])


    private fun toTransaction(bought: BigDecimal, boughtCurrency: String, paid: BigDecimal, paidCurrency: String)
            : Transaction =
            Transaction(exchangeName = BitBay.name
                    , id = id.toString()
                    , time = Date(time)
                    , operationType = operationType()
                    , bought = bought
                    , boughtCurrency = boughtCurrency
                    , paid = paid
                    , paidCurrency = paidCurrency
                    , rate = rate)
}
