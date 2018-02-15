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

import com.stasbar.taxledger.translations.Text

enum class OperationType(val key: String) {
    BUY(Text.BUY),
    SELL(Text.SELL),
    FEE(Text.FEE),
    DEPOSIT(Text.DEPOSIT),
    WITHDRAW(Text.WITHDRAW),
    TRANSFER(Text.TRANSFER),
    AFFILIATE_INCOME(Text.AFFILIATE_INCOME),
    CARD_WITHDRAW(Text.CARD_WITHDRAW),
    CANCEL_CARD_WITHDRAW(Text.CANCEL_CARD_WITHDRAW),
    CARD_ORDER_FEE(Text.CARD_ORDER_FEE),
    UNSUPPORTED(Text.UNSUPPORTED);

    override fun toString(): String {
        return getString(key)
    }

}