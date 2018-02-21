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

import com.stasbar.taxledger.*
import com.stasbar.taxledger.completers.TransactionCandidate
import com.stasbar.taxledger.exceptions.IllegalDateRangeArgument
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.models.Transactionable
import com.stasbar.taxledger.translations.Text
import java.text.ParseException
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass


class DateRange {
    var day: Int? = null
        private set
    var month: Int? = null
        private set
    var year: Int? = null
        private set

    fun setToYear(cal: Calendar) {
        this.year = cal.get(Calendar.YEAR)
    }

    fun setToMonth(cal: Calendar) {
        setToYear(cal)
        this.month = cal.get(Calendar.MONTH)
    }

    fun setToDay(cal: Calendar) {
        setToMonth(cal)
        this.day = cal.get(Calendar.DAY_OF_MONTH)
    }

    fun isInRange(it: Transaction): Boolean {
        if (day != null && day != it.time.toCalendar().get(Calendar.DAY_OF_MONTH))
            return false
        if (month != null && month != it.time.toCalendar().get(Calendar.MONTH))
            return false
        if (year != null && year != it.time.toCalendar().get(Calendar.YEAR))
            return false
        return true
    }

    fun isBefore(it: Transaction): Boolean {
        if (year != null && year!! < it.time.toCalendar().get(Calendar.YEAR))
            return false
        if (month != null && month!! < it.time.toCalendar().get(Calendar.MONTH))
            return false
        if (day != null && day!! < it.time.toCalendar().get(Calendar.DAY_OF_MONTH))
            return false
        return true
    }

    fun isAfter(it: Transaction): Boolean {
        if (year != null && year!! > it.time.toCalendar().get(Calendar.YEAR))
            return false
        if (month != null && month!! > it.time.toCalendar().get(Calendar.MONTH))
            return false
        if (day != null && day!! > it.time.toCalendar().get(Calendar.DAY_OF_MONTH))
            return false
        return true
    }
}


class TransactionsOptions(
        val dateRange: DateRange = DateRange(),
        val dateAfter: DateRange = DateRange(),
        val dateBefore: DateRange = DateRange(),
        var oneExchangeOnly: Exchange<out ExchangeApi<Transactionable, Transactionable>>? = null,
        var fileName: StringBuilder = StringBuilder(),
        var reverse: Boolean = false,
        var showNonEssential: Boolean = false,
        var showNonFiat: Boolean = false) {

    companion object {

        val arguments: Set<TransactionCandidate> = setOf(
                TransactionCandidate("--date", "Limit results to date range eg. --date 2017"),
                TransactionCandidate("--before", "Limit results to before date eg. --before 03.2017"),
                TransactionCandidate("--after", "Limit results to before date eg. --after 01.02.2016"),
                TransactionCandidate("--reverse", "Reverse transactions order"),
                TransactionCandidate("--onlyBB", "Use only Bitbay"),
                TransactionCandidate("--onlyAbu", "Use only Abucoins"),
                TransactionCandidate("--showNonEssential", "Show operations like deposit and withdraw"),
                TransactionCandidate("--showNonFiat", "Show crypto-crypto transactions"),
                TransactionCandidate("--all", "Show both -nonEssential and -nonFiat"))

        fun parse(args: ArrayDeque<String>, exchanges: Set<KClass<out Exchange<out ExchangeApi<Transactionable, Transactionable>>>>): TransactionsOptions {

            val option = TransactionsOptions()
            while (args.peekFirst() != null && args.peekFirst().contains("-")) {

                val argument = args.pollFirst()
                        .dropWhile { it == '-' }
                        .toLowerCase()
                option.fileName.append(argument)

                if (tryToParseOneExchange(option, argument, exchanges))
                    continue

                if (tryToParseImplicitDate(option.dateRange, argument))
                    continue

                if (tryToParseOneSideBoundDateRange(args, option.dateBefore, argument,
                                "before", "przed"))
                    continue

                if (tryToParseOneSideBoundDateRange(args, option.dateAfter, argument,
                                "after", "po"))
                    continue

                if (tryToParseOneSideBoundDateRange(args, option.dateRange, argument,
                                "dateRange", "date", "range", "data"))
                    continue

                when (argument.toLowerCase()) {
                    "showNonFiat".toLowerCase(),
                    "showNoneFiat".toLowerCase(),
                    "showNoFiat".toLowerCase() -> option.showNonFiat = true

                    "showNonEssential".toLowerCase(),
                    "showNoneEssential".toLowerCase(),
                    "showNoEssential".toLowerCase() -> option.showNonEssential = true

                    "all" -> {
                        option.showNonEssential = true
                        option.showNonFiat = true
                    }
                    "reverse" -> option.reverse = true

                    else -> Logger.err(getString(Text.Exceptions.INVALID_ARG).format(argument))
                }
            }
            return option
        }

        private fun tryToParseOneExchange(option: TransactionsOptions, argument: String, exchanges: Set<KClass<out Exchange<out ExchangeApi<Transactionable, Transactionable>>>>): Boolean {
            if (!argument.contains("only") || argument.length <= "only".length)
                return false
            val exchange = exchanges.firstOrNull {
                it.objectInstance!!.isNameOf(argument.substring("only".length))
            }
            if (exchange == null)
                return false
            option.oneExchangeOnly = exchange.objectInstance
            return true
        }

        private fun tryToParseOneSideBoundDateRange(args: ArrayDeque<String>, sideDateRange: DateRange, argument: String?, vararg synonyms: String): Boolean {
            val nextArgument = args.peekFirst()
            return if (argument in synonyms && nextArgument != null && nextArgument.isNotBlank()) {
                val success = tryToParseDateRange(sideDateRange, nextArgument)
                if (success) args.pop()
                success
            } else false
        }

        private fun tryToParseDateRange(dateRange: DateRange, argument: String): Boolean {
            var success = isLikelyToBeDateFormat(argument) && tryToParseExplicitDate(dateRange, argument)
            if (!success)
                success = tryToParseImplicitDate(dateRange, argument)
            return success
        }


        private fun isLikelyToBeDateFormat(candidate: String) = candidate.matches(Regex("([0-9]{1,2}).([0-9]{1,2}).([0-9]{4})"))
                || candidate.matches(Regex("([0-9]{1,2}).([0-9]{4})"))
                || candidate.matches(Regex("([0-9]{4})"))


        private fun tryToParseImplicitDate(dateRange: DateRange, argument: String): Boolean {
            val calendar = Date().toCalendar()
            when (argument) {


                "today".toLowerCase() -> {
                    dateRange.setToDay(calendar)
                }
                "yesterday".toLowerCase() -> {
                    calendar.roll(Calendar.DAY_OF_MONTH, -1)
                    dateRange.setToDay(calendar)
                }
                "thisMonth".toLowerCase() -> {
                    dateRange.setToMonth(calendar)
                }
                "lastMonth".toLowerCase(),
                "prevMonth".toLowerCase() -> {
                    calendar.roll(Calendar.MONTH, -1)
                    dateRange.setToMonth(calendar)
                }
                "thisYear".toLowerCase() -> {
                    dateRange.setToYear(calendar)
                }
                "lastYear".toLowerCase(),
                "prevYear".toLowerCase() -> {
                    calendar.roll(Calendar.YEAR, -1)
                    dateRange.setToYear(calendar)
                }
                else -> return false
            }
            return true
        }


        private fun tryToParseExplicitDate(dateRange: DateRange, argument: String): Boolean {
            val parsePosition = ParsePosition(0)
            try {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy")

                val calendar = dateFormat.parse(argument, parsePosition)
                        ?: throw IllegalDateRangeArgument(argument, parsePosition.errorIndex)
                dateRange.setToDay(calendar.toCalendar())

            } catch (e: Exception) {
                try {
                    val dateFormat = SimpleDateFormat("MM.yyyy")
                    val calendar = dateFormat.parse(argument, parsePosition)
                            ?: throw IllegalDateRangeArgument(argument, parsePosition.errorIndex)
                    dateRange.setToMonth(calendar.toCalendar())
                } catch (e: Exception) {
                    try {
                        val dateFormat = SimpleDateFormat("yyyy")
                        dateFormat.isLenient = false
                        val calendar = dateFormat.parse(argument, parsePosition)
                                ?: throw IllegalDateRangeArgument(argument, parsePosition.errorIndex)
                        dateRange.setToYear(calendar.toCalendar())
                    } catch (e: ParseException) {
                        Logger.err(e.message)
                        return false
                    } catch (e: IllegalDateRangeArgument) {
                        //if(e.arg.length >=2 && e.arg[1].isDigit())
                        Logger.err(e.message)
                        return false
                    }
                }
            }
            return true
        }

    }

    fun isInRange(it: Transaction): Boolean = dateRange.isInRange(it) && dateBefore.isBefore(it) && dateAfter.isAfter(it)

}
