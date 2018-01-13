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

import com.stasbar.taxledger.Exchange
import com.stasbar.taxledger.ExchangeApi
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.toCalendar
import java.io.File
import java.util.*


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
        var reverse: Boolean = false,
        var oldBitBayHistory: File? = null,
        var oneExchangeOnly: Exchange<out ExchangeApi>? = null,
        var fileName: StringBuilder = StringBuilder(),
        var showNonEssential: Boolean = false,
        var showNonFiat: Boolean = false) {
    fun isInRange(it: Transaction): Boolean = dateRange.isInRange(it) && dateBefore.isBefore(it) && dateAfter.isAfter(it)

}
