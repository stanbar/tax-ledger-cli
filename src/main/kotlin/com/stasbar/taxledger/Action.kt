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

import com.stasbar.taxledger.translations.Text.Actions.*


enum class Action(val title: String, val symbol: Int, val description: String) {
    TRANSACTIONS(TRANSACTIONS_TITLE, 0x1F4D6, TRANSACTIONS_DESC),
    OPEN(OPEN_FOLDER_TITLE, 0x1F4C2, OPEN_FOLDER_DESC),
    EXCHANGES(EXCHANGES_TITLE, 0x1F511, EXCHANGES_DESC),
    DONATE(DONATE_TITLE, 0x1F44D, DONATE_DESC),
    CONTACT(CONTACT_TITLE, 0x1F4E7, CONTACT_DESC),
    VERSION(VERSION_TITLE, 0x1F680, VERSION_DESC),
    EXIT(EXIT_TITLE, 0x1F6AA, EXIT_DESC);

}
