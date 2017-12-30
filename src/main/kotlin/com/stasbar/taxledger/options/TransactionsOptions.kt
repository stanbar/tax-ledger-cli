/*
 * Copyright (c) 2017 Stanislaw stasbar Baranski
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
import java.util.*


enum class DateRange {
    ALL {
        override fun isInRange(it: Transaction): Boolean = true
    },
    THIS_WEEK {
        override fun isInRange(it: Transaction): Boolean {
            val calendar = it.time.toCalendar()
            return calendar.get(Calendar.WEEK_OF_YEAR) == Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
                    && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
        }
    }
    ,
    PREV_WEEK {
        override fun isInRange(it: Transaction): Boolean {
            val shiftedTime = Calendar.getInstance()
            shiftedTime.roll(Calendar.WEEK_OF_YEAR, -1)
            val calendar = it.time.toCalendar()
            return calendar.get(Calendar.WEEK_OF_YEAR) == shiftedTime.get(Calendar.WEEK_OF_YEAR)
                    && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
        }
    },

    THIS_MONTH {
        override fun isInRange(it: Transaction): Boolean {
            val calendar = it.time.toCalendar()
            return calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)
                    && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
        }
    }
    ,
    PREV_MONTH {
        override fun isInRange(it: Transaction): Boolean {
            val calendar = it.time.toCalendar()
            val shiftedTime = Calendar.getInstance()
            shiftedTime.roll(Calendar.MONTH, -1)
            return calendar.get(Calendar.MONTH) == shiftedTime.get(Calendar.MONTH)
                    && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
        }
    },
    THIS_YEAR {
        override fun isInRange(it: Transaction): Boolean {
            val calendar = it.time.toCalendar()
            return calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
        }
    },
    PREV_YEAR {
        override fun isInRange(it: Transaction): Boolean {
            val calendar = it.time.toCalendar()
            val shiftedTime = Calendar.getInstance()
            shiftedTime.roll(Calendar.YEAR, -1)
            return calendar.get(Calendar.YEAR) == shiftedTime.get(Calendar.YEAR)
        }
    };

    fun Date.toCalendar() : Calendar{
        val cal = Calendar.getInstance()
        cal.time = this
        return cal
    }
    abstract fun isInRange(it: Transaction): Boolean
}

class TransactionsOptions(var dateRange: DateRange = DateRange.ALL,
                          var reverse: Boolean = false,
                          var oneExchangeOnly: Exchange<ExchangeApi>? = null,
                          var fileName : StringBuilder = StringBuilder()) {
}
