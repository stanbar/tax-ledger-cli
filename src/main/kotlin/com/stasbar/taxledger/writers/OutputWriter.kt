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
import java.math.RoundingMode

abstract class OutputWriter(val transactions: Collection<Transaction>) {

    val essentialOperation = arrayOf(OperationType.BUY, OperationType.SELL, OperationType.FEE
            , OperationType.AFFILIATE_INCOME)

    val headRow = listOf(getString(Text.EXCHANGE),
            getString(Text.TYPE),
            getString(Text.DATE),
            getString(Text.RATE), "",
            getString(Text.GET), "",
            getString(Text.PAID), "")

    fun getExpense(currency: String = "PLN"): BigDecimal {
        var expense: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.paidCurrency.toUpperCase() == currency && it.operationType == OperationType.BUY }
                .forEach { expense += it.paid }
        return expense.setScale(2, RoundingMode.DOWN)
    }

    fun getGrossIncome(currency: String = "PLN"): BigDecimal {
        var grossIncome: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.boughtCurrency.toUpperCase() == currency && it.operationType == OperationType.SELL }
                .forEach { grossIncome += it.bought }
        return grossIncome.setScale(2, RoundingMode.DOWN)
    }

    fun getFees(currency: String = "PLN"): BigDecimal {
        var fees: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.operationType == OperationType.FEE && it.paidCurrency.toUpperCase() == currency }
                .forEach { fees += it.paid }
        return fees.setScale(2, RoundingMode.DOWN)
    }

    fun getNetIncome(currency: String): BigDecimal {
        val netIncome = getGrossIncome(currency) - getExpense(currency) - getFees(currency)
        return netIncome.setScale(2, RoundingMode.DOWN)
    }

    fun getWithdraws(currency: String = "PLN"): BigDecimal {
        var withdraws: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.operationType == OperationType.WITHDRAW && it.paidCurrency.toUpperCase() == currency }
                .forEach { withdraws += it.paid }
        return withdraws.setScale(2, RoundingMode.DOWN)
    }

    fun getDeposits(currency: String = "PLN"): BigDecimal {
        var deposits: BigDecimal = BigDecimal.ZERO
        transactions.filter { it.operationType == OperationType.DEPOSIT && it.boughtCurrency.toUpperCase() == currency }
                .forEach { deposits += it.bought }
        return deposits.setScale(2, RoundingMode.DOWN)
    }


}