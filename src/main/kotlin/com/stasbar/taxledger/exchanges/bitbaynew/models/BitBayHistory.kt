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


data class Change(val total: BigDecimal,
                  val locked: BigDecimal,
                  val available: BigDecimal)


data class Balance(val id: String,
                   val name: String,
                   val userId: String,
                   val type: String,
                   val currency: String) {
    override fun toString(): String {
        return "name: $name, currency: $currency"
    }
}

data class BitBayHistory(val balance: Balance,
                         val change: Change,
                         val fundsBefore: Change,
                         val fundsAfter: Change,
                         val time: Long,
                         val historyId: String,
                         val value: BigDecimal,
                         val type: String,
                         val detailId: String) : Transactionable {
    override fun toTransaction(): Transaction {
        return when (operationType()) {
            OperationType.FEE -> Transaction(exchangeName = BitBay.name,
                    time = Date(time),
                    operationType = operationType(),
                    bought = BigDecimal.ZERO,
                    boughtCurrency = "",
                    paid = value.abs(),
                    paidCurrency = balance.currency)
            OperationType.DEPOSIT -> Transaction(exchangeName = BitBay.name,
                    time = Date(time),
                    operationType = operationType(),
                    bought = value.abs(),
                    boughtCurrency = balance.currency,
                    paid = BigDecimal.ZERO,
                    paidCurrency = "")
            OperationType.WITHDRAW -> Transaction(exchangeName = BitBay.name,
                    time = Date(time),
                    operationType = operationType(),
                    bought = BigDecimal.ZERO,
                    boughtCurrency = "",
                    paid = value.abs(),
                    paidCurrency = balance.currency)
            else -> throw IllegalStateException("NewBitBay operation type $type is not supported")
        }

    }


    override fun operationType() = when (type) {
        "FUNDS_MIGRATION" -> OperationType.DEPOSIT
        "TRANSACTION_PRE_LOCKING" -> OperationType.UNSUPPORTED
        "TRANSACTION_POST_OUTCOME" -> OperationType.UNSUPPORTED
        "TRANSACTION_OFFER_ABORTED_RETURN" -> OperationType.UNSUPPORTED
        "TRANSACTION_OFFER_STOP_CLOSED" -> OperationType.UNSUPPORTED
        "TRANSACTION_POST_INCOME" -> OperationType.SELL
        "TRANSACTION_COMMISSION_OUTCOME" -> OperationType.FEE
        "ADD_FUNDS" -> OperationType.DEPOSIT
        "WITHDRAWAL_LOCK_FUNDS" -> OperationType.UNSUPPORTED
        "WITHDRAWAL_SUBTRACT_FUNDS" -> OperationType.WITHDRAW
        "CREATE_BALANCE" -> OperationType.UNSUPPORTED
        "TRANSACTION_OFFER_COMPLETED_RETURN" -> OperationType.UNSUPPORTED
        else -> OperationType.UNSUPPORTED
    }

    override fun toString(): String {
        return "time: ${Date(time)} value: $value type: $type balance: $balance change: $change"
    }
}

enum class BitBayHistoryType {
    FUNDS_MIGRATION, TRANSACTION_PRE_LOCKING, TRANSACTION_OFFER_STOP_CLOSED, TRANSACTION_POST_INCOME, TRANSACTION_POST_OUTCOME,
    TRANSACTION_COMMISSION_OUTCOME, TRANSACTION_OFFER_ABORTED_RETURN, ADD_FUNDS, WITHDRAWAL_LOCK_FUNDS, WITHDRAWAL_SUBTRACT_FUNDS,
    CREATE_BALANCE, TRANSACTION_OFFER_COMPLETED_RETURN
}