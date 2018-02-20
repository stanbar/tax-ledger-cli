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

import java.math.BigDecimal
import java.util.*

data class Order(
        val status: String,
        val id: String,

        val price: BigDecimal = BigDecimal.ZERO, // How much did I paid or earn in the currency Price of the current order
        val size: String, //*Amount of current product
        val product_id: String, //ex. ZEC-BTC	Product id

        val side: String, //buy, sell	Operation type
        val type: String, //limit, market	Type of market offer
        val time_in_force: String?, //GTC	[optional] GTC, GTT

        val post_only: Boolean, //[optional] create order only if maker - true, false

        val created_at: Date,
        val filled_size: BigDecimal = BigDecimal.ZERO,


        val settled: Boolean

)
//    : Historable {
//    override fun operationType(): OperationType = when (side) {
//        "sell" -> OperationType.SELL
//        "buy" -> OperationType.BUY
//        else -> OperationType.UNKNOWN
//    }
//
//    override fun toTransaction(): BitBayHistory {
//        return if(operationType() == OperationType.BUY) toBuyHistory() else toSellHistory()
//    }
//
//    private fun toBuyHistory(): BitBayHistory {
//        val rate = if(type == "market") price/filled_size else price
//        val paid = if(type == "market") rate * filled_size else filled_size * price
//
//        return BitBayHistory(exchange = Abucoins
//                , id = id
//                , status = status
//                , time = created_at
//                , operationType = operationType()
//                , bought = filled_size
//                , boughtCurrency = product_id.split("-")[0]
//                , paid = paid
//                , paidCurrency = product_id.split("-")[1]
//                , rate = rate
//                , type = type)
//
//    }
//
//    private fun toSellHistory(): BitBayHistory {
//        if(type == "market")
//            AnsiConsole.out.println(toString())
//        val rate = if(type == "market") price/filled_size else price
//        val ammount = if(type == "market") price/filled_size else price
//
//        return BitBayHistory(exchange = Abucoins.SHORTCUT
//                , id = id
//                , status = status
//                , time = created_at
//                , operationType = operationType()
//                , bought = filled_size * price
//                , boughtCurrency = product_id.split("-")[1]
//                , paid = filled_size
//                , paidCurrency = product_id.split("-")[0]
//                , rate = rate
//                , type = type)
//
//    }
//
//}

