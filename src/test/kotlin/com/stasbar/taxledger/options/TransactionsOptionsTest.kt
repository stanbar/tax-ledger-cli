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

package com.stasbar.taxledger.options

import com.stasbar.taxledger.Abucoins
import com.stasbar.taxledger.BitBay
import org.junit.Test
import java.util.*
import kotlin.test.assertFalse

internal class TransactionsOptionsTest {
    private val exchanges = setOf(
            BitBay::class,
            Abucoins::class)


    @Test
    fun test_oneExchange() {

        val args = ArrayDeque<String>()
        args.add("--onlybb")
        var transactionsOptions = TransactionsOptions.parse(args, exchanges)
        assert(transactionsOptions.oneExchangeOnly == BitBay)

        args.clear()
        args.add("--onlybitbay")
        transactionsOptions = TransactionsOptions.parse(args, exchanges)
        assert(transactionsOptions.oneExchangeOnly == BitBay)


        args.clear()
        args.add("--onlyabu")
        transactionsOptions = TransactionsOptions.parse(args, exchanges)
        assert(transactionsOptions.oneExchangeOnly == Abucoins)

        args.clear()
        args.add("--onlyabucoins")
        transactionsOptions = TransactionsOptions.parse(args, exchanges)
        assert(transactionsOptions.oneExchangeOnly == Abucoins)

    }

    @Test
    fun test_parseTransactionsDefaults() {

        val args = ArrayDeque<String>()
        val transactionsOptions = TransactionsOptions.parse(args, exchanges)
        assert(transactionsOptions.dateRange.year == null)
        assert(transactionsOptions.dateRange.month == null)
        assert(transactionsOptions.dateRange.day == null)

        assert(transactionsOptions.dateAfter.year == null)
        assert(transactionsOptions.dateAfter.month == null)
        assert(transactionsOptions.dateAfter.day == null)

        assert(transactionsOptions.dateBefore.year == null)
        assert(transactionsOptions.dateBefore.month == null)
        assert(transactionsOptions.dateBefore.day == null)

        assert(transactionsOptions.fileName.toString().isEmpty())
        assertFalse(transactionsOptions.reverse)
        assertFalse(transactionsOptions.showNonEssential)
        assertFalse(transactionsOptions.showNonFiat)


    }

    @Test
    fun test_parseTransactionsImplicitDate() {
        val args = ArrayDeque<String>()
        args.add("--yesterday")
        val transactionsOptionsDay = TransactionsOptions.parse(args, exchanges)
        assert(transactionsOptionsDay.dateRange.year != null)
        assert(transactionsOptionsDay.dateRange.month != null)
        assert(transactionsOptionsDay.dateRange.day != null)

        args.clear()
        args.add("--thisMonth")
        val transactionsOptionsMonth = TransactionsOptions.parse(args, exchanges)
        assert(transactionsOptionsMonth.dateRange.year != null)
        assert(transactionsOptionsMonth.dateRange.month != null)
        assert(transactionsOptionsMonth.dateRange.day == null)

    }

    @Test
    fun test_parseTransactionsDateRange() {
        val args = ArrayDeque<String>()
        args.add("--date")
        args.add("2017")
        val transactionsOptions = TransactionsOptions.parse(args, exchanges)
        assert(transactionsOptions.dateRange.year == 2017)
        assert(transactionsOptions.dateRange.month == null)
        assert(transactionsOptions.dateRange.day == null)
    }

    @Test
    fun test_parseTransactionsOneSideDateRange() {
        val args = ArrayDeque<String>()
        args.add("--after")
        args.add("2017")
        args.add("--before")
        args.add("2017")
        val transactionsOptions = TransactionsOptions.parse(args, exchanges)

        assert(transactionsOptions.dateAfter.year == 2017)
        assert(transactionsOptions.dateAfter.month == null)
        assert(transactionsOptions.dateAfter.day == null)
        assert(transactionsOptions.dateBefore.year == 2017)
        assert(transactionsOptions.dateBefore.month == null)
        assert(transactionsOptions.dateBefore.day == null)
    }


}