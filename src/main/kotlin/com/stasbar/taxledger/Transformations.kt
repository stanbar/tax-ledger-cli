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

package com.stasbar.taxledger

import com.stasbar.taxledger.models.Transaction
import java.math.BigDecimal
import java.util.*

object Transformations{

    fun recalculateWithFifo(rawTransactions: MutableList<Transaction>)
            : ArrayList<Transaction> {
        rawTransactions.sortBy { it.time } // sort by time, just in case we mix our transactions from different exchanges
        val sells = toSellQueue(rawTransactions)
        val buys = toBuysMapQueue(rawTransactions)
        val transactions = ArrayList<Transaction>()
        while (sells.isNotEmpty()) {
            val income = sells.pop()
            val currency = income.paidCurrency
            var remainingBuyAmount = income.paid // Amount of sold currency, that we need to find out the buy source now

            while (remainingBuyAmount > BigDecimal.ZERO
                    && buys[currency] != null
                    && buys[currency]!!.isNotEmpty()) {
                // Searching from the oldest buys of this currency

                val buy = buys[currency]!!.pop() // Take the first (oldest) one

                // Calculate how much will I take from this transaction (may be all or part of, depends of what was smaller)
                val consumeAmount = remainingBuyAmount.min(buy.bought)
                // And enter this amount as expenses

                val paid = consumeAmount/buy.bought * buy.paid
                transactions.add(Transaction(exchangeName = buy.exchangeName,
                        id = buy.id, time = income.time,
                        operationType = buy.operationType,
                        bought = consumeAmount,
                        boughtCurrency = buy.boughtCurrency,
                        paid = paid,
                        paidCurrency = buy.paidCurrency,
                        rate = buy.rate))

                // Subtract remaining amount
                remainingBuyAmount -= consumeAmount
                // And amount that was took from this buy operation
                buy.bought -= consumeAmount

                // If that sell operation was less than the buy operation we push it back to the queue
                if (buy.bought > BigDecimal.ZERO) buys[currency]!!.addFirst(buy)

            }
            // After finding all the expenses we add the income operation (sell)
            transactions.add(income)
        }

        return transactions
    }

    private fun toBuysMapQueue(rawTransactions: MutableList<Transaction>): HashMap<String, ArrayDeque<Transaction>> {
        val buys = HashMap<String, ArrayDeque<Transaction>>()

        //init buys map with empty queues for each currency
        rawTransactions
                .filter { it.operationType == OperationType.BUY } //Take only buy operations
                .distinctBy { it.boughtCurrency } // distinct by currency
                .forEach { buys[it.boughtCurrency] = ArrayDeque() } //init buys map to empty queue

        //Fill buys queue
        rawTransactions.filter { it.operationType == OperationType.BUY }
                .forEach { buys[it.boughtCurrency]!!.add(it) }
        return buys
    }

    private fun toSellQueue(rawTransactions: MutableList<Transaction>): ArrayDeque<Transaction> {
        val sells = ArrayDeque<Transaction>()
        //Fill sells queue
        rawTransactions.filter { it.operationType == OperationType.SELL }
                .forEach { sells.add(it) }
        return sells
    }
}