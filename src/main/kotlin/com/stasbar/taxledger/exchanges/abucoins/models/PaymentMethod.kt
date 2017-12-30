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

package com.stasbar.taxledger.exchanges.abucoins.models

import java.math.BigDecimal

data class PaymentMethod(
        val id : String,
        val type : String,
        val name : String,
        val currency : String,
        val allow_buy : Boolean,
        val allow_sell : Boolean,
        val allow_deposit : Boolean,
        val allow_withdraw : Boolean,
        val limits : List<Limit>
)

data class Limit(
        val buy : BigDecimal,
        val sell : BigDecimal,
        val deposit : BigDecimal,
        val withdraw : BigDecimal)