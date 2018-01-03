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

package com.stasbar.taxledger.writers

import com.stasbar.taxledger.OperationType
import com.stasbar.taxledger.getString
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.translations.Text
import java.math.BigDecimal

abstract class OutputWriter {
    val essentialOperation = arrayOf(OperationType.BUY, OperationType.SELL, OperationType.FEE)
    val headRow = listOf(getString(Text.EXCHANGE),
            getString(Text.TYPE),
            getString(Text.DATE),
            getString(Text.RATE), "",
            getString(Text.GET), "",
            getString(Text.PAID), "")

    fun getExpense(transactions: Collection<Transaction>): BigDecimal {
        var expense: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.paidCurrency.toUpperCase() == "PLN" && it.operationType == OperationType.BUY }
                .forEach { expense += it.paid }
        return expense
    }

    fun getGrossIncome(transactions: Collection<Transaction>): BigDecimal {
        var grossIncome: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.boughtCurrency.toUpperCase() == "PLN" && it.operationType == OperationType.SELL }
                .forEach { grossIncome += it.bought }
        return grossIncome
    }

    fun getFees(transactions: Collection<Transaction>): BigDecimal {
        var fees: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.operationType == OperationType.FEE && it.paidCurrency.toUpperCase() == "PLN" }
                .forEach { fees += it.paid }
        return fees
    }

    fun getWithdraws(transactions: Collection<Transaction>): BigDecimal {
        var withdraws: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.operationType == OperationType.WITHDRAW && it.paidCurrency.toUpperCase() == "PLN" }
                .forEach { withdraws += it.paid }
        return withdraws
    }

    fun getDeposits(transactions: Collection<Transaction>): BigDecimal {
        var deposits: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.operationType == OperationType.DEPOSIT && it.boughtCurrency.toUpperCase() == "PLN" }
                .forEach { deposits += it.bought }
        return deposits
    }
}